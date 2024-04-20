package com.edfapay.paymentcard.utils

import android.animation.Animator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.edfapay.paymentcard.R
import com.edfapay.paymentcard.card.PaymentScheme
import java.io.Serializable

inline fun <reified T : java.io.Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}

fun List<PaymentScheme>.visibilityOf(scheme: PaymentScheme) = when (contains(scheme)) {
    true -> View.VISIBLE
    false -> View.GONE
}

fun LottieAnimationView.onEnd(callback:() -> Unit){
    addAnimatorListener(object : Animator.AnimatorListener{
        override fun onAnimationStart(p0: Animator) {}
        override fun onAnimationCancel(p0: Animator) {}
        override fun onAnimationRepeat(p0: Animator) {}
        override fun onAnimationEnd(p0: Animator) { callback() }
    })
}

fun LottieAnimationView.success(onEnd:() -> Unit){
    this.visibility = View.VISIBLE
    this.setAnimation(R.raw.success_animation)
    this.onEnd(onEnd)
}

fun LottieAnimationView.failed(onEnd:() -> Unit){
    this.visibility = View.VISIBLE
    this.setAnimation(R.raw.failed_animation)
    this.onEnd(onEnd)
}
fun LottieAnimationView.setupSpeedFor(res:Int){
    duration
    speed = if(res == R.raw.visa_animation){
        0.7f
    }else if(res == R.raw.mada_animation){
        0.7f
    }else if(res == R.raw.generic_scheme_animation){
        0.7f
    }else if(res == R.raw.mastercard_animation){
        0.7f
    }else if(res == R.raw.amex_animation){
        0.7f
    }else{
        1f
    }
}