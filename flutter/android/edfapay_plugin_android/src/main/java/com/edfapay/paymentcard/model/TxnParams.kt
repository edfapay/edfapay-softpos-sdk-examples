package com.edfapay.paymentcard.model

import com.edfapay.paymentcard.EdfaPayPlugin
import com.mastercard.terminalsdk.utility.ByteUtility
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*


data class TxnParams(
    val txnSeqCounter:String,
    val amount:String,
    val floorLimit:String,
    val transactionType: TransactionType,
    val countryCode:String,
    val currencyCode:String) : java.io.Serializable {

    fun validate() : TxnParams?{
        val allValid = true
        if(allValid)
            return this
        return null
    }

    fun getEmvReadyAmount() : ByteArray{
        val _amount = formatToEmvAmount(amount.toDouble())
        return ByteUtility.hexStringToByteArray(_amount)
    }

    fun getEmvReadyCvmLimit() : ByteArray{
        val _amount = formatToEmvAmount(floorLimit.toDouble())
        return ByteUtility.hexStringToByteArray(_amount)
    }

    fun getEmvReadyTxnSeqCounter() : ByteArray{
        val tsc = formatToEmvTxnSeqCounter(txnSeqCounter)
        return ByteUtility.hexStringToByteArray(tsc)
    }

    fun getEmvReadyTransactionType() : ByteArray{
        return byteArrayOf(transactionType.value.toByte())
    }

    fun getEmvReadyCountryCode() : ByteArray{
        val country = Locale("", countryCode).isO3Country
        val code = Currency.getInstance(currencyCode).numericCode.toString().padStart(4,'0')
        return ByteUtility.hexStringToByteArray(code)
    }

    fun getEmvReadyCurrencyCode() : ByteArray{
        val code = Currency.getInstance(currencyCode).numericCode.toString().padStart(4,'0')
        return ByteUtility.hexStringToByteArray(code)
    }

    fun getEmvReadyMerchantAndLocation() : ByteArray{
        val add = EdfaPayPlugin.getMerchantAddress()
        return ByteUtility.asciiStringToByteArray(add)
    }

    fun getEmvReadyIFD_SerialNumber(length:Int = 8) : ByteArray{
        val sn = EdfaPayPlugin.getInterfaceDeviceSerialNumber().padStart(length,'0')
        return ByteUtility.hexStringToByteArray(sn)
    }

    private fun formatToEmvID(amount:Double) : String{
        val decimalFormat = DecimalFormat("0000000000.00", DecimalFormatSymbols(Locale.US))
        return decimalFormat.format(amount).replace(".", "")
    }

    private fun formatToEmvAmount(amount:Double) : String{
        val decimalFormat = DecimalFormat("0000000000.00", DecimalFormatSymbols(Locale.US))
        return decimalFormat.format(amount).replace(".", "")
    }

    private fun formatToEmvTxnSeqCounter(tsc:String) : String{
        return tsc.padStart(6,'0')
    }
}