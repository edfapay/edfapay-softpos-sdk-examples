import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_edfapay_softpos_sdk/edfapay_softpos_sdk.dart';
import 'package:flutter_edfapay_softpos_sdk/enums/transaction_type.dart';
import 'package:flutter_edfapay_softpos_sdk/flutter_edfapay_softpos_sdk.dart';
import 'package:flutter_edfapay_softpos_sdk/helpers.dart';
import 'package:flutter_edfapay_softpos_sdk/models/txn_params.dart';
import 'package:flutter_edfapay_softpos_sdk_example/helper_methods.dart';
import 'package:hexcolor/hexcolor.dart';


const authCode = "Your SDK Auth Code";
const logoPath = "assets/images/edfa_logo.png";
const amountToPay = "01.010";

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  var _edfaPluginInitiated = false;

  @override
  void initState() {
    super.initState();
    initiate();
    setTheme();
  }

  @override
  Widget build(BuildContext context) {

    return MaterialApp(
      home: Scaffold(
        body: Padding(
          padding: const EdgeInsets.all(15),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              const SizedBox(height: 20),
              Expanded(
                flex: 2,
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    FractionallySizedBox(
                      widthFactor: 0.3,
                        child: Image.asset(logoPath)
                    ),
                    SizedBox(height: 30),
                    const Text(
                        "SDK",
                        style: TextStyle(fontSize: 65, fontWeight: FontWeight.w700), textAlign: TextAlign.center
                    ),
                    SizedBox(height: 10),
                    const Text(
                        "v0.0.1",
                        style: TextStyle(fontSize: 30, fontWeight: FontWeight.bold), textAlign: TextAlign.center
                    ),
                  ],
                ),
              ),

              const Expanded(
                flex: 1,
                child: Padding(
                  padding: EdgeInsets.all(10),
                  child: Text(
                      "You\'re on your way to enabling your Android App to allow your customers to pay in a very easy and simple way just click the payment button and tap your payment card on NFC enabled Android phone.",
                      style: TextStyle(fontSize: 14, fontWeight: FontWeight.w400, color: Colors.black45),
                      textAlign: TextAlign.center
                  ),
                ),
              ),

              ElevatedButton(
                  onPressed: pay,
                  style: ButtonStyle(backgroundColor: MaterialStatePropertyAll(HexColor("06E59F"))),
                  child: const Text("Pay $amountToPay", style: TextStyle(color: Colors.black))
              ),

              const Padding(
                padding: EdgeInsets.symmetric(horizontal: 10),
                child: Text(
                    "Click on button above to test the card processing with 10.00 SAR",
                    style: TextStyle(fontSize: 14, fontWeight: FontWeight.w400), textAlign: TextAlign.center
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }


  initiate() async{
    EdfaPayPlugin.initiate(authCode: authCode, environment: Env.DEVELOPMENT).then((value){
      setState(() {
        _edfaPluginInitiated = value;
      });
    });

  }

  setTheme() async {
    final logo = await assetsBase64(logoPath);

    EdfaPayPlugin.theme
        .setButtonBackgroundColor("#06E59F")
        .setButtonTextColor("#000000")
        .setPoweredByImage(logo)
        .setHeaderImage(logo);
  }

  pay() async{
    if(!_edfaPluginInitiated){
      toast("Edfapay plugin not initialized.");
      return;
    }

    final params = TxnParams(
        amount: amountToPay,
        transactionType: TransactionType.purchase,
    );

    EdfaPayPlugin.pay(
        params,
        onPaymentProcessComplete: (status, code, result){
          toast("Card Payment Process Completed");
          print('>>> Payment Process Complete');
        },
        onServerTimeOut: (){
          toast("Server Request Timeout");
          print('>>> Server Timeout');
          print(' >>> The request timeout while performing transaction at backend');
        },
        onScanCardTimeOut: (){
          toast("Card Scan Timeout");
          print('>>> Scan Card Timeout');
          print(' >>> The scan card timeout, no any card tap on device');
        },
        onCancelByUser: (){
          toast("Cancel By User");
          print('>>> Canceled By User');
          print(' >>> User have cancel the scanning/payment process on its own choice');
        },
        onError: (Exception error){
          toast(error.toString());
          print('>>> Exception');
          print(' >>> "Scanning/Payment process through an exception, Check the logs');
          print('  >>> ${error.toString()}');
        }
    );
  }
}
