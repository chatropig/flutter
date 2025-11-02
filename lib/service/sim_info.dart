import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class SimInfo {
  static const MethodChannel _channel = MethodChannel('sim_info');

  /// Returns a list of maps:
  /// [{ "subscriptionId": 1, "carrierName": "Carrier X", "simSlotIndex": 0 }, ...]
  /// On platforms that don't support it (iOS) returns [].
  static Future<List<Map<String, dynamic>>> getSubscriptionIds() async {
    try {
      final dynamic result = await _channel.invokeMethod('getSubscriptionIds');
      if (result == null) return <Map<String, dynamic>>[];

      // Prefer returning a List<Map<String, dynamic>>
      final List list = result as List;
      return list.map<Map<String, dynamic>>((e) {
        final map = Map<String, dynamic>.from(e as Map);
        return map;
      }).toList();
    } on PlatformException catch (e) {
      // Permission missing or other error
      debugPrint('PlatformException: ${e.code} ${e.message}');
      return <Map<String, dynamic>>[];
    } catch (e) {
      debugPrint('Unexpected error: $e');
      return <Map<String, dynamic>>[];
    }
  }

  static Future<void> makeCall({
    required String number,
    required int simSlot,
  }) async {
    try {
      final result = await _channel.invokeMethod("makeCall", {
        "phoneNumber": number,
        "simSlot": simSlot, // 0 for SIM1, 1 for SIM2
      });
      debugPrint(result);
    } on PlatformException catch (e) {
      debugPrint("Error: ${e.message}");
    }
  }
}
