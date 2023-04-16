package com.example.overlayservice.view

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import com.example.overlayservice.R
import com.example.overlayservice.databinding.ActivityMainBinding
import com.example.overlayservice.utils.OverlayReceiver
import com.example.overlayservice.utils.OverlayService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bootReceiver: OverlayReceiver
    private var overlayServiceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bootReceiver = OverlayReceiver()
        val intentFilter = IntentFilter(Intent.ACTION_BOOT_COMPLETED)
        registerReceiver(bootReceiver, intentFilter)

        overlayServiceIntent = Intent(this, OverlayService::class.java)

        binding.apply {
            startServiceButton.setOnClickListener {
                if (!isServiceRunning(this@MainActivity, OverlayService::class.java)) {
                    startOverlayService()
                } else {
                    Toast.makeText(this@MainActivity,"Servis zaten açık!", Toast.LENGTH_SHORT).show()
                }
            }
            stopServiceButton.setOnClickListener {
                if (isServiceRunning(this@MainActivity, OverlayService::class.java)) {
                    stopService(overlayServiceIntent)
                } else {
                    Toast.makeText(this@MainActivity, "Servis zaten durdurulmuş.", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun startOverlayService() {
        if (Settings.canDrawOverlays(this)) {
            startService(overlayServiceIntent)
        } else {
            val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION)
        }
    }

    companion object {
        private const val REQUEST_OVERLAY_PERMISSION = 1000
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_OVERLAY_PERMISSION) {
            if (Settings.canDrawOverlays(this)) {
                startService(overlayServiceIntent)
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun isServiceRunning(context: Context, serviceClass: Class<*>): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val services = activityManager.getRunningServices(Integer.MAX_VALUE)

        for (service in services) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(overlayServiceIntent)
        unregisterReceiver(bootReceiver)
    }

}