package com.diegusmich.intouch.providers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class NotificationProvider : FirebaseMessagingService() {

    private lateinit var token : String
    private lateinit var notificationManager : NotificationManager

    fun build(context: Context){
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun createChannel(name: String, id: String, description: String, importance: Int){
        val channel = NotificationChannel(id, name, importance).apply {
           setDescription(description)
        }

     notificationManager.createNotificationChannel(channel)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        this.token = token
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }
}