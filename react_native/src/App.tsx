import * as React from 'react';

import { View, Text, Image, Button, StyleSheet, Dimensions, Alert } from 'react-native';
import { dialog } from './Dialog';
import { Strings } from './Strings';
import { styles } from './Styles';
import * as Icons from './IconsBase64';

import { Transaction } from 'react-native-edfapay-softpos-sdk';
import * as EdfaPayPlugin from 'react-native-edfapay-softpos-sdk';

const logo = require('../assets/images/edfapay_text_logo.png');
const authCode="YUBmbG91Y2kuY29tOjEyMzRAV2VwYXk="
const amountToPay = "01.010";

export default function App() {
  const [initResult, setInitResult] = React.useState<boolean>();

  React.useEffect( () => {
    initiateSdk(setInitResult);
  }, []);

  return (
    <View style={styles.container}>
      <View style={styles.content}>
        <Image
          source={logo}
          style={styles.logo}
        />

        <Text style={styles.heading1}>{Strings.sdk}</Text>
        <Text style={styles.heading2}>{Strings.version}</Text>
        <Text style={[styles.heading3, {textAlign: 'center'}]}>{Strings.message}</Text>
      </View>


      <View style={styles.buttonContainer}>
        <Button color="#06E59F" disabled={!initResult} title={"Pay "+amountToPay} onPress={() => {
          pay((status) => {
            var title = status ? "Success" : "Fail"
            dialog.alert(title, "Check the 'result/response' printed in console")
          })
        }} />
      </View>
    </View>
  );
};



function initiateSdk(completion: ((status:boolean) => void)){

  EdfaPayPlugin.initiate(authCode).then(async (value:boolean) => {
    completion(value);
    if(value == false){
      dialog.alert("Error Initializing","Failed to initialize 'EdfaPay SDK'")
      return
    }
  });



  EdfaPayPlugin.theme
      .setButtonBackgroundColor("#06E59F")
      .setButtonTextColor("#000000")
      .setPoweredByImage(Icons.logo)
      .setHeaderImage(Icons.logo)

}


function pay(completion: ((status:boolean) => void)){
  console.log(`initiate payment with amount: ${amountToPay}`)
  var params = new EdfaPayPlugin.TxnParams(amountToPay)

  const onPaymentProcessComplete = (status:boolean, transaction:Transaction) => {
    dialog.alert("Payment Process Complete", "")
    completion(status)
  }

  const onServerTimeOut = () => {
    dialog.alert("Server Timeout", "The request timeout while performing transaction at backend")
  }

  const onScanCardTimeOut = () => {
    dialog.alert("Scan Card Timeout", "The scan card timeout, no any card tap on device")
  }

  const onCancelByUser = () => {
    dialog.alert("Canceled By User", "User have cancel the scanning/payment process on its own choice")
  }

  const onError = (error:Error) => {
    dialog.alert("Exception", "Scanning/Payment process through an exception, Check the console logs")
    console.error(`>>> ${error.message}`)
    console.error(`>>> ${error.cause}`)
    console.error(`>>> ${error.stack}`)
  }

  EdfaPayPlugin.pay(
    params,
    onPaymentProcessComplete,
    onServerTimeOut,
    onScanCardTimeOut,
    onCancelByUser,
    onError
  )
};


