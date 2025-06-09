package com.example.clearline

import android.content.Context
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class ScheduleActivity : AppCompatActivity() {

    private lateinit var daySwitches: List<Switch>
    private lateinit var startTimePicker: TimePicker
    private lateinit var endTimePicker: TimePicker
    private lateinit var saveButton: Button

    private val prefsName = "schedule_prefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)

        startTimePicker = findViewById(R.id.startTimePicker)
        endTimePicker = findViewById(R.id.endTimePicker)
        saveButton = findViewById(R.id.saveButton)

        daySwitches = listOf(
            findViewById(R.id.switchSunday),
            findViewById(R.id.switchMonday),
            findViewById(R.id.switchTuesday),
            findViewById(R.id.switchWednesday),
            findViewById(R.id.switchThursday),
            findViewById(R.id.switchFriday),
            findViewById(R.id.switchSaturday)
        )

        startTimePicker.setIs24HourView(true)
        endTimePicker.setIs24HourView(true)

        saveButton.setOnClickListener {
            saveSchedule()
        }

        loadSchedule()
    }

    private fun saveSchedule() {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        val startHour = startTimePicker.hour
        val startMinute = startTimePicker.minute
        val endHour = endTimePicker.hour
        val endMinute = endTimePicker.minute

        editor.putInt("startHour", startHour)
        editor.putInt("startMinute", startMinute)
        editor.putInt("endHour", endHour)
        editor.putInt("endMinute", endMinute)

        val days = daySwitches.map { it.isChecked }
        for ((index, isChecked) in days.withIndex()) {
            editor.putBoolean("day_$index", isChecked)
        }

        editor.apply()
        Toast.makeText(this, "Schedule saved", Toast.LENGTH_SHORT).show()
    }

    private fun loadSchedule() {
        val prefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE)

        startTimePicker.hour = prefs.getInt("startHour", 22)
        startTimePicker.minute = prefs.getInt("startMinute", 0)
        endTimePicker.hour = prefs.getInt("endHour", 7)
        endTimePicker.minute = prefs.getInt("endMinute", 0)

        for (i in daySwitches.indices) {
            daySwitches[i].isChecked = prefs.getBoolean("day_$i", false)
        }
    }
}
