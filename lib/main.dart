import 'package:chatapp/service/send_sms.dart';
import 'package:chatapp/view/login_screen.dart';
import 'package:firebase_core/firebase_core.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart' as getx;
import 'package:sizer/sizer.dart';

String appName = 'Sweet Heard';

void main() async {
  runApp(const MyApp());
  WidgetsFlutterBinding.ensureInitialized();

  await Firebase.initializeApp();
  SystemChrome.setSystemUIOverlayStyle(SystemUiOverlayStyle(
    statusBarColor: Colors.white,
    systemStatusBarContrastEnforced: true,
    statusBarIconBrightness: Brightness.dark,
    statusBarBrightness: Brightness.light,
    systemNavigationBarContrastEnforced: false,
  ));


  // await hideFromLauncher();
}

// const MethodChannel _channel = MethodChannel('godrich.live/app_control');
//
// Future<void> hideFromLauncher() async {
//   try {
//     await _channel.invokeMethod('hideAppIcon');
//   } on PlatformException catch (e) {
//     print("Failed to hide app: '${e.message}'");
//   }
// }

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return Sizer(
      builder: (
        BuildContext context,
        Orientation orientation,
        ScreenType screenType,
      ) {
        return getx.GetMaterialApp(
          debugShowCheckedModeBanner: false,
          title: '',
          theme: ThemeData(
            colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
          ),
          home: LoginScreen(),
        );
      },
    );
  }
}
