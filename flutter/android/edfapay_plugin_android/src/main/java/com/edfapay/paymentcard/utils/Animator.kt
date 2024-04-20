package com.edfapay.paymentcard

import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation


fun View.alphaAnimator(start:(()->Unit)? = null, end:(()->Unit)? = null, duration:Long = 500, startOffset:Long = 50, repeatCount:Int = 1){
    val anim: Animation = AlphaAnimation(0.0f, 1.0f)
    anim.setDuration(duration) //You can manage the blinking time with this parameter
    anim.setStartOffset(startOffset)
    anim.setRepeatMode(Animation.REVERSE)
    anim.setRepeatCount(repeatCount)
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) { if(start != null ) start() }
        override fun onAnimationEnd(animation: Animation?) { if(end != null ) end() }
        override fun onAnimationRepeat(animation: Animation?) {}
    })
    startAnimation(anim)
}

fun View.scaleAnimator(scale:Float = 1.5f, start:(()->Unit)? = null, end:(()->Unit)? = null, duration:Long = 500, startOffset:Long = 50, repeatCount:Int = 1){
    val anim: Animation = ScaleAnimation(
        scale,
        1.0f,
        scale,
        1.0f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )
    anim.setDuration(duration) //You can manage the blinking time with this parameter
    anim.setStartOffset(startOffset)
    anim.setRepeatMode(Animation.REVERSE)
    anim.setRepeatCount(repeatCount)
    anim.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) { if(start != null ) start() }
        override fun onAnimationEnd(animation: Animation?) { if(end != null ) end() }
        override fun onAnimationRepeat(animation: Animation?) {}
    })
    startAnimation(anim)
}

fun Animation.onComplete(callback:() -> Unit){
    setAnimationListener(object : Animation.AnimationListener{
        override fun onAnimationStart(p0: Animation?) {}
        override fun onAnimationRepeat(p0: Animation?) {}
        override fun onAnimationEnd(p0: Animation?) {
            callback()
        }
    })
}


fun List<View>.showAnimation(context:Context, completion:() -> Unit){
    val showAnim = AnimationUtils.loadAnimation(context, R.anim.show_animation)
    showAnim.onComplete(completion)
    forEach {
        it.visibility = View.VISIBLE
        it.startAnimation(showAnim)
    }
}
fun List<View>.hideAnimation(context:Context, completion:() -> Unit){
    val hideAnim = AnimationUtils.loadAnimation(context, R.anim.hide_animation)
    hideAnim.onComplete {
        forEach { it.visibility = View.GONE }
        completion()
    }
    forEach {
        it.visibility = View.VISIBLE
        it.startAnimation(hideAnim)
    }

}

fun View.hideAnimation() {
    val animation = AnimationUtils.loadAnimation(context, R.anim.hide_animation)
    animation.onComplete{
        visibility = View.GONE
    }
    startAnimation(animation)
}
fun View.showAnimation() {
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.show_animation)
    startAnimation(animation)
}

fun View.clockwise() {
    visibility = View.VISIBLE
    val animation = AnimationUtils.loadAnimation(context, R.anim.clockwise)
    startAnimation(animation)
}
fun View.zoomIn() {
    visibility = View.VISIBLE
    val animation1 = AnimationUtils.loadAnimation(context, R.anim.zoom_in)
    startAnimation(animation1)
}
fun View.zoomOut() {
    visibility = View.VISIBLE
    val animation1 = AnimationUtils.loadAnimation(context, R.anim.zoom_out)
    startAnimation(animation1)
}
fun View.fadeIn() {
    visibility = View.VISIBLE
    val animation1 = AnimationUtils.loadAnimation(context, R.anim.fade_in)
    startAnimation(animation1)
}
fun View.fadeOut() {
    visibility = View.VISIBLE
    val animation1 = AnimationUtils.loadAnimation(context, R.anim.fade_out)
    startAnimation(animation1)
}
fun View.slide() {
    visibility = View.VISIBLE
    val animation1 = AnimationUtils.loadAnimation(context, R.anim.slide)
    startAnimation(animation1)
}
fun View.move() {
    visibility = View.VISIBLE
    val animation1 = AnimationUtils.loadAnimation(context, R.anim.move)
    startAnimation(animation1)
}
fun View.blink() {
    visibility = View.VISIBLE
    val animation1 = AnimationUtils.loadAnimation(context, R.anim.blink)
    startAnimation(animation1)
}