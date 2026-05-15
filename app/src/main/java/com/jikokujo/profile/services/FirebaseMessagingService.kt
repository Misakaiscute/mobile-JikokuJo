package com.jikokujo.profile.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.res.painterResource
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.jikokujo.MainActivity
import com.jikokujo.R
import com.jikokujo.core.data.repository.UserRepository
import com.jikokujo.core.di.IoDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService: FirebaseMessagingService() {
    @Inject lateinit var userRepository: UserRepository
    @IoDispatcher @Inject lateinit var ioDispatcher: CoroutineDispatcher
    private val scope: CoroutineScope by lazy { CoroutineScope(ioDispatcher) }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "No title"
        val body = remoteMessage.notification?.body ?: "No body"

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "trip_update_channel"

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.jikokujoicon)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.jikokujoicon))
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            channelId,
            "Trip update channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager.createNotificationChannel(channel)
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onNewToken(token: String) {
        scope.launch {
            userRepository.assignFirebaseToken(token = token)
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}