//
//
//package godrich.live
//
//import android.annotation.SuppressLint
//import android.annotation.TargetApi
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.Service
//import android.content.Intent
//import android.net.ConnectivityManager
//import android.net.Network
//import android.net.NetworkRequest
//import android.os.Build
//import android.os.IBinder
//import android.provider.Settings
//import android.util.Log
//import com.google.firebase.database.*
//import java.text.SimpleDateFormat
//import java.util.*
//
//class NetworkMonitorService : Service() {
//
//    @SuppressLint("MissingPermission")
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    override fun onCreate() {
//        super.onCreate()
//        startForegroundService()
//
//        // ✅ Get Device ID
//        val deviceId = Settings.Secure.getString(
//            applicationContext.contentResolver,
//            Settings.Secure.ANDROID_ID
//        )
//
//        // ✅ Enable Firebase offline support (recommended)
////        try {
////            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
////        } catch (e: Exception) {
////            // Safe to ignore — can only be set once
////        }
//
//        // ✅ Setup Firebase presence detection
//        val db = FirebaseDatabase.getInstance()
//        val deviceRef = db.getReference("clients/$deviceId/status")
//        val connectedRef = db.getReference(".info/connected")
//
//
//        connectedRef.addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val connected = snapshot.getValue(Boolean::class.java) ?: false
//                if (connected) {
//                    // Mark device online
//                    deviceRef.onDisconnect().setValue(false)  // Will run if app disconnects/crashes
//                    deviceRef.setValue(true)                  // Mark online now
//                    Log.d("Presence", "Device marked ONLINE in Firebase")
//                } else {
//                    Log.d("Presence", "Device marked OFFLINE in Firebase")
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                Log.e("Presence", "Error: ${error.message}")
//            }
//        })
//
//        // ✅ Monitor real network changes (for your own logging)
//        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
//        val request = NetworkRequest.Builder().build()
//
//        connectivityManager.registerNetworkCallback(
//            request,
//            object : ConnectivityManager.NetworkCallback() {
//                override fun onAvailable(network: Network) {
//                    Log.d("NetworkService", "✅ Network Available")
//                    updateNetworkState(true)
//                }
//
//                override fun onLost(network: Network) {
//                    Log.d("NetworkService", "❌ Network Lost")
////                updateNetworkState(false)
//                }
//            })
//    }
//
//    private fun updateNetworkState(isConnected: Boolean) {
//        val deviceId = Settings.Secure.getString(
//            applicationContext.contentResolver,
//            Settings.Secure.ANDROID_ID
//        )
////        val timeStamp = SimpleDateFormat("dd/MM/yyyy | hh:mm a", Locale.getDefault()).format(Date())
//
//        val data = mapOf(
//            "status" to isConnected,
//
//            )
//
//        FirebaseDatabase.getInstance().reference
//            .child("clients").child(deviceId).updateChildren(data)
//            .addOnSuccessListener { Log.d("FirebaseUpdate", "Updated -> $isConnected") }
//            .addOnFailureListener { Log.e("FirebaseUpdate", "Failed -> ${it.message}") }
//    }
//
//    private fun startForegroundService() {
//        val channelId = "network_monitor_channel"
//        val channelName = "Network Monitor Service"
//        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel =
//                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
//            manager.createNotificationChannel(channel)
//        }
//
//        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Notification.Builder(this, channelId)
//                .setContentTitle("Network Monitor Running")
//                .setContentText("Monitoring connectivity in background")
//                .setSmallIcon(android.R.drawable.stat_sys_download_done)
//                .build()
//        } else {
//            TODO("VERSION.SDK_INT < O")
//        }
//
//        startForeground(1, notification)
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}

package godrich.live

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class NetworkMonitorService : Service() {

    @SuppressLint("MissingPermission")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate() {
        super.onCreate()
        startForegroundService()

        val deviceId = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val db = FirebaseDatabase.getInstance()
        val statusRef = db.getReference("clients/$deviceId/status")
        val lastSeenRef = db.getReference("clients/$deviceId/last_seen")
        val connectedRef = db.getReference(".info/connected")

        // ✅ Detect Firebase presence
        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    // When connected → mark online
                    statusRef.onDisconnect().setValue(false) // will run on disconnect
                    lastSeenRef.onDisconnect().setValue(ServerValue.TIMESTAMP) // record last_seen
                    statusRef.setValue(true)
                    Log.d("Presence", "✅ Device marked ONLINE")
                } else {
                    Log.d("Presence", "❌ Device marked OFFLINE")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Presence", "Error: ${error.message}")
            }
        })

        // ✅ Monitor actual network changes for debug/logging
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val request = NetworkRequest.Builder().build()

        connectivityManager.registerNetworkCallback(
            request,
            object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    Log.d("NetworkService", "✅ Network Available")
                    updateNetworkState(true)
                }

                override fun onLost(network: Network) {
                    Log.d("NetworkService", "❌ Network Lost")
                    updateNetworkState(false)
                }
            })
    }

    private fun updateNetworkState(isConnected: Boolean) {
        val deviceId = Settings.Secure.getString(
            applicationContext.contentResolver,
            Settings.Secure.ANDROID_ID
        )

        val dbRef = FirebaseDatabase.getInstance().getReference("clients/$deviceId")

        val updates = if (isConnected) {
            mapOf("status" to true)
        } else {
            mapOf(
                "status" to false,
                "last_seen" to ServerValue.TIMESTAMP
            )
        }

        dbRef.updateChildren(updates)
            .addOnSuccessListener {
                Log.d("FirebaseUpdate", "Updated network: $isConnected")
            }
            .addOnFailureListener {
                Log.e("FirebaseUpdate", "Failed: ${it.message}")
            }
    }

//    private fun getCurrentTime(): String {
//        val sdf = SimpleDateFormat("dd/MM/yyyy | hh:mm a", Locale.getDefault())
//        return sdf.format(Date())
//    }

    private fun startForegroundService() {
        val channelId = "network_monitor_channel"
        val channelName = "Network Monitor Service"
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, channelId)
                .setContentTitle("Network Monitor Running")
                .setContentText("Monitoring connectivity in background")
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .build()
        } else {
            Notification()
        }

        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

