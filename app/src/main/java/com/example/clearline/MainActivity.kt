package com.example.clearline

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.clearline.R
import org.json.JSONArray
import org.json.JSONException
import android.app.AlarmManager
import android.app.PendingIntent
import java.util.*

class MainActivity : AppCompatActivity() {

    private val permissions = arrayOf(
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ANSWER_PHONE_CALLS,
        Manifest.permission.READ_CALL_LOG
    )
    private val PERMISSION_REQUEST_CODE = 101

    private lateinit var blockToggle: Switch
    private lateinit var showBlockedButton: Button

    private val PREFS_NAME = "CallBlockPrefs"
    private val BLOCKING_ENABLED_KEY = "blocking_enabled"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        blockToggle = findViewById(R.id.blockToggle)
        showBlockedButton = findViewById(R.id.button_show_blocked)

        // Load saved toggle state from SharedPreferences
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isBlockingEnabled = sharedPrefs.getBoolean(BLOCKING_ENABLED_KEY, true) // default enabled
        blockToggle.isChecked = isBlockingEnabled

        blockToggle.setOnCheckedChangeListener { _, isChecked ->
            saveBlockingState(isChecked)
            val msg = if (isChecked) "Call blocking enabled" else "Call blocking disabled"
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        showBlockedButton.setOnClickListener {
            showBlockedNumbers()
        }

        if (!hasPermissions()) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE)
        } else {
            Toast.makeText(this, "All permissions granted!", Toast.LENGTH_SHORT).show()
        }



    }

    private fun saveBlockingState(enabled: Boolean) {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putBoolean(BLOCKING_ENABLED_KEY, enabled)
            apply()
        }
    }

    fun isBlockingEnabled(): Boolean {
        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getBoolean(BLOCKING_ENABLED_KEY, true)
    }

    private fun hasPermissions(): Boolean {
        return permissions.all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }


    private fun getBlockedNumbers(): List<String> {
        val prefs = getSharedPreferences("blocked_calls", MODE_PRIVATE)
        val jsonString = prefs.getString("blocked_numbers", "[]")
        val blockedNumbers = mutableListOf<String>()

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                blockedNumbers.add(jsonArray.getString(i))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return blockedNumbers
    }

    private fun showBlockedNumbers() {
        val intent = Intent(this, BlockedNumbersActivity::class.java)
        startActivity(intent)
    }


    private fun saveBlockedNumber(number: String) {
        val prefs = getSharedPreferences("blocked_calls", MODE_PRIVATE)
        val jsonString = prefs.getString("blocked_numbers", "[]")
        val jsonArray = try {
            JSONArray(jsonString)
        } catch (e: JSONException) {
            JSONArray()
        }

        jsonArray.put(number) // Add the new number

        prefs.edit().putString("blocked_numbers", jsonArray.toString()).apply()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Permissions granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied! App may not work properly.", Toast.LENGTH_LONG).show()
            }
        }
    }
}
