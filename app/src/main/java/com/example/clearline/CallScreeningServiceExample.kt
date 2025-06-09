package com.example.clearline

import android.content.ContentResolver
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.provider.ContactsContract
import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import org.json.JSONArray
import org.json.JSONException

class CallScreeningServiceExample : CallScreeningService() {

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle?.schemeSpecificPart ?: ""
        Log.d("CallScreening", "Incoming call from: $phoneNumber")

        val isBlockingEnabled = isBlockingEnabled()
        if (!isBlockingEnabled) {
            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .setDisallowCall(false)
                    .build()
            )
            Log.d("CallScreening", "Blocking disabled - call allowed: $phoneNumber")
            return
        }

        val isKnown = isNumberInContacts(phoneNumber)
        if (!isKnown) {
            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build()
            )
            logBlockedNumber(phoneNumber)  // ✅ Log the number here
            Log.d("CallScreening", "Blocked unknown call: $phoneNumber")
        } else {
            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .setDisallowCall(false)
                    .build()
            )
            Log.d("CallScreening", "Allowed call: $phoneNumber")
        }
    }

    private fun isNumberInContacts(phoneNumber: String): Boolean {
        if (phoneNumber.isEmpty()) return false

        val contentResolver: ContentResolver = contentResolver
        val lookupUri: Uri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )

        contentResolver.query(
            lookupUri,
            arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME),
            null,
            null,
            null
        )?.use { cursor ->
            return cursor.count > 0
        }

        return false
    }

    private fun isBlockingEnabled(): Boolean {
        val sharedPrefs = getSharedPreferences("CallBlockPrefs", Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean("blocking_enabled", true)
    }

    private fun logBlockedNumber(phoneNumber: String) {
        try {
            val prefs: SharedPreferences = getSharedPreferences("blocked_calls", Context.MODE_PRIVATE)
            val jsonString = prefs.getString("blocked_numbers", "[]")
            val jsonArray = JSONArray(jsonString)

            // Avoid duplicates
            for (i in 0 until jsonArray.length()) {
                if (jsonArray.getString(i) == phoneNumber) {
                    return
                }
            }

            jsonArray.put(phoneNumber)
            prefs.edit().putString("blocked_numbers", jsonArray.toString()).apply()
            Log.d("CallScreening", "Logged blocked number: $phoneNumber")
        } catch (e: JSONException) {
            Log.e("CallScreening", "Error logging blocked number", e)
        }
    }
}
