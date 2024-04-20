package com.edfapay.paymentcard.kernel_integration

import com.edfapay.paymentcard.card.PaymentCard
import com.edfapay.paymentcard.model.TxnParams

interface  AlcineoKernelExecutor{
    fun executeAlcineo(paymentCard: PaymentCard, parameters: TxnParams, completion:() -> Unit, onError:(Throwable) -> Unit){

    }
}