package com.example.clearline

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException

class BlockedNumbersActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blocked_numbers)

        val listView: ListView = findViewById(R.id.blockedListView)
        val emptyView: TextView = findViewById(R.id.emptyView)

        val blockedNumbers = getBlockedNumbers()
        listView.emptyView = emptyView


        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, blockedNumbers)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val number = blockedNumbers[position]
            val options = arrayOf("Call", "Search on Truecaller")

            AlertDialog.Builder(this)
                .setTitle("Choose action for $number")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> callNumber(number)
                        1 -> searchOnTruecaller(number)
                    }
                }
                .show()
        }
        val clearAllButton: Button = findViewById(R.id.clearAllButton)
        clearAllButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Clear All?")
                .setMessage("Are you sure you want to delete all blocked numbers?")
                .setPositiveButton("Yes") { _, _ ->
                    clearAllBlockedNumbers()
                    recreate() // Refresh the activity
                    Toast.makeText(this, "All blocked numbers cleared.", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun getBlockedNumbers(): List<String> {
        val prefs = getSharedPreferences("blocked_calls", Context.MODE_PRIVATE)
        val jsonString = prefs.getString("blocked_numbers", "[]")
        val list = mutableListOf<String>()

        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                list.add(jsonArray.getString(i))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return list
    }

    private fun callNumber(number: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }
    private fun clearAllBlockedNumbers() {
        val prefs = getSharedPreferences("blocked_calls", Context.MODE_PRIVATE)
        prefs.edit().putString("blocked_numbers", "[]").apply()
    }

    private fun searchOnTruecaller(number: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://www.truecaller.com/search/in/$number")
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "No browser found to open Truecaller", Toast.LENGTH_SHORT).show()
        }
    }
}
