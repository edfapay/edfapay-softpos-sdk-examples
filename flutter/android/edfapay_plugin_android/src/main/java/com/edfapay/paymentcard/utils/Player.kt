package com.edfapay.paymentcard.utils

import android.content.Context
import android.media.MediaPlayer
import android.media.PlaybackParams
import com.edfapay.paymentcard.R

private var player: MediaPlayer? = null
object Player {
    fun play(context: Context, res:Int, speed:Float? = null){
        Thread {
            with(PlaybackParams()) {
                this.speed = speed?: speed(res)

                player = MediaPlayer.create(context, res)
                player?.playbackParams = this
                player?.setVolume(100F, 100F)
                player?.start()
            }
        }.start()
    }

    private fun speed(res:Int) : Float{
        return if(res == R.raw.visa_sound){
            1f
        }else if(res == R.raw.mastercard_sound){
            1f
        }else if(res == R.raw.applepay){
            1f
        }else if(res == R.raw.pos_indicator_beep){
            1f
        }else if(res == R.raw.pos_indicator_beep_twice){
            1f
        }else
            1f
    }
}