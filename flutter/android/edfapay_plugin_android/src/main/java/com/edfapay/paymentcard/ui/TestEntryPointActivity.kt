package com.edfapay.paymentcard.ui

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.edfapay.paymentcard.EdfaPayPlugin
import com.edfapay.paymentcard.R
import com.edfapay.paymentcard.model.TransactionType
import com.edfapay.paymentcard.model.TxnParams
import com.edfapay.paymentcard.utils.delay

class TestEntryPointActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_entry_point)

        findViewById<Button>(R.id.btnPay).setOnClickListener {

            val params = TxnParams(
                amount = "10.00", countryCode = "SA", currencyCode = "SAR",
                floorLimit = "200.00", txnSeqCounter = "11",
                transactionType = TransactionType.PURCHASE,
            )

            EdfaPayPlugin
                .pay(
                    this,
                    params,
                    onCardProcessingComplete = { paymentCard, onServerComplete ->
                        Toast.makeText(this, "Card Processing Completed", Toast.LENGTH_SHORT).show()
                        delay(10000){ //  Delay for Server Request
                            Toast.makeText(this, "Server Response Received", Toast.LENGTH_SHORT).show()
                            onServerComplete(true){
                                Toast.makeText(this, "Server Status Acknowledged", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },

                    OnRequestTimerEnd = {
                        Toast.makeText(this, "Server Request Timeout", Toast.LENGTH_SHORT).show()
                    },

                    OnCardScanTimerEnd = {
                        Toast.makeText(this, "Card Scan Timeout", Toast.LENGTH_SHORT).show()
                    }
                )
        }
    }
}