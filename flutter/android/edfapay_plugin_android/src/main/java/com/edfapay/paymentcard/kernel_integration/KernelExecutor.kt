package com.edfapay.paymentcard.kernel_integration

import com.edfapay.paymentcard.EdfaException
import com.edfapay.paymentcard.Error
import com.edfapay.paymentcard.card.Applet
import com.edfapay.paymentcard.model.TxnParams

open class KernelExecutor : VisaKernelExecutor, MastercardKernelExecutor, AlcineoKernelExecutor {
    protected var selectedAppTemplate: Applet? = null
    val appTemplate get() = selectedAppTemplate ?: throw EdfaException(Error.APPLET_NOT_SELECTED)

    var transactionParameters: TxnParams? = null
    var kernelResponse:KernelResponse? = null
}