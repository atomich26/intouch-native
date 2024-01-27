package com.diegusmich.intouch.providers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.activities.EventActivity
import com.diegusmich.intouch.ui.activities.UserActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date
import kotlin.random.Random

class NotificationProvider : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FIREBASE_MESSAGING", token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("FIREBASE_MESSAGING", message.data.toString())
        val type = when(message.data["type"]){
            "event_deleted" -> NotificationType.EVENT_DELETED
            "event_updated" -> NotificationType.EVENT_UPDATED
            "comment" -> NotificationType.COMMENT
            "friendship_sent" -> NotificationType.FRIENDSHIP_REQUEST_SENT
            "friendship_accepted" -> NotificationType.FRIENDSHIP_REQUEST_ACCEPTED
            else -> null
        }

        type?.let {
            notify(baseContext, type, message.data["actor"].toString(), message.data["targetId"].toString())
        }
    }

    companion object {
        const val LOG_TAG = "FIREBASE_MESSAGING"

        private var notificationID = 0
        private lateinit var notificationManager: NotificationManager

        suspend fun getToken(): String? = withContext(Dispatchers.IO) {
            FirebaseMessaging.getInstance().token.await()
        }

        suspend fun deleteTokenforAuthUser(): Unit = withContext(Dispatchers.IO) {
            FirebaseMessaging.getInstance().deleteToken().await()
        }

        fun createChannel(id : String, name:String, description: String, importance : Int) {
            val channel = NotificationChannel(id, name, importance).apply {
                setDescription(description)
            }
            notificationManager.createNotificationChannel(channel)
        }

        fun notify(ctx: Context, type : NotificationType, actorUsername : String, targetId: String) {
            with(NotificationManagerCompat.from(ctx)) {
                if (ActivityCompat.checkSelfPermission(
                        ctx,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ) return@with

                val resultIntent = Intent(ctx, type.contextActivity).apply {
                    putExtra(type.activityArg, targetId)
                }
                val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(ctx).run {
                    addNextIntentWithParentStack(resultIntent)
                    getPendingIntent(0,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
                }

                val notification = NotificationCompat.Builder(ctx, ctx.getString(type.channelID)).apply {
                    setSmallIcon(type.smallIcon)
                    setContentIntent(resultPendingIntent)
                    setContentTitle(ctx.getString(type.contentTitle))
                    setStyle(NotificationCompat.BigTextStyle().bigText(ctx.getString(type.contentText, actorUsername)))
                    priority = type.priority
                    setAutoCancel(true)
                }.build()

                notify(notificationID++, notification)
            }
        }

        fun build(context: Context) {
            notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }
    enum class NotificationType(
        @StringRes val channelID: Int,
        @StringRes val contentTitle : Int,
        @DrawableRes val smallIcon : Int,
        @StringRes val contentText : Int,
        val priority : Int,
        val contextActivity : Class<*>,
        val activityArg : String
    ){
        FRIENDSHIP_REQUEST_SENT(
            R.string.notification_friendship_id,
            R.string.notification_friendship_title,
            R.drawable.baseline_group_add_24,
            R.string.notification_friendship_sent_content,
            NotificationCompat.PRIORITY_DEFAULT,
            UserActivity::class.java,
            UserActivity.USER_ARG
        ),
        FRIENDSHIP_REQUEST_ACCEPTED(
            R.string.notification_friendship_id,
            R.string.notification_friendship_title,
            R.drawable.baseline_group_24__black,
            R.string.notification_friendship_accepted_content ,
            NotificationCompat.PRIORITY_DEFAULT,
            UserActivity::class.java,
            UserActivity.USER_ARG
        ),
        COMMENT(
            R.string.notification_post_comment_id,
            R.string.notification_post_comment_title,
            R.drawable.baseline_comment_24,
            R.string.notification_post_comment_content,
            NotificationCompat.PRIORITY_DEFAULT,
            UserActivity::class.java,
            UserActivity.USER_ARG
        ),

        EVENT_UPDATED( R.string.notification_event_id,
            R.string.notification_event_updated_title,
            R.drawable.baseline_event_24,
            R.string.notification_event_updated_content,
            NotificationCompat.PRIORITY_HIGH,
            EventActivity::class.java,
            EventActivity.EVENT_ARG),

            EVENT_DELETED( R.string.notification_event_id,
            R.string.notification_event_deleted_title,
                R.drawable.baseline_event_busy_24,
            R.string.notification_event_deleted_content,
            NotificationCompat.PRIORITY_HIGH,
            EventActivity::class.java,
            EventActivity.EVENT_ARG);
    }

    enum class Channel(
        @StringRes val channelID: Int,
        @StringRes val channelName : Int,
        @StringRes val channelDesc : Int,
        val importance : Int){

        FRIENDSHIP(
            R.string.notification_friendship_id,
            R.string.notification_friendship_ch,
            R.string.notification_friendship_desc,
            NotificationManager.IMPORTANCE_DEFAULT
        ),
        COMMENT(
            R.string.notification_post_comment_id,
            R.string.notification_post_comment_ch,
            R.string.notification_post_comment_desc,
            NotificationManager.IMPORTANCE_DEFAULT),
        EVENT(
            R.string.notification_event_id,
            R.string.notification_event_ch,
            R.string.notification_event_desc,
            NotificationManager.IMPORTANCE_DEFAULT);

        fun create(ctx: Context){
            createChannel(
                ctx.getString(channelID),
                ctx.getString(channelName),
                ctx.getString(channelDesc),
                importance)
        }
    }
}