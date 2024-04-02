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
import com.mastercard.terminalsdk.utility.ByteUtility
import com.visa.app.ttpkernel.ContactlessConfiguration
import com.visa.app.ttpkernel.NfcTransceiver
import com.visa.vac.tc.emvconverter.Utils

private var commandExecutionTime: Long = 0L
interface VisaKernelExecutor{
    private val TERMINAL_TYPE get() = byteArrayOf(0x22)
    private val POS_ENTRY_MODE get() = byteArrayOf(0x07)
    private val TTQ_VISA get() = byteArrayOf((0x26).toByte(), (0x00).toByte(), (0x40).toByte(), (0x00).toByte())
    private val MERCHANT_NAME_AND_LOCATION get() = byteArrayOf(
        0x00.toByte(), 0x11.toByte(), 0x22.toByte(), 0x33.toByte(), 0x44.toByte(),
        0x55.toByte(), 0x66.toByte(), 0x77.toByte(), 0x88.toByte(), 0x99.toByte(),
        0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte(), 0xEE.toByte(),
        0xFF.toByte()
    )

    private fun nfcTransceiver(isoDep: IsoDep, onException:(Throwable) -> Unit) = object : NfcTransceiver{
        override fun transceive(bytes: ByteArray?): ByteArray {
            var response:ByteArray = byteArrayOf()
            runCatch {
                if (isoDep.isConnected) {
                    val startTime = System.nanoTime()
                    response = isoDep.transceive(bytes)
                    val endTime = System.nanoTime()
                    commandExecutionTime = endTime - startTime
                }else{
                    throw EdfaException(Error.CARD_DISCONNECTED_WHILE_PROCESSING)
                }
            }

            val resp = KernelResponseCode.getCode(response)?.detail
            return response
        }

        override fun destroy() {
            runCatch(isoDep::close)
        }

        override fun isCardPresent(): Boolean {
            return isoDep.isConnected
        }

        private fun runCatch(block:() -> Unit ){
            try{
                block()
            }catch (e:Exception){
                onException(e)
            }
        }

    }

    fun executeVisa(paymentCard: PaymentCard, parameters: TxnParams, completion:() -> Unit, onError:(Throwable) -> Unit){
        runCatching {
            val transceiver = nfcTransceiver(paymentCard.isoDep, onException = onError)
            val configuration = getConfig(paymentCard.appTemplate, parameters)
            val result = EdfaPayPlugin.Kernels.getVisa().performTransaction(transceiver, configuration)

            val iccHexTagValue = result.data.mapValues { Utils.getHexString(it.value) }
            val iccHexValues = iccHexTagValue.values.filterNotNull()
            iccHexTagValue.forEach {
                Log.i("VisaKernelExecutor", "[ICC.TAG] ${it.key} : ${it.value}")
            }

            Log.i("VisaKernelExecutor", "[ICC.TAG] Removed null key value pairs")
            Log.i("VisaKernelExecutor", "[ICC] Hex ==> ${iccHexValues.joinToString("")}")

            paymentCard.kernelResponse = prepareResponse(iccHexTagValue, TransactionStatus.forVisa(result.finalOutcome))
            completion()

        }.onFailure {
            onError(it)
        }
    }

    private fun getConfig(appTemplate: Applet, parameters: TxnParams) = with(ContactlessConfiguration.getInstance()) {
        with(terminalData) {
            set(PaymentConfigTags.AMOUNT_AUTHORIZED_NUMERIC.hex, parameters.getEmvReadyAmount())
            set(PaymentConfigTags.CVM_LIMIT.hex, parameters.getEmvReadyCvmLimit())
            set(PaymentConfigTags.TRANSACTION_TYPE.hex, parameters.getEmvReadyTransactionType())
            set(PaymentConfigTags.COUNTRY_CODE.hex, parameters.getEmvReadyCountryCode())
            set(PaymentConfigTags.CURRENCY.hex, parameters.getEmvReadyCurrencyCode())
            set(PaymentConfigTags.TSC.hex, parameters.getEmvReadyTxnSeqCounter()) //
            set(PaymentConfigTags.MERCHANT_NAME_AND_LOCATION.hex, parameters.getEmvReadyMerchantAndLocation())
            set(PaymentConfigTags.INTERFACE_DEVICE_SERIAL_NUMBER.hex, parameters.getEmvReadyIFD_SerialNumber())
            set(PaymentConfigTags.AID.hex, ByteUtility.hexStringToByteArray(appTemplate.aid))
            set(PaymentConfigTags.TTQ.hex, TTQ_VISA)
            set(PaymentConfigTags.TERMINAL_TYPE.hex, TERMINAL_TYPE)
            set(PaymentConfigTags.POS_ENTRY_MODE.hex, POS_ENTRY_MODE)
            set(PaymentConfigTags.APPLICATION_VERSION_NO.hex, ByteUtility.hexStringToByteArray("0020"))
            set(PaymentConfigTags.TERMINAL_CAPABILITIES.hex, ByteUtility.hexStringToByteArray("E0F8C8"))
            set(PaymentConfigTags.CVM.hex, ByteUtility.hexStringToByteArray("420300"))
            set(PaymentConfigTags.TRANSACTION_STATUS_INFO.hex, ByteUtility.hexStringToByteArray("0000"))
        }
        this
    }

    private fun prepareResponse(iccTags:Map<String,String>, _status:TransactionStatus) : KernelResponse{
        return with(KernelResponse("02")) {

            status = _status
            icc = generateICC(iccTags)

            sequenceNumber = extractValue(iccTags, PaymentConfigTags.CARD_SEQUENCE_NUMBER)
            cryptogram = extractValue(iccTags, PaymentConfigTags.CRYPTOGRAM)
            cryptogramData = extractValue(iccTags, PaymentConfigTags.CRYPTOGRAM_DATA)
            cvm = extractValue(iccTags, PaymentConfigTags.CVM)
            tvr = extractValue(iccTags, PaymentConfigTags.TVR)
            ctq = extractValue(iccTags, PaymentConfigTags.CTQ)
            ttq = extractValue(iccTags, PaymentConfigTags.TTQ)
            aid = extractValue(iccTags, PaymentConfigTags.DEDICATED_FILE_NAME)
            cardholderName = extractValue(iccTags, PaymentConfigTags.CARDHOLDER_NAME)
            track2Data = extractValue(iccTags, PaymentConfigTags.TRACK_2_DATA)
            track2Data = when (track2Data.isNullOrBlank()) {
                true -> extractValue(iccTags, PaymentConfigTags.TRACK_2_DATA_ALTERNATIVE)
                false -> track2Data
            }

            this
        }
    }

    private fun generateICC(iccTags:Map<String,String>) : String{
        return iccTags.values.joinToString("")
    }

    private fun extractValue(iccTags:Map<String,String>, tag:PaymentConfigTags) : String?{
        val tlv = iccTags[tag.hex]
        if(tlv.isNullOrEmpty())
            return null
        return tlv.substring(tag.hex.length + 2)
    }


}