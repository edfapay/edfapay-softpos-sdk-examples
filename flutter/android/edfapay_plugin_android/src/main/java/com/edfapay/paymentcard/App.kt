package com.edfapay.paymentcard

import android.app.Application
import com.edfapay.paymentcard.card.PaymentScheme

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        EdfaPayPlugin.initiate(this)
            .setMerchantNameAddress("Edfapay, Riyadh Saudi Arabia")
            .setInterfaceDeviceSerialNumber("0000000000000001")
            .setSupportedSchemes(
                listOf(
                    PaymentScheme.VISA,
                    PaymentScheme.MASTERCARD,
                    PaymentScheme.MAESTRO,
                    PaymentScheme.MADA_VISA,
                    PaymentScheme.MADA,
                )
            )
    }
}