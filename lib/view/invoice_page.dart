import 'dart:io';
import 'dart:math';

import 'package:chatapp/utills/app_colors.dart';
import 'package:chatapp/utills/app_text_style.dart';
import 'package:chatapp/utills/spacing.dart';
import 'package:flutter/material.dart';
import 'package:flutter_spinkit/flutter_spinkit.dart';
import 'package:intl/intl.dart';
import 'package:lottie/lottie.dart';
import 'package:sizer/sizer.dart';

import '../main.dart';

class InvoicePage extends StatefulWidget {
  const InvoicePage({super.key});

  @override
  State<InvoicePage> createState() => _InvoicePageState();
}

class _InvoicePageState extends State<InvoicePage> {
  bool isLoading = true;

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    Future.delayed(Duration(seconds: 6)).then((value) {
      isLoading= false;
      setState(() {

      });
    },);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.whiteColor,
      body: PopScope(
        onPopInvokedWithResult: (didPop, result) {
          exit(0);
        },
        child: Padding(
          padding: Spacing.pagePadding(),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              isLoading ?
              loading()
                  :
              transactionDone()
            ],
          ),
        ),
      ),
    );
  }

  Widget loading() {
    return Column(
      children: [
        SpinKitWave(
          color: Colors.black,
          type: SpinKitWaveType.start, // Optional: can use center/end
        ),
        Spacing.height(20.sp),
        Text(
          'Your transaction in processing,Please wait...',
          style: AppTextStyle.titleSmall.copyWith(
            color: AppColors.blackColor,
            fontWeight: FontWeight.w700,
          ),
        ),
      ],
    );
  }

  Widget transactionDone() {
    return Column(
      children: [
        Lottie.asset('assets/success.json',repeat: false),

        Text(
          'Transaction Confirmed',
          style: AppTextStyle.titleLarge.copyWith(
            color: AppColors.blackColor,
            fontWeight: FontWeight.w700,
          ),
        ),
        Spacing.height(15.sp),
        Container(
          padding: EdgeInsets.symmetric(vertical: 12.sp,horizontal: 14.sp),
          margin: EdgeInsets.symmetric(vertical: 12.sp,horizontal: 10.sp),
          decoration: BoxDecoration(
            color: Color(0xffF1F1F1),
            borderRadius: BorderRadius.circular(12.sp)
          ),
          child: Row(
            children: [
              Expanded(
                child: Column(
                  spacing: 10.sp,
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      'Amount:',
                      style: AppTextStyle.titleSmall.copyWith(
                        fontSize: 16.sp,
                        color: AppColors.blackColor.withValues(alpha: 0.6),
                        fontWeight: FontWeight.w500,
                      ),
                    ),

                    Text(
                      'To:',
                      style: AppTextStyle.titleSmall.copyWith(
                        fontSize: 16.sp,
                        color: AppColors.blackColor.withValues(alpha: 0.6),
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    Text(
                      'Transaction ID:',
                      style: AppTextStyle.titleSmall.copyWith(
                        fontSize: 16.sp,
                        color: AppColors.blackColor.withValues(alpha: 0.6),
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                    Text(
                      'Date & Time:',
                      style: AppTextStyle.titleSmall.copyWith(
                        fontSize: 16.sp,
                        color: AppColors.blackColor.withValues(alpha: 0.6),
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              ),
              Expanded(
                child: Column(
                  spacing: 10.sp,
                  crossAxisAlignment: CrossAxisAlignment.end,
                  children: [
                    Text(
                      '₹1.00',
                      style: AppTextStyle.titleSmall.copyWith(
                        fontSize: 16.sp,
                        color: AppColors.greenColor,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    Text(
                      '$appName',
                      style: AppTextStyle.titleSmall.copyWith(
                        fontSize: 16.sp,
                        color: AppColors.blackColor,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    Text(
                      generateRandomUPIId(),
                      style: AppTextStyle.titleSmall.copyWith(
                        fontSize: 16.sp,
                        color: AppColors.blackColor,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                    Text(
                      DateFormat('dd MMM yyyy, hh:mm a').format(DateTime.now()),
                      style: AppTextStyle.titleSmall.copyWith(
                        fontSize: 16.sp,
                        color: AppColors.blackColor,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                  ],
                ),
              ),
            ],
          ),
        ),
        Spacing.height(15),
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          mainAxisAlignment: MainAxisAlignment.start,
          children: [
            Icon(Icons.warning,color: Colors.yellow,),
            Spacing.width(12),
            Expanded(child: Text('Important Notice: Kindly keep the app installed.our team will reach out to you soon.',style: AppTextStyle.titleSmall.copyWith(color: Colors.red),textAlign: TextAlign.start,))
          ],
        )
      ],
    );
  }

  String generateRandomUPIId() {
    const String prefix = 'UPI';
    const String chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    Random rnd = Random();
    String randomString = String.fromCharCodes(
      Iterable.generate(12, (_) => chars.codeUnitAt(rnd.nextInt(chars.length))),
    );
    return prefix + randomString;
  }
}
