package com.edfapay.paymentcard

//class EdfaContactlessException(message:String, throwable:Throwable?) : Throwable(message, throwable) {
class EdfaException(type: Error, throwable: Throwable? = Throwable().fillInStackTrace()) : Exception(type.message, throwable ?: Throwable(type.message)) {

    companion object{
        fun with(throwable: Throwable) : EdfaException = EdfaException(Error.THROWABLE.by(throwable), throwable)
    }
}

enum class Error(var message:String){
    PLUGIN_NOT_INITIALIZED_PROPERLY(
        "Make sure you have initialize the EdfaPayPlugin properly as below:\n" +
            "EdfaPayPlugin.initiate(Context)\n" +
            "            .setMerchantNameAddress(String)\n" +
            "            .setInterfaceDeviceSerialNumber(String)\n" +
            "            .setSupportedSchemes(List<PaymentScheme>)"
    ),

    THROWABLE("Something went wrong"),
    CARD_DISCONNECTED_WHILE_PROCESSING(""),
    TLV_DECODING_WHILE_PPSE_SELECT(""),
    TLV_DECODED_NULL_WHILE_PPSE_SELECT(""),
    PROCESSING_AID_WHILE_PPSE_SELECT("PPSE:Response ->  (61) application templates not exist"),
    KERNEL_NOT_IMPLEMENTED(""),
    INVALID_OR_EMPTY_TRANSACTION_PARAMS("Invalid or empty transaction parameters"),
    VISA_KERNEL_NOT_INITIALIZED("Visa kernel may not initialized"),
    MASTERCARD_KERNEL_NOT_INITIALIZED("Mastercard kernel may not initialized"),
    ALCINEO_KERNEL_NOT_INITIALIZED("Alcineo kernel may not initialized"),
    APPLET_NOT_SELECTED("Applet is not selected"),
    ;

    fun by(throwable: Throwable) : Error {
        val t = THROWABLE
        t.message = throwable.localizedMessage ?: throwable.message ?: message
        return t
    }
    fun by(message: String) : Error {
        this.message = message
        return this
    }
}