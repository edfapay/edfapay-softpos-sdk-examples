package com.edfapay.paymentcard.kernel_integration

import android.nfc.tech.IsoDep
import android.util.Log
import com.edfapay.paymentcard.EdfaException
import com.edfapay.paymentcard.EdfaPayPlugin
import com.edfapay.paymentcard.Error
import com.edfapay.paymentcard.card.Applet
import com.edfapay.paymentcard.card.PaymentCard
import com.edfapay.paymentcard.emvparser.PaymentConfigTags
import com.edfapay.paymentcard.model.TxnParams
import com.mastercard.terminalsdk.listeners.CardCommunicationProvider
import com.mastercard.terminalsdk.listeners.PaymentDataProvider
import com.mastercard.terminalsdk.utility.ByteArrayWrapper
import java.util.HashMap
import com.edfapay.paymentcard.emvparser.PaymentConfigTags.*
import com.mastercard.terminalsdk.objects.ReaderOutcome


private var commandExecutionTime: Long = 0L
interface MastercardKernelExecutor {

    private fun nfcTransceiver(isoDep: IsoDep, onException:(Throwable) -> Unit) = object : CardCommunicationProvider{
        override fun sendReceive(bytes: ByteArray?): ByteArray {
            var response:ByteArray = byteArrayOf()
            runCatch {
                if (isoDep.isConnected) {
                    val startTime = System.nanoTime()
                    response = isoDep.transceive(bytes)
                    val endTime = System.nanoTime()
                    commandExecutionTime = endTime - startTime
                }else
                    throw EdfaException(Error.CARD_DISCONNECTED_WHILE_PROCESSING)
            }

            val resp = KernelResponseCode.getCode(response)?.detail
            return response
        }

        override fun waitForCard(): CardCommunicationProvider.ConnectionObject {
            return ConnectionObjectImpl()
        }

        override fun removeCard(): Boolean {
            runCatch(isoDep::close)
            return isoDep.isConnected
        }

        override fun connectReader(): Boolean {
            if (isoDep.isConnected.not()){
                runCatch {
                    isoDep.close()
                    isoDep.connect()
                }
            }
            return true
        }

        override fun disconnectReader(): Boolean {
            runCatch(isoDep::close)
            return isoDep.isConnected.not()
        }

        override fun isReaderConnected() = isoDep.isConnected
        override fun isCardPresent() = isoDep.isConnected
        override fun getDescription() = "Built-in NFC Controller"
        override fun getPreviousCommandExecutionTime() = commandExecutionTime

        private fun runCatch(block:() -> Unit ){
            try{
                block()
            }catch (e:Exception){
                onException(e)
            }
        }
    }

    @Throws(Exception::class)
    fun executeMasterCard(paymentCard: PaymentCard, parameters: TxnParams, completion:() -> Unit, onError:(Throwable) -> Unit){
        Log.v("MastercardKernelExecutor", ".executeMasterCard")
        runCatching {
            val transceiver = nfcTransceiver(paymentCard.isoDep, onException = onError)
            val configuration = EdfaPayPlugin.Kernels.getMastercard().configuration
            with(configuration) {
                withCardCommunication(transceiver)
                setInterface(transceiver.description)
                withTransactionObserver {
                    Log.v("MastercardKernelExecutor", "executeMasterCard.withTransactionObserver")
                    paymentCard.kernelResponse = prepareResponse(it)
                    completion()
                }

                initializeLibrary().apply {
                    selectProfile("MPOS") // Profiles are only available for use after successful initialization of the Transaction API
                    proceedWithMastercardTransaction(getConfig(paymentCard.appTemplate, parameters), paymentCard.ppseResponse.rawBytes)
                }
            }
        }.onFailure {
            onError(it)
        }
        /*
        * */
    }


    private fun getConfig(appTemplate: Applet, parameters: TxnParams): PaymentDataProvider {
       return object: PaymentDataProvider{
           override fun getPaymentDataMap(): HashMap<Int, ByteArrayWrapper?> {

               return with(HashMap<Int, ByteArrayWrapper?>()) {
                   put(AMOUNT_AUTHORIZED_NUMERIC.tagAsInt, ByteArrayWrapper(parameters.getEmvReadyAmount()))
                   put(CVM_LIMIT.tagAsInt, ByteArrayWrapper(parameters.getEmvReadyCvmLimit()))
                   put(TERMINAL_COUNTRY_CODE.tagAsInt, ByteArrayWrapper( parameters.getEmvReadyCountryCode()))
                   put(TRANSACTION_TYPE.tagAsInt, ByteArrayWrapper(parameters.getEmvReadyTransactionType()))
                   put(CURRENCY.tagAsInt, ByteArrayWrapper(parameters.getEmvReadyCurrencyCode()))
                   put(TSC.tagAsInt, ByteArrayWrapper(parameters.getEmvReadyTxnSeqCounter()))
                   put(MERCHANT_NAME_AND_LOCATION.tagAsInt, ByteArrayWrapper(parameters.getEmvReadyMerchantAndLocation()))
                   put(INTERFACE_DEVICE_SERIAL_NUMBER.tagAsInt, ByteArrayWrapper(parameters.getEmvReadyIFD_SerialNumber()))
                   put(TRANSACTION_STATUS_INFO.tagAsInt, ByteArrayWrapper("0000"))
                   put(PUNATC_TRACK1.tagAsInt, ByteArrayWrapper("000000000FE0"))
                   put(ACCOUNT_TYPE.tagAsInt, ByteArrayWrapper("00"))
                   put(TXN_CURRENCY_EXPONENT.tagAsInt, ByteArrayWrapper("02")) // Transaction Currency Exponent
                   put(AMOUNT_AUTHORIZED_BINARY.tagAsInt, ByteArrayWrapper("00000000"))
                   put(AMOUNT_OTHER_NUMERIC.tagAsInt, ByteArrayWrapper("000000000000"))
                   put(AMOUNT_OTHER_BINARY.tagAsInt, ByteArrayWrapper("00000000"))

                   put(0x9F53, ByteArrayWrapper("52")) // Transaction Category Code
                   put(0x9F7C, ByteArrayWrapper("4469676974616C44657669636573202620496F54")) // Merchant Custom Data
                   put(0xDF8104, null) // Balance Read Before Gen AC
                   put(0xDF8105, null) // Balance Read After Gen AC
                   this
               }
           }

           override fun setPaymentDataEntry(p0: Int?, p1: ByteArrayWrapper?) {}

       }
    }


    private fun prepareResponse(outcome: ReaderOutcome) : KernelResponse{
        return with(KernelResponse("02")) {

            when (outcome.outcomeParameterSet.isDataRecordPresent) {
                true -> {
                    with(finalIccData(outcome)) {
                        status = TransactionStatus.forMasterCard(outcome.outcomeParameterSet)
                        icc = this.values.joinToString("")
                        sequenceNumber = extractValue(this, CARD_SEQUENCE_NUMBER)
                        cryptogram = extractValue(this, CRYPTOGRAM)
                        cryptogramData = extractValue(this, CRYPTOGRAM_DATA)
                        cvm = extractValue(this, CVM)
                        tvr = extractValue(this, TVR)
                        ctq = extractValue(this, CTQ)
                        ttq = extractValue(this, TTQ)
                        aid = extractValue(this, DEDICATED_FILE_NAME)
                        cardholderName = extractValue(this, CARDHOLDER_NAME)
                        track2Data = extractValue(this, TRACK_2_DATA)
                        track2Data = when (track2Data.isNullOrBlank()) {
                            true -> extractValue(this, TRACK_2_DATA_ALTERNATIVE)
                            false -> track2Data
                        }
                    }
                }
                false -> {
                    status = TransactionStatus.ERROR
                }
            }

            this
        }
    }

    private fun finalIccData(outcome: ReaderOutcome) = with(mutableMapOf<String, String>()) {
        outcome.additionalInformation.forEach {
            val key = it.tagObject.toHexString() 
            val tlv = it.toHexString()
            if(key.isNullOrBlank().not() && tlv.isNullOrBlank().not()){
                put(key, tlv)
            }
        }
        
        outcome.dataRecordContents.forEach {
            val key = it.tagObject.toHexString()
            val tlv = it.toHexString()
            if(key.isNullOrBlank().not() && tlv.isNullOrBlank().not()){
                put(key, tlv)
            }
        }
        this
    }

    private fun extractValue(map:Map<String,String>, tag:PaymentConfigTags) : String?{
        val tlv = map[tag.hex]
        if(tlv.isNullOrEmpty() || (tlv.length + 2) <= tag.hex.length)
            return null
        return tlv.substring(tag.hex.length + 2)
    }

    private class ConnectionObjectImpl : CardCommunicationProvider.ConnectionObject {
        override fun getInterfaceType(): CardCommunicationProvider.InterfaceType =
            CardCommunicationProvider.InterfaceType.CONTACTLESS

        override fun getBytes(): ByteArray = ByteArray(0)
    }
}