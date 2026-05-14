package com.jikokujo.profile.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.messaging.FirebaseMessaging
import com.jikokujo.core.data.repository.UserRepository
import com.jikokujo.core.di.IoDispatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseNotificationTokenUpdater: Service() {
    @Inject lateinit var userRepository: UserRepository
    @IoDispatcher @Inject lateinit var ioDispatcher: CoroutineDispatcher
    private val scope: CoroutineScope by lazy { CoroutineScope(ioDispatcher) }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            scope.launch {
                userRepository.assignFirebaseToken(token = task.result)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        scope.cancel()
    }
}