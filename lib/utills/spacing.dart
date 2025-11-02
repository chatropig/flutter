import 'dart:ffi';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:sizer/sizer.dart';


class Spacing{

  static SizedBox height(double height){
   return  SizedBox(height: height.sp);
  }

  static SizedBox width(double width){
    return  SizedBox(width: width.sp);
  }

  static EdgeInsets pagePadding(){
    return  EdgeInsets.only(top: 12.sp, bottom: 12.sp + MediaQuery.of(Get.context!).viewPadding.bottom,left: 12.sp,right: 12.sp);
  }

  static double fullWidth(BuildContext context){
    return MediaQuery.of(context).size.width;
  }

}