package com.example.overlayservice.utils

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.overlayservice.R

class OverlayService: Service() {

        private lateinit var windowManager: WindowManager
        private lateinit var overlayView: View
        override fun onBind(intent: Intent?): IBinder? {
            return null
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            return START_STICKY
        }

        override fun onCreate() {
            super.onCreate()

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

            val packageManager = packageManager
            val packageName = packageName
            val packageInfo = packageManager.getPackageInfo(packageName, 0)

            val packageNameTextView = overlayView.findViewById<TextView>(R.id.packageNameTextView)
            packageNameTextView.text = packageInfo.packageName

            windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.addView(overlayView, params)

        }

        override fun onDestroy() {
            super.onDestroy()
            windowManager.removeViewImmediate(overlayView) //Servisi kapattığımız için package name yazısını da kaldırdık.
        }


}