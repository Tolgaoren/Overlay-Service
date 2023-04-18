package com.example.overlayservice.utils

import android.app.*
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.overlayservice.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService: Service() {
    @Inject
    lateinit var windowManager: WindowManager

    @Inject
    lateinit var inflater: LayoutInflater
    private lateinit var overlayView: View
    private val handler = Handler()
    private lateinit var usageStatsManager: UsageStatsManager

        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            return START_STICKY
        }

        override fun onCreate() {
            super.onCreate()

            usageStatsManager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            handler.post(updateTextRunnable)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Channel for OverlayService",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(channel)

                val notification = Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Overlay Service")
                    .setContentText("Overlay Service Running")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build()

                startForeground(1, notification)
            } else {
                startForeground(1, Notification())
            }

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )

            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            overlayView = inflater.inflate(R.layout.overlay_layout, null)

            updatePackageNameTextView()

            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.addView(overlayView, params)

        }

    private val updateTextRunnable = object : Runnable {
        override fun run() {
            updatePackageNameTextView()
            handler.postDelayed(this, 1000) // 1 saniye sonra tekrar çağrılacak
        }
    }
    private fun updatePackageNameTextView() {
        // Get the current top activity
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, System.currentTimeMillis() - 1000, System.currentTimeMillis())
        val topActivity = stats?.maxByOrNull { it.lastTimeUsed }?.packageName

        // Update the package name text view
        val packageNameTextView = overlayView.findViewById<TextView>(R.id.packageNameTextView)
        packageNameTextView.text = topActivity ?: ""
    }


    override fun onDestroy() {
        handler.removeCallbacks(updateTextRunnable)
        super.onDestroy()
        windowManager.removeViewImmediate(overlayView) //Servisi kapattığımız için package name yazısını da kaldırdık.
    }

    companion object {
        const val CHANNEL_ID = "com.example.overlayservice.toren"
    }
}