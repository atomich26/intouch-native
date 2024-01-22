package com.diegusmich.intouch.providers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class NotificationProvider : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FIREBASE_MESSAGING", token)
        registerTokenToUser(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FIREBASE_MESSAGING", message.data.getOrDefault("title", "default"))
    }

    override fun onDeletedMessages() {
        super.onDeletedMessages()
    }

    private fun registerTokenToUser(token : String) {
        Firebase.functions.getHttpsCallable("registerMessagingToken").call(mapOf("token" to token)).addOnCompleteListener {
            if(!it.isSuccessful)
                return@addOnCompleteListener
        }
    }

    companion object{
        const val LOG_TAG = "FIREBASE_MESSAGING"

        private lateinit var notificationManager : NotificationManager

        suspend fun getToken() : String? = withContext(Dispatchers.IO){
            FirebaseMessaging.getInstance().token.await()
        }

        suspend fun deleteTokenforAuthUser(): Unit = withContext(Dispatchers.IO) {
            FirebaseMessaging.getInstance().deleteToken().await()
        }

        fun createChannel(name: String, id: String, description: String, importance: Int){
            val channel = NotificationChannel(id, name, importance).apply {
                setDescription(description)
            }

            notificationManager.createNotificationChannel(channel)
        }

        fun build(context: Context){
            notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }
}