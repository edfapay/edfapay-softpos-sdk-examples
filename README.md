# EdfaPay SoftPos SDK

## Installation

> [!IMPORTANT]
> ### Configure Repository 
> Its is important to add the `gradlePluginPortal` and `maven jipack with authorization` repositories to your project, It's allows the gradle to download edfapay plugin from **gradlePluginPortal** the native dependency from **jitpack**.
>
> If your project build was configured to prefer settings repositories, Place the below maven block to project **`./settings.gradle`**
> ```groovy
> pluginManagement {
>     repositories {
>         
>         // Add below at same
>         gradlePluginPortal()
>     }
> }
>
> dependencyResolutionManagement {
>     repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
>     repositories {
>         mavenCentral()
>
>         // Add below at same
>         maven {
>             url "https://jitpack.io"
>             credentials { username "jp_i9ed2av1lj1kjnqpgobpeh0e7k" }
>         }
>     }
> }
> ```
>
> ***
>
> If your project build was configured to prefer traditional **build.gradle** repositories, Place the below maven block to project **`./build.gradle`**
> ```groovy
> allprojects {
>   repositories {
>
>     // Add below at same
>     gradlePluginPortal()
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
> It is important to apply edfapay plugin to your app module build.gradle
>
> **Using [plugin DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):**
> ```groovy
> plugins {
>   id("com.edfapay.softpos.tools") version "0.0.7"
> }
> ```
>
> ***
>
> **Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):**
>
> Add the classpath to the project `build.gradle`
> ```groovy
> buildscript {
>   dependencies {
>     classpath("com.edfapay.softpos:plugin:0.0.7") // add this line
>   }
> }
>
> apply(plugin = "com.edfapay.softpos.tools")
> ```
> [Learn how to apply plugins to subprojects](https://docs.gradle.org/current/userguide/plugins.html#sec:subprojects_plugins_dsl)


> [!IMPORTANT]
> ### Adding dependency
> **It is important to add dependency to your project module `build.gradle`**
> - Provide your `partner code` to the script below at install method like: `install("xyz...")`
> - If you provide the `null` in install method like: `install(null)`, The plugin will look for the value from environment variable `EDFAPAY_PARTNER` and apply it to the script.
> ```groovy
> dependencies {
>     .
>     .
>     .
>     //add below at same
>     edfapay{
>         softpos{
>             install("You Partner Code Here Received from EdfaPay")
>         }
>     }
> }
> 
> 
> // Also add below at same
> configurations.configureEach {
>     exclude group: "com.github.edfapay.emv", module: "mastercard-debug"
>     exclude group: "com.github.edfapay.emv", module: "discovery-corec-release"
>     exclude group: "com.github.edfapay.emv", module: "discovery-readerc-release"
> }
>
> ```
> **Setting Environment Variable**
>
> The partner code will be provided by EdfaPay, Developer should set permanent `EDFAPAY_PARTNER` variable to system/user level environment variables in operation system.
> 
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





## Usage


### 1: Import

```dart
import com.edfapay.paymentcard.EdfaPayPlugin
import com.edfapay.paymentcard.Env
import com.edfapay.paymentcard.model.TransactionType
import com.edfapay.paymentcard.model.TxnParams
```



### 2: Initialization
```dart
EdfaPayPlugin.initiate(
    context = this,
    environment = Env.DEVELOPMENT,
    authCode = "Your login auth code here received from edfapay",
    onSuccess = { plugin ->
        // Successfully initialized
    }
){ err ->
    err.printStackTrace()
}
```



### 3: Setting Theme (Optional)
```dart
EdfaPayPlugin.theme
    .setButtonBackgroundColor("#06E59F")
    .setButtonTextColor("#000000")
    .setHeaderImage(this, R.drawable.logo)
    .setPoweredByImage(this, R.drawable.ogo);
```


### 4: Pay
```kotlin
val params = TxnParams(
    amount = amount,
    transactionType = TransactionType.PURCHASE,
)

EdfaPayPlugin.pay(
    this, 
    params,
    onRequestTimerEnd = {
        Toast.makeText(this, "Server Request Timeout", Toast.LENGTH_SHORT).show()
    },

    onCardScanTimerEnd = {
        Toast.makeText(this, "Card Scan Timeout", Toast.LENGTH_SHORT).show()
    },

    onPaymentProcessComplete = { status, code, transaction ->
        when (status) {
            true -> Toast.makeText(this, "Success: Payment Process Complete", Toast.LENGTH_SHORT).show()
            false -> Toast.makeText(this, "Failure: Payment Process Complete", Toast.LENGTH_SHORT).show()
        }
    },

    onCancelByUser = {
        Toast.makeText(this, "Cancel: Cancel By User", Toast.LENGTH_SHORT).show()
    },

    onError = { e ->
        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    },
)
```

## License

MIT
