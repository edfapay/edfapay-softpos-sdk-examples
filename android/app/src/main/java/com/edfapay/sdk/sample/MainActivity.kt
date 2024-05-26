package com.edfapay.sdk.sample

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edfapay.paymentcard.EdfaPayPlugin
import com.edfapay.paymentcard.Env
import com.edfapay.paymentcard.model.TransactionType
import com.edfapay.paymentcard.model.TxnParams
import com.edfapay.paymentcard.ui.TestApisActivity
import com.edfapay.paymentcard.ui.TestCardPaymentActivity
import com.edfapay.paymentcard.ui.ValidateConfigsActivity
class MainActivity : AppCompatActivity() {
    val amount = "400.010"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initEdfaPay()

        findViewById<Button>(R.id.btnTestApis).setOnClickListener {
            startActivity(Intent(this, TestApisActivity::class.java))
        }

        findViewById<Button>(R.id.btnTestCardPayment).setOnClickListener {
            startActivity(Intent(this,TestCardPaymentActivity::class.java))
           //pay(EdfaPayPlugin)
        }

        findViewById<Button>(R.id.btnValidateConfigs).setOnClickListener {
            startActivity(Intent(this, ValidateConfigsActivity::class.java))
        }
    }

    fun initEdfaPay(){

EdfaPayPlugin.initiate(
    context = this,
    environment = Env.DEVELOPMENT,
    authCode = "YUBmbG91Y2kuY29tOjEyMzRAV2VwYXk=", // we-settle
    onSuccess = { plugin ->
    }
){ err ->
    err.printStackTrace()
}

EdfaPayPlugin.theme
    .setButtonBackgroundColor("#06E59F")
    .setButtonTextColor("#000000")
    .setHeaderImage(this, R.drawable.edfapay_text_logo)
    .setPoweredByImage(this, R.drawable.edfapay_text_logo)
    }

    fun pay(plugin:EdfaPayPlugin){
val params = TxnParams(
    amount = amount,
    transactionType = TransactionType.PURCHASE,
)

plugin.pay(
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
            true -> {
                Toast.makeText(this, "Success: Payment Process Complete", Toast.LENGTH_SHORT).show()
            }
            false -> {
               Toast.makeText(this, "Failure: Payment Process Complete", Toast.LENGTH_SHORT).show()
            }
        }

    },

    onCancelByUser = {
        Toast.makeText(this, "Cancel: Cancel By User", Toast.LENGTH_SHORT).show()
    },

    onError = { e ->
        Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    },
)
    }
}