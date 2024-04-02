
import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:flutter_edfapay_softpos_sdk/models/TxnParams.dart';
import 'package:flutter_edfapay_softpos_sdk/models/event_channel.dart';
import 'package:flutter_edfapay_softpos_sdk/models/kernel_response.dart';

import 'callback/callbacks.dart';
import 'enums/payment_scheme.dart';

class PlatformChannelFlutterEdfapayPlugin {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('com.edfapay.fplugin.method');

  @visibleForTesting
  final eventChannel = const EventChannel('com.edfapay.fplugin.event');

  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  Future<bool> initiate({required String authCode}) async{
    final res =  await methodChannel.invokeMethod<bool>('initiate', authCode);
    return Future.value(res ?? false);
  }

  pay(TxnParams params, {
    required onPaymentProcessComplete onPaymentProcessComplete,
    required onScanCardTimeOut onScanCardTimeOut,
    required onServerTimeOut onServerTimeOut,
    required onCancelByUser onCancelByUser,
    required onError onError,
  }) {

    methodChannel.invokeMethod('pay', params.toJson()).then((event){

      if(event is Map){
        final e = Event.fromMap(event);
        if(e.event == Event.OnPaymentProcessComplete){
          onPaymentProcessComplete(e.status, e.data.cast());
        }else if(e.event == Event.OnScanCardTimeOut){
          onScanCardTimeOut();
        }else if(e.event == Event.OnServerTimeOut){
          onServerTimeOut();
        }else if(e.event == Event.OnCancelByUser){
          onCancelByUser();
        }
      }

    }).catchError((e){
      onError(e);
    });

  }

  Future<bool?> setButtonBackgroundColor(String hex) {
    return methodChannel.invokeMethod<bool>('setButtonBackgroundColor', hex);
  }

  Future<bool?> setButtonTextColor(String hex) {
    return methodChannel.invokeMethod<bool>('setButtonTextColor', hex);
  }

  Future<bool?> setHeaderImage(String base64) {
    return methodChannel.invokeMethod<bool>('setHeaderImage', base64);
  }

  Future<bool?> setPoweredByImage(String base64) {
    return methodChannel.invokeMethod<bool>('setPoweredByImage', base64);
  }
}
