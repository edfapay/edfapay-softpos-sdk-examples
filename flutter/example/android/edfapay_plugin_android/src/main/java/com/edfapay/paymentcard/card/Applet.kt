package com.edfapay.paymentcard.card

import android.util.Log
import io.github.binaryfoo.DecodedData
import io.github.binaryfoo.findForTag
import io.github.binaryfoo.tlv.Tag


private val tag4f = Tag.fromHex("4f") //  Application Identifier (AID) – card
private val tag50 = Tag.fromHex("50") //  Application Label – card
private val tag87 = Tag.fromHex("87") //  Application Priority Indicator – card
data class Applet(var tlv:DecodedData){
    var aid:String? = tlv.children.findForTag(tag4f)?.fullDecodedData
    var aidByteArray:ByteArray? = tlv.children.findForTag(tag4f)?.tlv?.getValue()
    var scheme:PaymentScheme? = aid?.let { PaymentScheme.findBy(it) }
    var label:String = tlv.children.findForTag(tag50)?.fullDecodedData ?: ""
    var priority:String = tlv.children.findForTag(tag87)?.fullDecodedData ?: "0"


    override fun toString(): String {
        return """
            AppTemplate(
                aid:$aid,
                scheme: $scheme,
                label: $label,
                priority: $priority,
                tlv: ${tlv}
            )
        """.trimIndent()
    }
}

fun List<Applet>.validate(): MutableList<Applet> {
    return with(toMutableList()) {
        removeAll {
            val res = it.aid == null || it.aid!!.isEmpty() || it.scheme == null
            if(res)
                Log.v("Applet.validate", "(Removed) Invalid applet found [AID:${it.aid}, SCHEME:${it.scheme}]")
            res
        }
        this
    }
}

fun List<Applet>.aids() = validate().map { it.aid }