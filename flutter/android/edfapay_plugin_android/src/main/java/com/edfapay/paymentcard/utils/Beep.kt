package com.edfapay.paymentcard.utils

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

class Beeper{

    companion object{

        fun once(duration: Int = 200)
        {
            // First Tone
            val toneG = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            toneG.startTone(ToneGenerator.TONE_DTMF_0, duration)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                toneG.release()
            }, (duration + 50).toLong())
        }

        fun twice()
        {
            val duration = 200

            // First Tone
            var tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            tone.startTone(ToneGenerator.TONE_DTMF_0, duration)
            var handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                tone.release()

                // Second Tone
                tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                tone.startTone(ToneGenerator.TONE_DTMF_0, duration)
                handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    tone.release()

                }, (duration + 30).toLong())

            }, (duration + 30).toLong())
        }

        fun thrice()
        {
            val duration = 200

            // First Tone
            var tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
            tone.startTone(ToneGenerator.TONE_DTMF_0, duration)
            var handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                tone.release()

                // Second Tone
                tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                tone.startTone(ToneGenerator.TONE_DTMF_0, duration)
                handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    tone.release()

                    // Third Tone
                    tone = ToneGenerator(AudioManager.STREAM_MUSIC, 100)
                    tone.startTone(ToneGenerator.TONE_DTMF_0, duration)
                    handler = Handler(Looper.getMainLooper())
                    handler.postDelayed({
                        tone.release()

                    }, (duration + 30).toLong())

                }, (duration + 30).toLong())

            }, (duration + 30).toLong())
        }
    }
}