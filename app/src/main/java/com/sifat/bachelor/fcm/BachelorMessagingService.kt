package com.sifat.bachelor.fcm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sifat.bachelor.R
import com.sifat.bachelor.home.HomeActivity
import android.content.Context
import android.media.RingtoneManager

class BachelorMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Check if the message contains data payload
        remoteMessage.data.isNotEmpty().let {
            handleDataMessage(remoteMessage.data)
        }

        // Check if the message contains a notification payload
        remoteMessage.notification?.let {
            showNotification(it)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        // Handle the data payload (for messages received in both foreground & background)
        // Process the data and execute logic based on key-value pairs
        // For example, you could log or update your app's UI, etc.
        Log.d("DataMessage", "Message data payload: $data")
    }

    private fun showNotification(notification: RemoteMessage.Notification) {
        // Create a notification and display it
        val notificationTitle = notification.title ?: "New Message"
        val notificationBody = notification.body ?: "You have a new message."

        val intent = Intent(this, HomeActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            //addFlags(Intent.FLAG_SHOW_WHEN_LOCKED) // Show on lock screen
            //addFlags(Intent.FLAG_TURN_SCREEN_ON) // Turn screen on when notification is clicked
        }
        intent.setPackage(applicationContext.packageName)

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        // Define vibration pattern (vibrate for 100 ms, sleep for 100 ms, repeat 2 times)
        var vibrationPattern = longArrayOf(0, 100, 100, 100) // Start immediately, vibrate for 100ms, sleep for 100ms, repeat

        val notificationBuilder = NotificationCompat.Builder(this, "default")
            .setSmallIcon(R.drawable.ic_chat)
            .setContentTitle(notificationTitle)
            .setContentText(notificationBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(vibrationPattern) // Set vibration pattern
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) // Set sound

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // For Android O and above, create a notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "default", "Bachelor", NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true) // Enable vibration on the channel
                vibrationPattern = vibrationPattern // Set vibration pattern
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null) // Set sound on the channel
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(0, notificationBuilder.build())
    }


    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Handle token refresh logic here (send the new token to your server)
        Log.d("FCMToken", "New token: $token")
    }
}
