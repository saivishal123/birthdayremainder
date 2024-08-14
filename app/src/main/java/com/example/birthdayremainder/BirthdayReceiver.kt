package com.example.birthdayremainder

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

class BirthdayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val name = intent?.getStringExtra("name")

        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(context, "birthday_channel")
            .setSmallIcon(R.drawable.baseline_cake_24)  // Use the appropriate icon resource
            .setContentTitle("Birthday Reminder")
            .setContentText("It's $name's birthday today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(0, notification)
    }
}
