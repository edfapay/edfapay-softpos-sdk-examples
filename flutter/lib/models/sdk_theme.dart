import 'package:flutter_edfapay_softpos_sdk/flutter_edfapay_softpos_sdk.dart';

class SdkTheme{
  String buttonBackgroundColor;
  String buttonTextColor;
  String headerImage;
  String poweredByImage;


  SdkTheme({this.buttonBackgroundColor = "", this.buttonTextColor = "", this.headerImage = "", this.poweredByImage = ""});

  SdkTheme setButtonBackgroundColor(String hex){
    EdfaPayPlugin.setButtonBackgroundColor(hex);
    return this;
  }

  SdkTheme setButtonTextColor(String hex){
    EdfaPayPlugin.setButtonTextColor(hex);
    return this;
  }

  SdkTheme setHeaderImage(String base64){
    EdfaPayPlugin.setHeaderImage(base64);
    return this;
  }

  SdkTheme setPoweredByImage(String base64){
    EdfaPayPlugin.setPoweredByImage(base64);
    return this;
  }
}