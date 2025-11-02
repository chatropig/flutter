import 'package:chatapp/utills/app_colors.dart';
import 'package:chatapp/utills/app_text_style.dart';
import 'package:chatapp/utills/spacing.dart';
import 'package:chatapp/view/pin_input_screen.dart';
import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:sizer/sizer.dart';

import '../controller/payment_method_controller.dart';

class PaymentMethodScreen extends StatelessWidget {
  const PaymentMethodScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: AppBar(
        elevation: 0,
        backgroundColor: Colors.white,
        // backgroundColor: AppColors.primaryColor,
        centerTitle: true,
        iconTheme: IconThemeData(color: AppColors.blackColor),
        title: Text(
          'Payment Verification',
          style: AppTextStyle.titleLarge.copyWith(
            color: AppColors.blackColor,
            fontWeight: FontWeight.w600,
          ),
        ),
        bottom: PreferredSize(
          preferredSize: Size.fromHeight(1.0),
          child: Divider(
            color: Colors.grey, // Or any color you want
            height: 1.0,
            thickness: 1.0,
          ),
        ),
      ),
      body: GetBuilder<PaymentMethodController>(
        init: PaymentMethodController(),
        builder: (controller) {
          return Padding(
            padding: Spacing.pagePadding(),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.start,

              children: [
                Spacing.height(15),
                // Text('Verification Fees:',style: AppTextStyle.titleMedium.copyWith(fontWeight: FontWeight.w600),),
                // Text('₹1',style: AppTextStyle.titleSmall.copyWith(fontWeight: FontWeight.w500,color: AppColors.greenColor),),
                //   Spacing.height(12),
                Text(
                  'Select Payment Method',
                  style: AppTextStyle.titleMedium.copyWith(
                    fontWeight: FontWeight.w600,
                  ),
                ),

                ListView.builder(
                  shrinkWrap: true,
                  physics: NeverScrollableScrollPhysics(),
                  itemCount: controller.methods.length,
                  // padding: EdgeInsets.symmetric(horizontal: 12.sp,vertical: 10.sp),
                  itemBuilder: (context, index) {
                    return GestureDetector(
                      onTap: () {
                        controller.selectedMethod.value =controller.methods[index].id;
                        controller.update();
                      },
                      child: Container(
                        margin: EdgeInsets.symmetric(
                          vertical: 12.sp,
                          horizontal: 5.sp,
                        ),
                        // padding: EdgeInsets.symmetric(horizontal: 10.sp),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(12.sp),
                          color: Colors.white,
                          border: Border.all(color: Colors.black),
                        ),
                        child: Row(
                          mainAxisAlignment: MainAxisAlignment.center,
                          children: [
                            Padding(
                              padding: const EdgeInsets.all(8.0),
                              child: Image.asset(
                                controller.methods[index].image,
                                height: 32.sp,
                                width: 32.sp,
                              ),
                            ),
                            Spacing.width(15),
                            Text(
                              controller.methods[index].name,
                              style: AppTextStyle.titleSmall.copyWith(
                                fontSize: 17.sp,
                              ),
                            ),
                            Spacer(),
                            // Transform.scale(
                            //   scale: 1,
                            //   child:
                      Padding(
                        padding: EdgeInsets.only(right: 12.sp,bottom: 18.sp),
                        child: SizedBox(
                                  height: 24.sp,
                                  width: 24.sp,
                                  child: RadioListTile(value: controller.methods[index].id, groupValue: controller.selectedMethod.value,
                                   tileColor: AppColors.primaryColor,
                                    activeColor: AppColors.primaryColor,
                                    onChanged: (value) {
                                    controller.selectedMethod.value = value??1;
                                    controller.update();
                                  },),
                                ),
                      ),
                            // )
                          ],
                        ),
                      ),
                    );
                  },
                ),
                Spacing.height(15.sp),
                Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  spacing: 5.sp,
                  children: [
                  Icon(Icons.shield_outlined),
                    Text('Secure Payment Gateway')
                  ],
                ),
                Spacer(),
                GestureDetector(
                  onTap: () {
                    Get.to(()=> UpiPinScreen());
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
                      "Pay ₹1",
                      style: AppTextStyle.titleMedium.copyWith(
                        color: Colors.white,
                        fontWeight: FontWeight.w700,
                      ),
                    ),
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }
}
