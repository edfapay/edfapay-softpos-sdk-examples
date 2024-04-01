import 'dart:ui';

import 'package:fluttertoast/fluttertoast.dart';

toast(String text){
  Fluttertoast.showToast(msg: text);
}

delay(int seconds, VoidCallback callback) {
  Future.delayed(Duration(seconds: seconds), callback);
}