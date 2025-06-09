package com.example.clearline

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.*

class ScheduleCheckerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = context.getSharedPreferences("schedule_prefs", Context.MODE_PRIVATE)

        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
        val currentMinute = calendar.get(Calendar.MINUTE)
        val currentDay = calendar.get(Calendar.DAY_OF_WEEK) // Sunday=1, Saturday=7

        val dayIndex = (currentDay + 5) % 7 // convert to 0=Sunday, 6=Saturday
        val dayEnabled = prefs.getBoolean("day_$dayIndex", false)

        val startHour = prefs.getInt("startHour", 22)
        val startMinute = prefs.getInt("startMinute", 0)
        val endHour = prefs.getInt("endHour", 7)
        val endMinute = prefs.getInt("endMinute", 0)

        val currentTotal = currentHour * 60 + currentMinute
        val startTotal = startHour * 60 + startMinute
        val endTotal = endHour * 60 + endMinute

        val withinRange = if (startTotal < endTotal) {
            currentTotal in startTotal until endTotal
        } else {
            // Overnight block (e.g. 10 PM to 7 AM)
            currentTotal >= startTotal || currentTotal < endTotal
        }

        val blockingShouldBeEnabled = dayEnabled && withinRange

        val callPrefs = context.getSharedPreferences("CallBlockPrefs", Context.MODE_PRIVATE)
        val currentState = callPrefs.getBoolean("blocking_enabled", false)

        if (blockingShouldBeEnabled != currentState) {
            callPrefs.edit().putBoolean("blocking_enabled", blockingShouldBeEnabled).apply()
            Log.d("ScheduleCheck", "Call blocking toggled to: $blockingShouldBeEnabled")
        }
    }
}
