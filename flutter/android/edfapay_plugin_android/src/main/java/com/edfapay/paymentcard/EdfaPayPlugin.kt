package com.edfapay.paymentcard

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import androidx.annotation.RestrictTo
import androidx.lifecycle.LifecycleOwner
import com.alcineo.softpos.payment.api.SoftposAPI
import com.alcineo.softpos.payment.api.TransactionAPI
import com.alcineo.softpos.payment.api.interfaces.NFCListener
import com.alcineo.softpos.payment.api.interfaces.TransactionEventListener
import com.alcineo.softpos.payment.model.transaction.TransactionParameters
import com.edfapay.paymentcard.card.PaymentCard
import com.edfapay.paymentcard.card.PaymentScheme
import com.edfapay.paymentcard.model.TxnParams
import com.edfapay.paymentcard.ui.ScanCardActivity
import com.mastercard.terminalsdk.TerminalSdk
import com.mastercard.terminalsdk.exception.L1RSPException
import com.mastercard.terminalsdk.listeners.*
import com.mastercard.terminalsdk.objects.ErrorIndication
import com.visa.app.ttpkernel.ContactlessKernel
import com.visa.app.ttpkernel.Version
import io.github.binaryfoo.tlv.toHexString
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException
import java.nio.ByteBuffer
import java.security.SecureRandom

private typealias PluginCallBack = (PaymentCard, onServerComplete:(status:Boolean, statusAcknowlege:() -> Unit) -> Unit) -> Unit
private typealias TimeOutCallBack = () -> Unit

@RestrictTo(RestrictTo.Scope.LIBRARY)
lateinit var onCardProcessingComplete: PluginCallBack
lateinit var OnRequestTimerEnd: TimeOutCallBack
lateinit var OnCardScanTimerEnd: TimeOutCallBack
object EdfaPayPlugin{
    private var merchantAddress:String = "Edfapa, Riyadh Saudi Arabia"
    private var interfaceDeviceSerialNumber:String = "0000000000000001"
    private var supportedSchemes: List<PaymentScheme> = mutableListOf()
    fun getMerchantAddress()  = merchantAddress
    fun getInterfaceDeviceSerialNumber()  = interfaceDeviceSerialNumber
    fun getSupportedSchemes()  = supportedSchemes


    fun setMerchantNameAddress(address:String) : EdfaPayPlugin {
        merchantAddress = address
        return this
    }

    fun setInterfaceDeviceSerialNumber(serialNumber:String): EdfaPayPlugin {
        interfaceDeviceSerialNumber = serialNumber
        return this
    }

    fun setSupportedSchemes(supportedSchemes: List<PaymentScheme>): EdfaPayPlugin {
        EdfaPayPlugin.supportedSchemes = supportedSchemes
        return this
    }

    @Throws(EdfaException::class)
    fun pay(activity: Activity, transactionParameters:TxnParams, onCardProcessingComplete: PluginCallBack, OnRequestTimerEnd: TimeOutCallBack, OnCardScanTimerEnd: TimeOutCallBack){
        com.edfapay.paymentcard.onCardProcessingComplete = onCardProcessingComplete
        com.edfapay.paymentcard.OnRequestTimerEnd = OnRequestTimerEnd
        com.edfapay.paymentcard.OnCardScanTimerEnd = OnCardScanTimerEnd

        if(supportedSchemes.isEmpty() || merchantAddress.isEmpty() || interfaceDeviceSerialNumber.isEmpty())
            throw EdfaException(Error.PLUGIN_NOT_INITIALIZED_PROPERLY)

        transactionParameters.validate()?.let { params ->
            with(Intent(activity, ScanCardActivity::class.java)) {
                putExtra(ScanCardActivity.PARAMS, params)
                activity.startActivity(this)
            }
        }
    }

    object Kernels{
        private var visa:ContactlessKernel? = null
        private var mastercard:TerminalSdk? = null
        private var alcineo: Alcineo? = null

        fun getVisa() = visa ?: throw EdfaException(Error.VISA_KERNEL_NOT_INITIALIZED)
        fun getMastercard() = mastercard ?: throw EdfaException(Error.MASTERCARD_KERNEL_NOT_INITIALIZED)
        fun getAlcineo() = alcineo ?: throw EdfaException(Error.ALCINEO_KERNEL_NOT_INITIALIZED)


        private fun initVisa(context: Context){
            visa = with(ContactlessKernel.getInstance(context)) {
                val kernalData = this.kernelData.mapValues { it.value.toHexString() }
                val version = Version.getVersion().toHexString()
                Log.d("EdfaPayKernels", "[VISA] Kernel initiated: version ${version}")
                kernalData.forEach {
                    Log.d("EdfaPayKernels", "[VISA] Kernel Data: ${it.key} -> ${it.value}")
                }
                this
            }
        }

        private fun initMasterCard(context: Context){
            mastercard = with(TerminalSdk.getInstance()) {
                with(configuration) {

                    // This enables the SDK to access the resources files
                    withResourceProvider { fileName ->
                        context.resources.assets.open("mastercard/$fileName")
                    }

                    // implementations that allows the SDK to interact with the card.
                    with(MasterCardCommProviderStub()) {
                        withCardCommunication(this)
                        setInterface(description)
                    }

                    // This enables the mPOS application to receive outcome of a transaction.
                    withTransactionObserver { }

                    // This enables the SDK to request for a random number
                    withUnpredictableNumberProvider { size ->
                        val randomNumber = ByteArray(size)
                        SecureRandom().nextBytes(randomNumber)
                        val checkBuffer = ByteBuffer.allocate(randomNumber.size + (8 - (randomNumber.size % 8)))
                        checkBuffer.put(randomNumber)
                        randomNumber
                    }

                    // mange message success scanning or not
                    withMessageDisplayProvider {
                        val toneGen1 = ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME)
//                        if (it.status == UserInterfaceData.UIRDStatus.CARD_READ_SUCCESSFULLY)
//                            toneGen1.startTone(ToneGenerator.TONE_DTMF_P, 500)
//                        else
//                            toneGen1.startTone(ToneGenerator.TONE_SUP_CONGESTION, 600)
                    }

                    initializeLibrary()
                    selectProfile("MPOS")
                }

                val libInfo = libraryServices.libraryInformation.toString().split("\n")
                Log.d("EdfaPayKernels", "[MASTERCARD] Kernel initiated.")
                libInfo.forEach {
                    Log.d("EdfaPayKernels","[MASTERCARD] $it")
                }

                this
            }
        }

        private fun initAlcineo(context: Context){
            runCatching {
                SoftposAPI.initialize(context)
                alcineo = Alcineo
            }.onFailure {
                Log.e("EdfaPayKernels", "[ALCINEO] Failed to initialize kernel.")
            }.onSuccess {
                Log.d("EdfaPayKernels", "[ALCINEO] Kernel initiated.")
            }
        }

        internal fun initiate(context: Context){
            CoroutineScope(Dispatchers.IO).launch {
                initVisa(context = context)
                initMasterCard(context = context)
                initAlcineo(context = context)
            }
        }

    }



    fun initiate(context: Context) : EdfaPayPlugin {
        Kernels.initiate(context)
        return this
    }


    object Alcineo{
        fun startTransaction(transactionParameters: TransactionParameters, nfcListener: NFCListener, transactionEventListener: TransactionEventListener,  lifecycleOwner: LifecycleOwner){
            TransactionAPI.startTransaction(transactionParameters, nfcListener, transactionEventListener, lifecycleOwner)
        }

        fun isTransactionActive() = TransactionAPI.isTransactionActive()

        @Deprecated("")
        fun stopTransactionAndConfirm() = TransactionAPI.stopTransactionAndConfirm()

        fun cancelTransaction() = TransactionAPI.cancelTransaction()

        @Throws(IOException::class)
        fun sendDET(bytes: ByteArray?) = TransactionAPI.sendDET(bytes)

        @Throws(IOException::class)
        fun sendPin(bytes: ByteArray?) = TransactionAPI.sendPin(bytes)

        @Throws(IOException::class)
        fun sendPinCancel() = TransactionAPI.sendPinCancel()

    }

    private class MasterCardCommProviderStub : CardCommunicationProvider {

        @Throws(L1RSPException::class)
        override fun sendReceive(bytes: ByteArray): ByteArray {
            Log.e(TAG, "sendReceive: Utilizing Stub Implementation of CardCommunicationProvider")
            throw L1RSPException("Stub Reader", ErrorIndication.L1_Error_Code.TIMEOUT_ERROR)

        }

        @Throws(L1RSPException::class)
        override fun waitForCard(): CardCommunicationProvider.ConnectionObject {
            Log.e(TAG, "connectCard: Utilizing Stub Implementation of CardCommunicationProvider")
            throw L1RSPException("Stub Reader", ErrorIndication.L1_Error_Code.TIMEOUT_ERROR)
        }

        override fun removeCard(): Boolean {
            return false
        }

        @Throws(L1RSPException::class)
        override fun connectReader(): Boolean {
            Log.e(TAG, "connectReader: Utilizing Stub Implementation of CardCommunicationProvider")
            throw L1RSPException("Stub Reader", ErrorIndication.L1_Error_Code.PROTOCOL_ERROR)
        }

        override fun disconnectReader(): Boolean {
            return false
        }

        override fun isReaderConnected(): Boolean {
            return false
        }

        override fun isCardPresent(): Boolean {
            return false
        }

        override fun getDescription(): String {
            return "Reader Stub"
        }

        override fun getPreviousCommandExecutionTime(): Long {
            return 0
        }

        companion object {
            private const val TAG = "CardCommProviderStub"
        }
    }

}

