package com.edfapay.sdk.sample

import android.R.attr.name
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.edfapay.paymentcard.EdfaPayPlugin
import com.edfapay.paymentcard.model.TransactionType
import com.edfapay.paymentcard.model.TxnParams
import com.edfapay.paymentcard.model.enums.Env
import com.edfapay.paymentcard.utils.isMocked
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE


class MainActivity : AppCompatActivity() {
    lateinit var cache:SharedPreferences

    // Register the launcher and result handler
    private val barcodeLauncher = registerForActivityResult<ScanOptions, ScanIntentResult>(
        ScanContract()
    ) { result: ScanIntentResult ->
        if (result.contents != null){
            findViewById<TextView>(R.id.txtAuthCode).text = result.contents
            val myEdit = cache.edit()
            myEdit.putString("auth", result.contents)
            myEdit.commit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cache = getSharedPreferences("EdfaPaySoftPOS", MODE_PRIVATE)
        initView()

        val have = EdfaPayPlugin.haveLocationPermission(this)
        if(!have){
            EdfaPayPlugin.requestLocationPermission(this){
                EdfaPayPlugin.currentLocation(this){ location, permission ->
                    Log.i("EdfaPayPlugin.currentLocation", "Mocked: ${location?.isMocked()}")
                    Log.i("EdfaPayPlugin.currentLocation",  it.toString())
                }
            }
        }
    }

    fun initView(){

        showInitializeSdk()

        findViewById<TextView>(R.id.txtVersion).text = EdfaPayPlugin.SDK_VERSION
        findViewById<TextView>(R.id.txtAuthCode).text = cache.getString("auth", "");

        findViewById<View>(R.id.btnInitialize).setOnClickListener {
            initEdfaPay()
        }

        findViewById<View>(R.id.btnPay).setOnClickListener {
            this.pay()
        }

        findViewById<View>(R.id.btnScanQr).setOnClickListener {
            barcodeLauncher.launch(
                ScanOptions()
                    .setBarcodeImageEnabled(false)
                    .setOrientationLocked(true)
                    .setDesiredBarcodeFormats(QR_CODE)
            )
        }
    }

    fun showInitializeSdk(){
        findViewById<View>(R.id.viewInit).visibility = View.VISIBLE
        findViewById<View>(R.id.viewPay).visibility = View.GONE
    }

    fun showPay(){
        findViewById<View>(R.id.viewInit).visibility = View.GONE
        findViewById<View>(R.id.viewPay).visibility = View.VISIBLE
    }

    fun initEdfaPay(){

        val authCode = findViewById<TextView>(R.id.txtAuthCode).text.toString()
        if(authCode.isEmpty()){
            Toast.makeText(this, "Invalid Auth Code", Toast.LENGTH_SHORT).show()
            return
        }

        EdfaPayPlugin.initiate(
            context = this,
            environment = Env.DEVELOPMENT,//.with("https://testdeployment.edfapay.com/"),
            authCode = authCode, // we-settle
            onSuccess = { plugin ->
                showPay()

                EdfaPayPlugin.theme
                    .setButtonBackgroundColor("#06E59F")
                    .setButtonTextColor("#000000")
                    .setHeaderImage(this, R.drawable.edfapay_text_logo)
                    .setPoweredByImage(this, R.drawable.edfapay_text_logo)

                Toast.makeText(this, "SDK Initialized Successfully", Toast.LENGTH_SHORT).show()
            }
        ){ err ->
            err.printStackTrace()
            Toast.makeText(this, "Error Initializing: ${err.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun pay(){
        val amount = findViewById<TextView>(R.id.txtAmount).text
        if(amount.toString().toDoubleOrNull() == null){
            Toast.makeText(this, "Invalid Amount", Toast.LENGTH_SHORT).show()
            return
        }

        val params = TxnParams(
            amount = amount.toString(),
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

    // Launc
}