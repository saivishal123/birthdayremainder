package com.example.birthdayremainder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class UpcomingBirthdaysActivity : AppCompatActivity() {

    private lateinit var upcomingBirthdaysTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_upcoming_birthdays)

        // Toolbar setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        upcomingBirthdaysTextView = findViewById(R.id.upcomingBirthdaysTextView)
        displayUpcomingBirthdays()
    }

    private fun displayUpcomingBirthdays() {
        val sharedPreferences = getSharedPreferences("birthday_prefs", Context.MODE_PRIVATE)
        val birthdayList = sharedPreferences.getStringSet("birthdays", setOf()) ?: setOf()
        upcomingBirthdaysTextView.text = "Upcoming Birthdays:\n" + birthdayList.joinToString("\n")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upcoming_birthdays -> {
                Toast.makeText(this, "You are already on the Upcoming Birthdays page", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
