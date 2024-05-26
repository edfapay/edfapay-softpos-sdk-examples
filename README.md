# EdfaPay SoftPos SDK
## Installation

### 1: Configuration (Important)
**Its is important to add the jipack support and authorization to your project, It's allows the gradle to download the dependency from jitpack.**

If your project build was configured to prefer settings repositories, Place the below maven block to project `settings.gradle`
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()

        // Add below to your project setting.gradle
        maven {
            url "https://jitpack.io"
            credentials { username "jp_i9ed2av1lj1kjnqpgobpeh0e7k" }
        }
    }
}
```

***

If your project build was configured to prefer traditional build.gradle repositories, Place the below maven block to project `build.gradle`
```groovy
buildscript {
  dependencies {
    classpath("com.edfapay.softpos:plugin:0.0.7")
  }
  repositories {
      mavenCentral()

      // Add below to your project setting.gradle
      maven {
          url "https://jitpack.io"
          credentials { username "jp_i9ed2av1lj1kjnqpgobpeh0e7k" }
      }
  }
}
```

### 2: Adding Edfapay plugin  (Important)
It is important to apply edfapay plugin to your app module build.gradle

**Using [plugin DSL](https://docs.gradle.org/current/userguide/plugins.html#sec:plugins_block):**
```groovy
plugins {
  id("com.edfapay.softpos.tools") version "0.0.7"
}
```

***

**Using [legacy plugin application](https://docs.gradle.org/current/userguide/plugins.html#sec:old_plugin_application):**

Add the classpath to the project `build.gradle`
```groovy
buildscript {
  dependencies {
    classpath("com.edfapay.softpos:plugin:0.0.7") // add this line
  }
}

apply(plugin = "com.edfapay.softpos.tools")
```
[Learn how to apply plugins to subprojects](https://docs.gradle.org/current/userguide/plugins.html#sec:subprojects_plugins_dsl)



### 2: Adding dependency (Important)
It is important to add dependency to your project module `build.gradle`
```groovy
dependencies {
    .
    .
    .
    . //add below script
    edfapay{
        softpos{
            install("You Partner Code Here Received from EdfaPay")
        }
    }
}

```


## Usage [(Example)](#example)


#### 1: Import (Important)

```dart
import com.edfapay.paymentcard.EdfaPayPlugin
import com.edfapay.paymentcard.Env
import com.edfapay.paymentcard.model.TransactionType
import com.edfapay.paymentcard.model.TxnParams
```



#### 2: Initialization (Important)
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



#### 3: Setting Theme (Optional)
```dart
EdfaPayPlugin.theme
    .setButtonBackgroundColor("#06E59F")
    .setButtonTextColor("#000000")
    .setHeaderImage(this, R.drawable.logo)
    .setPoweredByImage(this, R.drawable.ogo);
```


#### 4: Pay
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
