package com.voidclient.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.voidclient.R
import com.voidclient.native.NativeInterface
import kotlinx.coroutines.*

class ClientService : Service() {
    companion object {
        private const val TAG = "ClientService"
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "VoidClientChannel"
    }

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var isRunning = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "ClientService created")
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            startClient()
            isRunning = true
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "ClientService destroyed")
        stopClient()
        isRunning = false
        serviceScope.cancel()
    }

    private fun startClient() {
        Log.d(TAG, "Starting VoidClient...")

        // Start foreground service with notification
        startForeground(NOTIFICATION_ID, createNotification())

        // Initialize native interface
        serviceScope.launch {
            try {
                NativeInterface.initialize()
                Log.d(TAG, "Native interface initialized")

                // Start main client loop
                startClientLoop()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize client", e)
            }
        }
    }

    private fun stopClient() {
        Log.d(TAG, "Stopping VoidClient...")
        NativeInterface.cleanup()
    }

    private suspend fun startClientLoop() {
        while (isRunning) {
            try {
                // Perform periodic client tasks
                NativeInterface.update()

                // Delay for 16ms (~60 FPS)
                delay(16)
            } catch (e: Exception) {
                Log.e(TAG, "Error in client loop", e)
                delay(1000) // Wait before retrying
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "VoidClient Service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun createNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("VoidClient 2.0")
        .setContentText("Client is running")
        .setSmallIcon(R.drawable.ic_notification)
        .build()
}