package com.edfapay.paymentcard.ui

import android.content.Context
import android.content.res.ColorStateList
import android.media.MediaPlayer
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import com.edfapay.paymentcard.R
import com.edfapay.paymentcard.databinding.PosStatusIndicatorBinding
import com.edfapay.paymentcard.model.TerminalStatus
import com.edfapay.paymentcard.scaleAnimator
import com.edfapay.paymentcard.utils.Player
import kotlinx.coroutines.*


class PosStatusIndicator : RelativeLayout{

    private val TAG: String = "PosStatusIndicator"

    private var busyJob: Job? = null
    private var idleJob: Job? = null

    var binding: PosStatusIndicatorBinding

    var mediaPlayer: MediaPlayer? = null


    constructor(context: Context) : super(context) {
        binding = PosStatusIndicatorBinding.inflate(LayoutInflater.from(context), this, true)
    }


    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        binding = PosStatusIndicatorBinding.inflate(LayoutInflater.from(context), this, true)
    }

    init {
        binding = PosStatusIndicatorBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
    }

    fun handle(status:TerminalStatus){
        Log.e("TerminalStatus", status.name)
        when (status) {
            TerminalStatus.ERROR -> terminalError()
            TerminalStatus.READY -> ready()
            TerminalStatus.WAITING_FOR_CARD -> waitingForCard()
            TerminalStatus.CARD_TAPPED -> cardTapped()
            TerminalStatus.CARD_CONNECT_FAILURE -> cardError()
            TerminalStatus.CARD_CONNECTING,
            TerminalStatus.CARD_CONNECT_SUCCESS,
            TerminalStatus.CARD_SCHEMES_FETCHING,
            TerminalStatus.CARD_SCHEMES_FOUND,
            TerminalStatus.CARD_SCHEMES_SORTED,
            TerminalStatus.CARD_SCHEMES_FILTERED,
            TerminalStatus.CARD_SCHEME_SELECTED -> busy()
            TerminalStatus.TRANSACTION_STARTED -> processing()
            TerminalStatus.TRANSACTION_PROCESSING -> processing()
            TerminalStatus.TRANSACTION_SUCCESS -> txnSuccess()
            TerminalStatus.TRANSACTION_FAILED -> txnError()
        }
    }

    fun ready(){
        on(binding.light1, R.color.orange_indicator)
        off(binding.light2)
        off(binding.light3)
        off(binding.light4)
    }

    fun waitingForCard(){
        on(binding.light1, R.color.green_indicator)
        off(binding.light2)
        off(binding.light3)
        off(binding.light4)
        playSound(R.raw.pos_indicator_beep)
    }

    fun cardTapped(){
        on(binding.light1, R.color.green_indicator)
        on(binding.light2, R.color.orange_indicator)
        off(binding.light3)
        off(binding.light4)
        playSound(R.raw.pos_indicator_beep)
    }

    fun busy(){
        on(binding.light1, R.color.green_indicator)
        on(binding.light2, R.color.green_indicator)
        on(binding.light3, R.color.orange_indicator)
        off(binding.light4)
    }

    fun processing(){
        on(binding.light1, R.color.green_indicator)
        on(binding.light2, R.color.green_indicator)
        on(binding.light3, R.color.green_indicator)
        on(binding.light4, R.color.orange_indicator)
    }

    fun cardError(){
        on(binding.light1, R.color.green_indicator)
        on(binding.light2, R.color.green_indicator)
        on(binding.light3, R.color.red_indicator)
        off(binding.light4)
        playSound(R.raw.pos_indicator_beep_twice)
    }

    fun txnError(){
        on(binding.light1, R.color.green_indicator)
        on(binding.light2, R.color.green_indicator)
        on(binding.light3, R.color.green_indicator)
        on(binding.light4, R.color.red_indicator)
        playSound(R.raw.pos_indicator_beep_twice)
    }

    fun txnSuccess(){
        on(binding.light1, R.color.green_indicator)
        on(binding.light2, R.color.green_indicator)
        on(binding.light3, R.color.green_indicator)
        on(binding.light4, R.color.green_indicator)
        playSound(R.raw.pos_indicator_beep)
    }

    fun terminalError(){
        on(binding.light1, R.color.green_indicator)
        on(binding.light2, R.color.green_indicator)
        on(binding.light3, R.color.green_indicator)
        on(binding.light4, R.color.red_indicator)
        playSound(R.raw.pos_indicator_beep_twice)
    }

    fun schemeNotConfigured(){
        on(binding.light1, R.color.red_indicator)
        on(binding.light2, R.color.red_indicator)
        on(binding.light3, R.color.red_indicator)
        on(binding.light4, R.color.red_indicator)
        playSound(R.raw.pos_indicator_beep_twice)
    }


    fun schemeNotSupported(){
        on(binding.light1, R.color.red_indicator)
        on(binding.light2, R.color.red_indicator)
        on(binding.light3, R.color.red_indicator)
        on(binding.light4, R.color.red_indicator)
        playSound(R.raw.pos_indicator_beep_twice)
    }


    private fun off(lightCard: View){
        CoroutineScope(Dispatchers.Main).launch {
            busyJob?.job?.cancel()
            lightCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_grey))
        }
    }

    private fun on(lightCard: View, color:Int){
        CoroutineScope(Dispatchers.Main).launch {
            lightCard.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, color))
        }
    }

    private fun animate(lightCard: View, color:Int, repeatCount:Int = Animation.INFINITE){
        GlobalScope.launch(Dispatchers.Main) {
            lightCard.backgroundTintList = ColorStateList.valueOf(color)
            lightCard.outlineAmbientShadowColor = ContextCompat.getColor(context, color)
            lightCard.outlineSpotShadowColor = ContextCompat.getColor(context, color)
            lightCard.scaleAnimator(repeatCount = repeatCount, duration = 300)
        }
    }

    private fun playSound(sound:Int){
        Player.play(context, sound)
    }
}