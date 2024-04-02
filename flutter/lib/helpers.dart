import 'dart:convert';

import 'package:flutter/services.dart';

assetsBytes(String path) async{
  final data = await rootBundle.load(path);
  return data.buffer.asUint8List();
}

assetsBase64(String path) async{
  final bytes = await  assetsBytes(path);
  final b64 = base64Encode(bytes);
  return b64;
}