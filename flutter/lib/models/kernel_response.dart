enum  TransactionStatus {
  ONLINE_REQUEST("ONLINE_REQUEST"),
  DECLINED("DECLINED"),
  SELECT_NEXT("SELECT_NEXT"),
  TRY_AGAIN("TRY_AGAIN"),
  ABORTED("ABORTED"),
  ERROR("ERROR"),
  NA("NA");

  final String name;
  const TransactionStatus(this.name);
}


enum  CVMStatus{
  NO_CVM("NO_CVM"),
  ONLINE_PIN("ONLINE_PIN"),
  SIGNATURE("SIGNATURE"),
  NA("NA");

  final String name;
  const CVMStatus(this.name);
}

class KernelResponse{
  String? kernel;
  String? aid;
  TransactionStatus? status;
  CVMStatus? cvmStatus;
  String? sequenceNumber;
  String? track2Data;
  String? icc;
  String? cryptogram;
  String? cryptogramData;
  String? tvr;
  String? ctq;
  String? ttq;
  String? cvm;
  String? cardholderName;
  Map<String, String>? allIccTags;

  isSuccess() => status == TransactionStatus.ONLINE_REQUEST;

  static KernelResponse from(Map map){
    final kernelResponse =  KernelResponse();
    return kernelResponse;
  }

}