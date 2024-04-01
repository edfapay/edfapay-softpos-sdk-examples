
# react-native-edfapay-softpos-sdk
## Installation

#### 1: Confiiguration (Important)
Its is important to add the jipack support and authorization to your project android module, It's allows the gradle to download the native dependency from jitpack.
<br>**Place the below code snippit to `./android/app/build.gradle` path**
```gradle
repositories.maven{
  url "https://jitpack.io"
  credentials{
      username "jp_i9ed2av1lj1kjnqpgobpeh0e7k"
  } 
}
```
#### 2: Install react-native SDK (Important)
```js
npm install react-native-edfapay-softpos-sdk
```

## Usage [(Example)](#example)


#### 1: Import (Important)

```js
import { Transaction } from 'react-native-edfapay-softpos-sdk';
import * as EdfaPayPlugin from 'react-native-edfapay-softpos-sdk';
```



#### 2: Initialization (Important)
```js
const authCode="You Sdk Login Auth Code"

EdfaPayPlugin.initiate(authCode).then(async (value) => {
  if(value == false){
    // Handle initialization failed
    // inform the developer or user initializing failed
  }else{
    // Allow user to start payment process in next step 
  }  
});
```



#### 3: Setting Theme (Optional)
```js
const logo = "base64 of image" 
EdfaPayPlugin.theme
    .setButtonBackgroundColor("#06E59F")
    .setButtonTextColor("#000000")
    .setPoweredByImage(logo)
    .setHeaderImage(logo)
```


#### 4: Pay
```js
var params = new EdfaPayPlugin.TxnParams("10.00", EdfaPayPlugin.TxnType.PURCHASE)

const onPaymentProcessComplete = (status:boolean, transaction:Transaction) => {
  console.log(`>>> Payment Process Complete`)
  if(status){
    console.log(` >>> [ Success ]`)
    console.log(` >>> [ ${JSON.stringify(transaction)} ]`)
  }else{
    console.log(` >>> [ Failed ]`)
    console.log(` >>> [ ${JSON.stringify(transaction)} ]`)
  }
}

const onServerTimeOut = () => {
  console.log(`>>> Server Timeout`)
  console.log(` >>>The request timeout while performing transaction at backend`)
}

const onScanCardTimeOut = () => {
  console.log(`>>> Scan Card Timeout`)
  console.log(` >>> The scan card timeout, no any card tap on device`)
}

const onCancelByUser = () => {
  console.log(`>>> Canceled By User`)
  console.log(` >>> "User have cancel the scanning/payment process on its own choice`)
}

const onError = (error:Error) => {
  console.error(`>>> Exception`)
  console.error(` >>> "Scanning/Payment process through an exception, Check the console logs`)
  console.error(`  >>> ${error.message}`)
  console.error(`  >>> ${error.cause}`)
  console.error(`  >>> ${error.stack}`)
}

EdfaPayPlugin.pay(
  params,
  onPaymentProcessComplete,
  onServerTimeOut,
  onScanCardTimeOut,
  onCancelByUser,
  onError
)
```



## Example
<details>
  <summary> Click to Expand/Collapes </summary>
  
```js
  
import * as React from 'react';

import { View, Text, Image, Button, StyleSheet, Dimensions, Alert } from 'react-native';

import { Transaction } from 'react-native-edfapay-softpos-sdk';
import * as EdfaPayPlugin from 'react-native-edfapay-softpos-sdk';


const logo = require('../assets/images/edfapay_text_logo.png');
const authCode="You Sdk Login Auth Code"
const amountToPay = "10.000";

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



  function initiateSdk(completion: ((status:boolean) => void)){
  
    EdfaPayPlugin.initiate(authCode).then(async (value) => {
      completion(value);
  
      if(value == false){
        dialog.alert("Error Initializing","Failed to initialize 'EdfaPay SDK'")
        return
      }
  
      const resLogo = await EdfaPayPlugin.setMerchantLogo(logo).catch(console.log)
      const resTheme = await EdfaPayPlugin.setTheme(
        new EdfaPayPlugin.Theme(
          "#06E59F", 
          "#000"
        ).json()
      ).catch(console.log)
      
      if(!resLogo){
        dialog.alert("Error Setting Logo","Failed to set merchant logo")
      }
  
      if(!resTheme){
        dialog.alert("Error Setting Theme","Failed to set merchant Theme")
      }
      
    });
  
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
};



/*
====================================================
Strings for UI
====================================================
*/
  class Dialog{
    alert(title:string, message:string){
        Alert.alert(
            title, message, 
            [
                {
                    text: 'OK', 
                    onPress: () => console.log('OK Pressed'),
                    style: 'cancel'
                }
            ]
        );
    }
    
    confirm(title:string, message:string, positiveCallback:Function, negativeCallback:Function ){
        Alert.alert(
            title, message, 
            [
                {
                    text: 'Yes', 
                    onPress: () => positiveCallback(),
                    style: 'cancel'
                },

                {
                    text: 'No', 
                    onPress: () => negativeCallback(),
                    style: 'cancel'
                }
            ]
        );
    }
}
const dialog = new Dialog();


/*
====================================================
Strings for UI
====================================================
*/
const Strings = {
  sdk: 'SDK',
  version: 'v0.1.2',
  message: "You\'re on your way to enabling your Android App to allow your customers to pay in a very easy and simple way just click the payment button and tap your payment card on NFC enabled Android phone."
};


/*
====================================================
Styles for UI
====================================================
*/
const screen = Dimensions.get('window');
const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
  },
  content: {
    alignItems: 'center',
  },
  logo: {
    width: screen.width/1.5,
    resizeMode: 'contain',
  },
  heading1: {
    fontSize: 65,
    fontWeight: "700",
    color: "#000",
  },
  heading2: {
    fontSize: 30,
    fontWeight: "700",
    color: "#000",
  },
  heading3: {
    marginHorizontal:30,
    fontSize: 13,
    fontWeight: "400",
    color: "#787878",
    marginVertical: 100,
  },
  buttonContainer: {
    position: 'absolute',
    bottom: 20,
    left: 0,
    right: 0,
    marginHorizontal: 20,
    borderRadius: 10,
    overflow: 'hidden',
  },
});

```

</details>

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)