import 'dart:async';
import 'dart:typed_data';

import 'package:flutter_edfapay_softpos_sdk/models/TxnParams.dart';
import 'package:flutter_edfapay_softpos_sdk/models/sdk_theme.dart';

import 'callback/callbacks.dart';
import 'flutter_edfapay_plugin_platform_channel.dart';

PlatformChannelFlutterEdfapayPlugin _instance = PlatformChannelFlutterEdfapayPlugin();
class EdfaPayPlugin {
  static var theme = SdkTheme(
    buttonBackgroundColor: "#06E59F",
    buttonTextColor: "#000000"
  );

  static Future<String?> getPlatformVersion() {
    return _instance.getPlatformVersion();
  }

  static Future<bool> initiate(String authCode) {
    return _instance.initiate(authCode: authCode);
  }

  static pay( TxnParams params, {
    required onPaymentProcessComplete onPaymentProcessComplete,
    required onScanCardTimeOut onScanCardTimeOut,
    required onServerTimeOut onServerTimeOut,
    required onCancelByUser onCancelByUser,
    required onError onError,
  }){
      _instance.pay(
        params,
        onPaymentProcessComplete: onPaymentProcessComplete,
        onScanCardTimeOut: onScanCardTimeOut,
        onServerTimeOut: onServerTimeOut,
        onCancelByUser: onCancelByUser,
        onError: onError,
      );
  }

  static setButtonBackgroundColor(String hex) => _instance.setButtonBackgroundColor(hex);

  static setButtonTextColor(String hex) => _instance.setButtonTextColor(hex);

  static setHeaderImage(String base64) => _instance.setHeaderImage(base64);

  static setPoweredByImage(String base64) => _instance.setPoweredByImage(base64);

}
