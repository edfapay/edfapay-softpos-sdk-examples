package com.edfapay.paymentcard.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.os.Handler
import android.os.Looper
import com.edfapay.paymentcard.R


fun delay(millis:Long, completion:() -> Unit){
    Handler(Looper.getMainLooper()).postDelayed(completion, millis)
}