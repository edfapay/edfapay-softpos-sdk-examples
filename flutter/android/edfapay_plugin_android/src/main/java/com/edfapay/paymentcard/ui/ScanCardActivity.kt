package com.edfapay.paymentcard.ui

import android.animation.Animator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.edfapay.paymentcard.*
import com.edfapay.paymentcard.kernel_integration.EdfaContactless
import com.edfapay.paymentcard.card.EdfaContactlessEvents
import com.edfapay.paymentcard.card.PaymentCard
import com.edfapay.paymentcard.card.PaymentScheme
import com.edfapay.paymentcard.databinding.ActivityScanCardBinding
import com.edfapay.paymentcard.model.NfcStatus
import com.edfapay.paymentcard.model.TerminalStatus
import com.edfapay.paymentcard.model.TxnParams
import com.edfapay.paymentcard.utils.*
import kotlinx.coroutines.launch

class ScanCardActivity : AppCompatActivity(), EdfaContactlessEvents {
    val REQUEST_TIMEOUT = 30000
    private lateinit var binding: ActivityScanCardBinding
    private var txnParams:TxnParams? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanCardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        txnParams =  intent.serializable(PARAMS)
        if(txnParams?.validate() == null)
            assert(false) { "Invalid transaction parameters passed to ScanCardActivity.class" }
        initView()
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            with(this@ScanCardActivity) {
                EdfaContactless(this, this).pay(txnParams!!)
            }
        }
        binding.timoutTimer.start(30000)
        binding.timoutTimer.setOnCountdownEndListener {
            OnCardScanTimerEnd()
            finish()
        }
    }

    fun initView(){
        binding.txtAmount.text = txnParams?.amount
        binding.txtAmountCurrency.text = txnParams?.currencyCode
        with(EdfaPayPlugin.getSupportedSchemes()) {
            with(binding.schemes) {
                iconMada.visibility = visibilityOf(PaymentScheme.MADA)
                iconMasterCard.visibility = visibilityOf(PaymentScheme.MASTERCARD)
                iconVisa.visibility = visibilityOf(PaymentScheme.VISA)
                iconMaestro.visibility = visibilityOf(PaymentScheme.MAESTRO)
                iconAmex.visibility = visibilityOf(PaymentScheme.AMEX)
                iconDiscover.visibility = visibilityOf(PaymentScheme.DISCOVER)

                if(iconMasterCard.visibility == View.GONE){
                    iconMasterCard.visibility = visibilityOf(PaymentScheme.MADA_MASTERCARD)
                }

                if(iconVisa.visibility == View.GONE){
                    iconVisa.visibility = visibilityOf(PaymentScheme.MADA_VISA)
                }

                llEmptyCardSchemes.visibility = when (isEmpty()) {
                    true -> {
                        binding.lightIndicator.schemeNotConfigured()
                        View.VISIBLE
                    }
                    false -> View.GONE
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onNfcStatus(status: NfcStatus) {
        println("onNfcStatus: ")
    }

    override fun waitingForCardToTap() {
        println("waitingForCardToTap: ")
    }

    override fun onCardTap() {
        println("onCardTap: ")
    }

    override fun onCardConnected(paymentCard: PaymentCard) {
        println("onPaymentCardConnected: ")
        paymentCard.log()
        paymentCard.selectScheme(this)
    }

    override fun onCardSchemeSelected(paymentCard: PaymentCard) {
        println("onSchemeSelected: ")
        paymentCard.invokeKernel()
    }

    override fun supportedSchemeNotFound() {
        println("supportedSchemeNotFound: ")
    }

    override fun onCardProcessComplete(paymentCard: PaymentCard) {
        println("onCardProcessComplete: ")
        if(paymentCard.isSuccess() == true){
            cardSuccess(paymentCard = paymentCard)
        }
        runOnUiThread {
            onCardProcessingComplete(paymentCard, ::serverProcessComplete)
        }
    }

    override fun onTerminalStatus(status: TerminalStatus) {
        binding.lightIndicator.handle(status)
        if(status == TerminalStatus.TRANSACTION_SUCCESS){
        }
    }

    override fun onError(throwable: EdfaException) {
        println("onError: ")
        Toast.makeText(this@ScanCardActivity, throwable.toString(), Toast.LENGTH_LONG).show()
    }

    companion object{
        val PARAMS = "TXN_PARAMS"
    }

    private fun cardSuccess(paymentCard: PaymentCard){
        binding.timoutTimer.stop()
        startRequestTimeoutCountdown()
        delay(500){
            animate(show = false)
            with(binding.schemeAnimation) {
                this.showAnimation()
                this.setupSpeedFor(paymentCard.schemeAnimation())
                this.setAnimation(paymentCard.schemeAnimation())
                this.addAnimatorListener(object : Animator.AnimatorListener{
                    override fun onAnimationCancel(anim: Animator) {}
                    override fun onAnimationRepeat(anim: Animator) {}
                    override fun onAnimationStart(anim: Animator) {
                        Player.play(context, paymentCard.schemeSound())
                    }

                    override fun onAnimationEnd(anim: Animator) {
                        binding.viewReqTimeout.root.showAnimation()
                        binding.schemeAnimation.hideAnimation()
                    }

                })
            }
        }
    }

    private fun startRequestTimeoutCountdown(){
        runOnUiThread{
            with(binding.viewReqTimeout) {
                root.visibility = View.GONE

                txtCountdown.start(REQUEST_TIMEOUT.toLong())
                progress.max = REQUEST_TIMEOUT
                txtCountdown.setOnCountdownIntervalListener(10){ view, remainTime ->
                    progress.setProgressCompat(remainTime.toInt(), true)
                }
                txtCountdown.setOnCountdownEndListener {
                    OnRequestTimerEnd()
                    finish()
                }
            }
        }
    }

    private fun serverProcessComplete(status: Boolean, statusAcknowlege: () -> Unit){
        with(binding.viewReqTimeout) {
            txtCountdown.stop()
            when (status) {
                true -> statusAnimation.success {
                    statusAcknowlege()
                    finish()
                }
                false -> statusAnimation.failed {
                    statusAcknowlege()
                    finish()
                }
            }
        }
    }

    fun animate(show:Boolean){
        val views = listOf(binding.view1,binding.view2,binding.view3,binding.view4,binding.view5, binding.view6)
        when (show) {
            true -> views.showAnimation(this){

            }
            false -> views.hideAnimation(this){
            }
        }
    }
}