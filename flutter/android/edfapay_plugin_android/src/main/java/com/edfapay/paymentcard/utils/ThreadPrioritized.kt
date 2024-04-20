package com.edfapay.paymentcard.utils

import android.os.Looper
import android.os.StrictMode
import java.util.logging.Handler

object ThreadPrioritized {
    fun run(block:()->Unit){

        System.gc()
        android.os.Handler(Looper.getMainLooper()).post {
            // why PMD suppression is needed: https://github.com/pmd/pmd/issues/808
            android.os.Process.setThreadPriority(Thread.MAX_PRIORITY); //NOPMD AccessorMethodGeneration
//            StrictMode.setThreadPolicy(
//                StrictMode.ThreadPolicy.Builder()
//                    .detectNetwork()
//                    .penaltyDeath()
//                    .penaltyLog()
//                    .build()
//            )
            block()
        }
    }
}