import 'dart:convert';
import 'dart:ffi';

class Event{
  static String OnPaymentProcessComplete = "OnPaymentProcessComplete";
  static String OnServerTimeOut = "OnServerTimeOut";
  static String OnCancelByUser = "OnCancelByUser";
  static String OnScanCardTimeOut = "OnScanCardTimeOut";

  final String event;
  final bool status;
  final Map data;

  Event(this.event,this.status, this.data);

  static Event fromJson(String json){
    final map = jsonDecode(json);
    return Event(
        map["event"],
        map["status"],
        map["data"]
    );
  }
  static Event fromMap(Map map){
    return Event(
        map["event"],
        map["status"],
        map["data"]
    );
  }
}