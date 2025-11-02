import 'package:get/get.dart';



class PaymentMethodController extends GetxController{

  RxList<PaymentMethod> methods = <PaymentMethod>[
    PaymentMethod(id: 1, image: 'assets/gPay.png', name: 'GPay'),
    PaymentMethod(id: 2, image: 'assets/phonePay.png', name: 'Phone Pay'),
    PaymentMethod(id: 3, image: 'assets/paytm.png', name: 'paytm'),
    PaymentMethod(id: 4, image: 'assets/whatsapp.png', name: 'Whatsapp'),
  ].obs;

  RxInt selectedMethod = 1.obs;

}



class PaymentMethod{
  int id;
  String image;
  String name;


  PaymentMethod({required this.id,required this.image,required this.name});
}