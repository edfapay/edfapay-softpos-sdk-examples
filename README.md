# React-Native EdfaPay SoftPos SDK


## Installation
> [!IMPORTANT]
> ### Configure Repository
> Its is important to add the `gradlePluginPortal` and `maven jipack with authorization` repositories to your project level build.gradle, It's allows the gradle to download edfapay plugin from gradlePluginPortal the native dependency from jitpack.
> <br>**Place the below code snippit to `./android/build.gradle` file**
> ```gradle
> allprojects {
>   repositories {
>     maven{
>       url "https://jitpack.io"
>       credentials{
>         username "jp_i9ed2av1lj1kjnqpgobpeh0e7k"
>       }
>     }
>   }
> }
> ```


> [!IMPORTANT]
> ### Adding Edfapay plugin
> It is important to apply edfapay plugin to your app module `./android/app/build.gradle`
>
> **Using [plugin DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):**
> ```groovy
> plugins {
>   id("com.edfapay.softpos.tools") version "0.0.8"
> }
> ```
>
> ***
>
> **Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):**
>
> Add the classpath to the project `./android/build.gradle`
> ```groovy
> buildscript {
>    repositories {
>     maven {
>       url = uri("https://plugins.gradle.org/m2/")
>     }
>   }
>   dependencies {
>     classpath("com.edfapay.softpos:plugin:0.0.7") // add this line
>   }
> }
>
> apply(plugin = "com.edfapay.softpos.tools")
> ```
> [Learn how to apply plugins to subprojects](https://docs.gradle.org/current/userguide/plugins.html#sec:subprojects_plugins_dsl)


> [!IMPORTANT]
> ### Configure Partner Code
> The partner code will be provided by EdfaPay, Developer should set permanent `EDFAPAY_PARTNER` variable to system/user level environment variables in operation system.
>
> _Developer can setup partner code by two ways_
> 1. _Gradle Properties_
> 2. _Environment Variable_
>
> **Setup by Gradle Properties**
>
> At file `~/.gradle/gradle.properties` you need to add property named `EDFAPAY_PARTNER`
> ```
> EDFAPAY_PARTNER=Your_Partner_Code
> ```
> 
> 
> **Setup by Environment Variable**
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
> ### Install react-native SDK
> Run the below command in the project directory
> ```js
> npm install react-native-edfapay-softpos-sdk
> ```

## Usage


### Import

```js
import { Transaction } from 'react-native-edfapay-softpos-sdk';
import * as EdfaPayPlugin from 'react-native-edfapay-softpos-sdk';
```



### Initialization
```js
const authCode="You Sdk Login Auth Code"

EdfaPayPlugin.initiate(authCode, EdfaPayPlugin.Env.UAT).then(async (value) => {
  if(value == false){
    // Handle initialization failed
    // inform the developer or user initializing failed
  }else{
    // Allow user to start payment process in next step 
  }  
});
```



### Setting Theme (Optional)
```js
const logo = "base64 of image" 
EdfaPayPlugin.theme
    .setButtonBackgroundColor("#06E59F")
    .setButtonTextColor("#000000")
    .setPoweredByImage(logo)
    .setHeaderImage(logo)
```


### Pay
```js
var params = new EdfaPayPlugin.TxnParams("10.00", EdfaPayPlugin.TxnType.PURCHASE)

const onPaymentProcessComplete = (status:boolean, code:string, transaction:Transaction) => {
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
