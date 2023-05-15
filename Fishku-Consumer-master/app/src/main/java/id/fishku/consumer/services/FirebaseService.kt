package id.fishku.consumer.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import id.fishku.consumer.R
import id.fishku.consumer.core.data.source.local.datastore.LocalData
import id.fishku.consumer.core.domain.model.ChatArgs
import id.fishku.consumer.livechat.ChatRoomActivity
import id.fishku.consumer.livechat.LiveChatFragment
import kotlin.random.Random

class FirebaseService: FirebaseMessagingService() {
    companion object{
        const val CHANNEL_ID = "my_channel"

        var sharedPref: LocalData? = null

        var token: String?
            get() {
                return sharedPref?.getTokenFcm()
            }
            set(value) {
                sharedPref?.setTokenFcm(value ?: "")
            }
    }


    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        token = newToken
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this, ChatRoomActivity::class.java)

        val notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notifyManager)
        }
        val sellerEmail = message.data["sellerEmail"].toString()
        val chatId = message.data["chatId"].toString()
        intent.putExtra(LiveChatFragment.CHAT_KEY, ChatArgs(sellerEmail, chatId))
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["content"])
            .setSmallIcon(R.drawable.ic_fishku)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()
        notifyManager.notify(notificationId, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager){
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName,IMPORTANCE_HIGH).apply {
            description = "Notification description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}