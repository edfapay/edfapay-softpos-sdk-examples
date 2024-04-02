

import 'dart:convert';

import '../enums/TransactionType.dart';

class TxnParams{
  String amount;
  TransactionType transactionType;

  TxnParams({
    required this.amount,
    required this.transactionType,
  });

  String toJson(){
    final map = {
      "amount" : amount,
      "type" : transactionType.index
    };
    return jsonEncode(map);
  }
}