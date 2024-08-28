package com.edfapay.sdk.sample

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.edfapay.paymentcard.EdfaPayPlugin
import com.edfapay.paymentcard.model.TransactionType
import com.edfapay.paymentcard.model.TxnParams
import com.edfapay.paymentcard.model.enums.Env
import com.edfapay.paymentcard.utils.extentions.getDeviceIdentifierAsUUID
import com.edfapay.paymentcard.utils.isMocked
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions
import com.journeyapps.barcodescanner.ScanOptions.QR_CODE


class MainActivity : AppCompatActivity() {
    private var contentView: View? = null
    private var viewPay: View? = null
    private var viewInit: View? = null
    private var txtAmount: TextView? = null
    private var btnScanQr: View? = null
    private var btnPay: View? = null
    private var btnInitialize: Button? = null
    private var txtAuthCode: TextView? = null
    private var txtVersion: TextView? = null
    private var txtPartner: TextView? = null
    private var spinnerEnv: Spinner? = null
    private var txtEnvUrl: TextView? = null
    private var txtDeviceId: TextView? = null
    lateinit var cache:SharedPreferences
    var env:Env? = null

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

        contentView = getWindow().getDecorView().findViewById(android.R.id.content)

        txtDeviceId = findViewById(R.id.txtDeviceId)
        txtEnvUrl = findViewById(R.id.txtEnvUrl)
        spinnerEnv = findViewById(R.id.envSpinner)
        txtPartner = findViewById(R.id.txtPartner)
        txtVersion = findViewById(R.id.txtVersion)
        txtAuthCode = findViewById(R.id.txtAuthCode)
        btnInitialize = findViewById(R.id.btnInitialize)
        btnPay = findViewById(R.id.btnPay)
        btnScanQr = findViewById(R.id.btnScanQr)
        txtAmount = findViewById<TextView>(R.id.txtAmount)

        viewInit = findViewById<View>(R.id.viewInit)
        viewPay = findViewById<View>(R.id.viewPay)

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
        txtDeviceId?.text = "UDID: ".plus(getDeviceIdentifierAsUUID())

        showInitializeSdk()

        with(spinnerEnv!!) {
            val items = Env.values().map { it.name }.toMutableList()
            items.add(0, "Select Environment")
            adapter = ArrayAdapter(applicationContext, android.R.layout.simple_spinner_dropdown_item, items)
            onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    env = Env.values().firstOrNull { it.name ==  items[position]}
                    with(txtEnvUrl) {
                        this?.text = env?.url ?: "Missing Environment Url"
                        this?.isEnabled = env?.name == "DEMO"
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }

        txtPartner?.text = EdfaPayPlugin.PARTNER
        txtVersion?.text = EdfaPayPlugin.SDK_VERSION
        txtAuthCode?.text = cache.getString("auth", "");
        btnInitialize?.setOnClickListener {
            initEdfaPay()
        }

        btnPay?.setOnClickListener {
            this.pay()
        }

        btnScanQr?.setOnClickListener {
            barcodeLauncher.launch(
                ScanOptions()
                    .setBarcodeImageEnabled(false)
                    .setOrientationLocked(true)
                    .setDesiredBarcodeFormats(QR_CODE)
            )
        }
    }

    fun showInitializeSdk(){
        viewInit?.visibility = View.VISIBLE
        viewPay?.visibility = View.GONE
    }

    fun showPay(){
        viewInit?.visibility = View.GONE
        viewPay?.visibility = View.VISIBLE
    }

    fun initEdfaPay(){
        val authCode = txtAuthCode?.text.toString()
        if(authCode.isEmpty()){
            Toast.makeText(this, "Invalid Auth Code", Toast.LENGTH_SHORT).show()
            return
        }else if(env == null){
            Toast.makeText(this, "Invalid Environment Selected", Toast.LENGTH_SHORT).show()
            return
        }else {

        }


        initializing(true)
        EdfaPayPlugin.initiate(
            context = this,
            environment = Env.DEVELOPMENT,//.with("https://testdeployment.edfapay.com/"),
            authCode = authCode, // we-settle
            onSuccess = { plugin ->
                initializing(false)
                showPay()

                EdfaPayPlugin.theme
                    .setButtonBackgroundColor("#06E59F")
                    .setButtonTextColor("#000000")
                    .setHeaderImage(this, R.drawable.edfapay_text_logo)
                    .setPoweredByImage(this, R.drawable.edfapay_text_logo)

                Toast.makeText(this, "SDK Initialized Successfully", Toast.LENGTH_SHORT).show()
            }
        ){ err ->

            initializing(false)
            err.printStackTrace()
            Toast.makeText(this, "Error Initializing: ${err.message}", Toast.LENGTH_SHORT).show()
        }
    }

    fun pay(){
        val amount = txtAmount?.text
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

    fun initializing(yes:Boolean){
        when (yes) {
            true -> {
                btnInitialize?.text = getString(R.string.initializing_please_wait)
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
            false -> {
                btnInitialize?.text = getString(R.string.initialize_sdk)
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }
}