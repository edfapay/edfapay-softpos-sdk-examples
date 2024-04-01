package com.edfapay.paymentcard.kernel_integration

import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.nfc.NfcAdapter
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.edfapay.paymentcard.EdfaException
import com.edfapay.paymentcard.card.EdfaContactlessEvents
import com.edfapay.paymentcard.emvparser.PPSE
import com.edfapay.paymentcard.model.NfcStatus
import com.edfapay.paymentcard.model.TxnParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


// reader mode flags: listen for type A (not B), skipping ndef check
private const val READER_FLAGS = NfcAdapter.FLAG_READER_NFC_A or
        NfcAdapter.FLAG_READER_NFC_B or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
        NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS

class EdfaContactless(private val mActivity: ComponentActivity, private val listener: EdfaContactlessEvents) {
    init { instance = this }
    companion object{
        private lateinit var instance:EdfaContactless
        fun getInstance() = instance
    }

    private val nfcAdapter: NfcAdapter get() = NfcAdapter.getDefaultAdapter(mActivity)
    private var isoDep:IsoDep? = null

    val nfcStatus: NfcStatus
        get(){
        return when (mActivity.packageManager.hasSystemFeature(PackageManager.FEATURE_NFC)) {
            true -> when(nfcAdapter.isEnabled) {
                true -> NfcStatus.ENABLED
                false -> NfcStatus.DISABLED
            }
            false -> NfcStatus.NOT_SUPPORTED
        }
    }

    private lateinit var params: TxnParams
    fun pay(params: TxnParams) {
        this.params = params
        isoDep?.close()

        with(nfcStatus) {
            Log.e("***EdfaPay***", "[EdfaContactless] Status $this")
            listener.onNfcStatus(this)
            when (this) {
                NfcStatus.ENABLED -> {
                    listener.ready()

                    nfcAdapter.enableForegroundDispatch(mActivity, PendingIntent.getActivity(mActivity, 0, Intent(), PendingIntent.FLAG_IMMUTABLE), null, null)
                    nfcAdapter.enableReaderMode(
                        mActivity,
                        {
                            Log.e("***EdfaPay***", "[EdfaContactless] Payment card tapped")
                            listener.cardTap()

                            runCatching {
                                Log.e("***EdfaPay***", "[EdfaContactless] Executing ppse select command on IsoDep")
                                listener.cardConnecting()
                                isoDep = IsoDep.get(it)
                                isoDep?.connect()

                            }.onFailure {
                                Log.e("***EdfaPay***", "[EdfaContactless] Payment card scanning failed")
                                Log.e("***EdfaPay***", "[EdfaContactless] ${it.message}", it)
                                listener.cardConnectFail(data = it)

                            }.onSuccess {
                                listener.cardConnectSuccess()

                                runCatching {
                                    listener.cardSchemesFetching()

                                    with(PPSE.Select(isoDep!!)) {
                                        Log.e("***EdfaPay***", "[EdfaContactless] Success ppse command execution on IsoDep")
                                        Log.e("***EdfaPay***", "[EdfaContactless] Creating 'PaymentCard' from ppse response")
                                        listener.cardSchemesFound()
                                        with(this.paymentCard(params)) {
                                            listener.cardSchemesSorted()
                                            this
                                        }
                                    }
                                }.onFailure {
                                    mActivity.lifecycleScope.launch {
                                        listener.cardConnectFail(data = it)
                                    }

                                }.onSuccess {
                                    Log.e("***EdfaPay***", "[EdfaContactless] 'PaymentCard' object created from ppse response")
                                    mActivity.lifecycleScope.launch { listener.onCardConnected(it) }
                                }
                            }

                        },
                        READER_FLAGS,
                        Bundle()
                    )

                    Log.e("***EdfaPay***", "[EdfaContactless] Waiting for card to tap")
                    listener.waitingForCard()

                }
                else -> {}
            }

        }
    }

    fun close(){
        isoDep?.close()
        nfcAdapter.disableReaderMode(mActivity)
    }

    fun reset(withDelay:Long){
        isoDep?.close()
        nfcAdapter.disableReaderMode(mActivity)
        Handler(Looper.getMainLooper()).postDelayed({
            pay(params)
        }, withDelay)
    }
}