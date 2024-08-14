package com.example.birthdayremainder

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var nameEditText: EditText
    private lateinit var dateTextView: TextView
    private lateinit var submitButton: Button
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        nameEditText = findViewById(R.id.nameEditText)
        dateTextView = findViewById(R.id.dateTextView)
        submitButton = findViewById(R.id.submitButton)

        setSupportActionBar(findViewById(R.id.toolbar))

        createNotificationChannel()

        dateTextView.setOnClickListener {
            showDatePickerDialog()
        }

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString()
            if (name.isNotEmpty()) {
                // Store the birthday
                storeBirthday(name, selectedDate)
                // Schedule the notification
                scheduleBirthdayNotification(selectedDate, name)
                // Show immediate notification
                showImmediateNotification(name)
            } else {
                Log.e("MainActivity", "Name is empty")
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_upcoming_birthdays -> {
                val intent = Intent(this, UpcomingBirthdaysActivity::class.java)
                startActivity(intent)
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

    private fun showDatePickerDialog() {
        val year = selectedDate.get(Calendar.YEAR)
        val month = selectedDate.get(Calendar.MONTH)
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            selectedDate.set(selectedYear, selectedMonth, selectedDay)
            dateTextView.text = "${selectedMonth + 1}/$selectedDay/$selectedYear"
        }, year, month, day)
        datePickerDialog.show()
    }

    private fun scheduleBirthdayNotification(birthday: Calendar, name: String) {
        try {
            val intent = Intent(this, BirthdayReceiver::class.java).apply {
                putExtra("name", name)
            }
            val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, birthday.timeInMillis, pendingIntent)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error scheduling notification", e)
        }
    }

    private fun showImmediateNotification(name: String) {
        try {
            val notificationId = 1
            val notificationManager = NotificationManagerCompat.from(this)
            val notification = NotificationCompat.Builder(this, "birthday_channel")
                .setSmallIcon(R.drawable.baseline_cake_24)  // Use the appropriate icon resource
                .setContentTitle("Birthday Added")
                .setContentText("Birthday reminder for $name has been added.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManager.notify(notificationId, notification)
        } catch (e: Exception) {
            Log.e("MainActivity", "Error showing notification", e)
        }
    }

    private fun storeBirthday(name: String, date: Calendar) {
        try {
            val sharedPreferences = getSharedPreferences("birthday_prefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val birthdayList = sharedPreferences.getStringSet("birthdays", mutableSetOf()) ?: mutableSetOf()
            birthdayList.add("$name: ${date.get(Calendar.MONTH) + 1}/${date.get(Calendar.DAY_OF_MONTH)}/${date.get(Calendar.YEAR)}")
            editor.putStringSet("birthdays", birthdayList)
            editor.apply()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error storing birthday", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "BirthdayChannel"
            val descriptionText = "Channel for birthday notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("birthday_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
