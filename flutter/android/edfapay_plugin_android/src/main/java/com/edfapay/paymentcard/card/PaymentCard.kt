package com.edfapay.paymentcard.card

import android.nfc.tech.IsoDep
import android.util.Log
import com.edfapay.paymentcard.EdfaException
import com.edfapay.paymentcard.EdfaPayPlugin
import com.edfapay.paymentcard.Error
import com.edfapay.paymentcard.R
import com.edfapay.paymentcard.utils.ThreadPrioritized
import com.edfapay.paymentcard.emvparser.PPSE
import com.edfapay.paymentcard.kernel_integration.*
import kotlinx.coroutines.*

class PaymentCard(val isoDep: IsoDep, val ppseResponse:PPSE.Response, private val applets: MutableList<Applet> = mutableListOf()) : KernelExecutor() {

    fun isValid() = true

    fun isSuccess() = kernelResponse?.isSuccess == true

    override fun toString(): String {
        return """
            appTemplates: $applets
        """.trimIndent()
    }

    fun log(){
        Log.e("***EdfaPay***", "[PaymentCard] -- cardholderName: ${kernelResponse?.cardholderName ?: "NONE"} ")
        Log.e("***EdfaPay***", "[PaymentCard] --- cvmLimit: ${kernelResponse?.cvm ?: "NONE"}")
        Log.e("***EdfaPay***", "[PaymentCard] - appTemplates: ${applets} ")
    }

    private var listener:EdfaContactlessEvents? = null

    fun selectScheme(listener:EdfaContactlessEvents){
        this.listener = listener

        // Avoiding the card schemes if its not supported by Backend/App
        applets.removeIf {
            val remove = EdfaPayPlugin.getSupportedSchemes().contains(it.scheme).not()
            remove
        }
        listener.cardSchemesFiltered()


        val iterator = applets.iterator()
        if(iterator.hasNext()){
            selectedAppTemplate = iterator.next()
            listener.cardSchemeSelected()
            listener.onCardSchemeSelected(this)
        }else{
            listener.supportedSchemeNotFound()
        }
    }

    fun invokeKernel(){
        listener?.transactionStarted()

        when (appTemplate.scheme) {
            PaymentScheme.MADA -> executeAlcineo()
            PaymentScheme.MADA_VISA, PaymentScheme.VISA -> executeVisa()
            PaymentScheme.MASTERCARD, PaymentScheme.MADA_MASTERCARD, PaymentScheme.MAESTRO -> executeMasterCard()

            PaymentScheme.UNION_PAY -> handleError(EdfaException(Error.KERNEL_NOT_IMPLEMENTED.by("Kernel not implemented for scheme ${appTemplate.scheme}")))
            PaymentScheme.PAY_PAK -> handleError(EdfaException(Error.KERNEL_NOT_IMPLEMENTED.by("Kernel not implemented for scheme ${appTemplate.scheme}")))
            PaymentScheme.AMEX -> handleError(EdfaException(Error.KERNEL_NOT_IMPLEMENTED.by("Kernel not implemented for scheme ${appTemplate.scheme}")))
            PaymentScheme.DISCOVER -> handleError(EdfaException(Error.KERNEL_NOT_IMPLEMENTED.by("Kernel not implemented for scheme ${appTemplate.scheme}")))
            PaymentScheme.MEEZA -> handleError(EdfaException(Error.KERNEL_NOT_IMPLEMENTED.by("Kernel not implemented for scheme ${appTemplate.scheme}")))
            PaymentScheme.UNKNOWN -> handleError(EdfaException(Error.KERNEL_NOT_IMPLEMENTED.by("Kernel not implemented for scheme ${appTemplate.scheme}")))
            else -> handleError(EdfaException(Error.KERNEL_NOT_IMPLEMENTED.by("Kernel not implemented for scheme ${appTemplate.scheme}")))
        }
    }

    @Throws(Exception::class)
    private fun executeVisa(){
        transactionParameters?.let { params ->
            listener?.transactionProcessing()
            executeVisa(
                this@PaymentCard,
                params,
                completion = {
                    when(kernelResponse?.isSuccess) {
                        true -> {
                            listener?.transactionSuccess()
                            listener?.onCardProcessComplete(this@PaymentCard)
                        }
                        else -> listener?.transactionFail()
                    }
                },
                onError = ::handleError
            )
        } ?: handleError(EdfaException(Error.INVALID_OR_EMPTY_TRANSACTION_PARAMS))
    }

    @Throws(Exception::class)
    private fun executeMasterCard(){
        transactionParameters?.validate()?.let { params ->
            ThreadPrioritized.run {
                listener?.transactionProcessing()
                executeMasterCard(
                    this,
                    params,
                    completion = {
                        when(kernelResponse?.isSuccess) {
                            true -> {
                                listener?.transactionSuccess()
                                listener?.onCardProcessComplete(this)
                            }
                            else -> listener?.transactionFail()
                        }
                    },
                    onError = ::handleError
                )
            }
        } ?: handleError(EdfaException(Error.INVALID_OR_EMPTY_TRANSACTION_PARAMS))
    }

    @Throws(Exception::class)
    private fun executeAlcineo(){
        transactionParameters?.validate()?.let { params ->
            listener?.transactionProcessing()
            executeAlcineo(
                this,
                params,
                completion = {
                    when(kernelResponse?.isSuccess) {
                        true -> {
                            listener?.transactionSuccess()
                            listener?.onCardProcessComplete(this)
                        }
                        else -> listener?.transactionFail()
                    }
                },
                onError = ::handleError
            )
        } ?: handleError(EdfaException(Error.INVALID_OR_EMPTY_TRANSACTION_PARAMS))
    }

    private fun handleError(e:Throwable){
        CoroutineScope(Dispatchers.Main).launch {
            when ((e is EdfaException)) {
                true -> listener?.onError(e)
                false -> listener?.onError(EdfaException(Error.THROWABLE.by(e), e))
            }
        }
    }

    fun schemeSound() = appTemplate.scheme!!.sound()
    fun schemeAnimation() = appTemplate.scheme!!.animation()

}