package com.johnohhh.diybrick

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.nfc.NfcAdapter
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.ContextCompat

object ActionExecutor {

    private var flashlightOn = false

    fun perform(context: Context, action: CardAction) {
        when (action.type) {
            ActionType.OPEN_CAMERA.name -> openCamera(context)
            ActionType.TOGGLE_FLASHLIGHT.name -> toggleFlashlight(context)
            ActionType.BLUETOOTH_TOGGLE.name -> toggleBluetooth()
            ActionType.WIFI_TOGGLE.name -> toggleWifi(context)
            ActionType.HOTSPOT_TOGGLE.name -> openHotspotSettings(context)
            ActionType.DND_TOGGLE.name -> toggleDnd(context)
            ActionType.LAUNCH_MAPS.name -> launchMaps(context)
            ActionType.LAUNCH_MUSIC.name -> launchMusic(context)
            ActionType.SHARE_CONTACT.name -> shareContact(context)
            ActionType.CUSTOM_TEXT.name -> customToast(context)
            else -> Toast.makeText(context, "Unknown action", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openCamera(context: Context) {
        context.startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun toggleFlashlight(context: Context) {
        try {
            val cam = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val id = cam.cameraIdList.firstOrNull() ?: return
            flashlightOn = !flashlightOn
            cam.setTorchMode(id, flashlightOn)
        } catch (e: Exception) {
            Toast.makeText(context, "Flashlight unavailable", Toast.LENGTH_SHORT).show()
        }
    }

    private fun toggleBluetooth() {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        adapter?.let {
            if (it.isEnabled) it.disable() else it.enable()
        }
    }

    @Suppress("DEPRECATION")
    private fun toggleWifi(context: Context) {
        val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifi.isWifiEnabled = !wifi.isWifiEnabled
    }

    private fun openHotspotSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun toggleDnd(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val notifManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as android.app.NotificationManager
            val current = notifManager.currentInterruptionFilter
            val target = if (current == android.app.NotificationManager.INTERRUPTION_FILTER_NONE) {
                android.app.NotificationManager.INTERRUPTION_FILTER_ALL
            } else {
                android.app.NotificationManager.INTERRUPTION_FILTER_NONE
            }
            if (notifManager.isNotificationPolicyAccessGranted) {
                notifManager.setInterruptionFilter(target)
            } else {
                context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }

    private fun launchMaps(context: Context) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

    private fun launchMusic(context: Context) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_APP_MUSIC)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    private fun shareContact(context: Context) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, "Contact me: [your contact info here]")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(Intent.createChooser(intent, "Share Contact"))
    }

    private fun customToast(context: Context) {
        Toast.makeText(context, "DIY Brick card activated!", Toast.LENGTH_LONG).show()
    }
}
