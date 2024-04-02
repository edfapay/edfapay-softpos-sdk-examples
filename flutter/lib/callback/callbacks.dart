import 'dart:ffi';
import 'dart:ui';

import 'package:flutter_edfapay_softpos_sdk/models/kernel_response.dart';


typedef onPaymentProcessComplete = Function(bool status, Map<String, dynamic> result);
typedef onServerTimeOut = VoidCallback;
typedef onCancelByUser = VoidCallback;
typedef onScanCardTimeOut = VoidCallback;
typedef onError = Function(Exception);
