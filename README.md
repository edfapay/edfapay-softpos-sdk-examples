# EdfaPay SoftPos SDK
## Installation
> [!IMPORTANT]
> ### Configure Repository
> Its is important to add the jipack support and authorization to your project android module, It's allows the gradle to download the native dependency from jitpack.
> <br>**Place the below code snippit to `./android/app/build.gradle` file**
> ```gradle
> repositories.maven{
>   url "https://jitpack.io"
>   credentials{
>       username "jp_i9ed2av1lj1kjnqpgobpeh0e7k"
>   } 
> }
> ```


> [!IMPORTANT]
> ### Configure Partner Code
> The partner code will be provided by EdfaPay, Developer should set permanent `EDFAPAY_PARTNER` variable to system/user level environment variables in operation system.
>
> **Setting Environment Variable**
> <details>
> <summary> MacOS/Linux </summary>
>
> Permanent environment variables should be added to the .bash_profile file:
> 1. Open the .bash_profile file with a text editor of your choice. (create file if not exist)
> 2. Scroll down to the end of the .bash_profile file.
> 3. Copy below text and paste to a new line. (replace `your partner code` with actual value received from `EdfaPay`)
>     - export EDFAPAY_PARTNER=your partner code
> 4. Save changes you made to the .bash_profile file.
> 5. Execute the new .bash_profile by either restarting the machine or running command below:
>       - source ~/.bash-profile
> </details>
> <details>
> <summary> Windows </summary>
>
> 1. Open the link below:
>     - https://phoenixnap.com/kb/windows-set-environment-variable#ftoc-heading-4
> 2. Make sure below:
>     - Variable name should be `EDFAPAY_PARTNER`
>     - Variable value should be `your partner code` received from `EdfaPay`
> </details>


> [!IMPORTANT]
> ### Install flutter-edfapay-softpos-sdk
> ```terminal
> flutter pub add flutter-edfapay-softpos-sdk
> ```
> or add the dependecy in project `pubspec.yaml`
> ```yaml
> dependencies:
>   flutter-edfapay-softpos-sdk: any
> ```

## Usage


### 1: Import

```dart
import 'package:flutter_edfapay_softpos_sdk/edfapay_softpos_sdk.dart';
```



### 2: Initialization
```dart
const authCode="You Sdk Login Auth Code";

EdfaPayPlugin.initiate(
    authCode: authCode,
    environment: Env.PRODUCTION // Check Env.*
).then((value){
    if(value == false){
    // Handle initialization failed
    // inform the developer or user initializing failed
    }else{
    // Allow user to start payment process in next step 
    }
});
```



### 3: Setting Theme (Optional)
```dart
final logo = "base64 of image";
// final logo = await assetsBase64('path to image asset');

EdfaPayPlugin.theme
    .setButtonBackgroundColor("#06E59F")
    .setButtonTextColor("#000000")
    .setPoweredByImage(logo)
    .setHeaderImage(logo);
```

> [!TIP]
> There is an helper method in SDK to convert image asset to base64
> ```dart
> final logo = await assetsBase64('path to image asset');
> ```


### 4: Pay
```dart

final params = TxnParams(
    amount: "10.000",
    transactionType: TransactionType.purchase,
);

EdfaPayPlugin.pay(
    params,
    onPaymentProcessComplete: (status, code, result){
      if(status){
        print(' >>> [ Success ]');
        print(' >>> [ ${jsonEncode(result)} ]');
      }else{
        print(' >>> [ Failed ]');
        print(' >>> [ ${jsonEncode(result)} ]');
      }
    },
    onServerTimeOut: (){
      print('>>> Server Timeout');
      print(' >>> The request timeout while performing transaction at backend');
    },
    onScanCardTimeOut: (){
      print('>>> Scan Card Timeout');
      print(' >>> The scan card timeout, no any card tap on device');
    },
    onCancelByUser: (){
      print('>>> Canceled By User');
      print(' >>> User have cancel the scanning/payment process on its own choice');
    },
    onError: (error){
      print('>>> Exception');
      print(' >>> "Scanning/Payment process through an exception, Check the logs');
      print('  >>> ${error.toString()}');
    }
);
```



## [Example](https://github.com/edfapay/edfapay-softpos-sdk-examples/tree/main/flutter)
<details>
  <summary> Click to Expand/Collapes </summary>

```dart
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_edfapay_softpos_sdk/edfapay_softpos_sdk.dart';

/* add the plugin for below: https://pub.dev/packages/hexcolor */
import 'package:hexcolor/hexcolor.dart';


const authCode = "Auth_Code provided by EdfaPay ";
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
EdfaPayPlugin.initiate(
    authCode: authCode,
    environment: Env.UAT
).then((value){
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
      print('>>> Edfapay plugin not initialized.');
      return;
    }

    final params = TxnParams(
        amount: amountToPay,
        transactionType: TransactionType.purchase,
    );

    EdfaPayPlugin.pay(
        params,
        onPaymentProcessComplete: (status, code, result){
          print('>>> Payment Process Complete');
        },
        onServerTimeOut: (){
          print('>>> Server Timeout');
          print(' >>> The request timeout while performing transaction at backend');
        },
        onScanCardTimeOut: (){
          print('>>> Scan Card Timeout');
          print(' >>> The scan card timeout, no any card tap on device');
        },
        onCancelByUser: (){
          print('>>> Canceled By User');
          print(' >>> User have cancel the scanning/payment process on its own choice');
        },
        onError: (Exception error){
          print('>>> Exception');
          print(' >>> "Scanning/Payment process through an exception, Check the logs');
          print('  >>> ${error.toString()}');
        }
    );
  }
}
```

</details>

## License

MIT

---
