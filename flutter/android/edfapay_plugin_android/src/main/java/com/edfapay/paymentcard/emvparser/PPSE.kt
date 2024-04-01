package com.edfapay.paymentcard.emvparser

import android.nfc.tech.IsoDep
import com.edfapay.paymentcard.EdfaException
import com.edfapay.paymentcard.Error
import com.edfapay.paymentcard.card.Applet
import com.edfapay.paymentcard.card.PaymentCard
import com.edfapay.paymentcard.card.validate
import com.edfapay.paymentcard.model.TxnParams
import io.github.binaryfoo.*
import io.github.binaryfoo.tlv.ISOUtil
import io.github.binaryfoo.tlv.Tag


// Proximity Payment System Environment (PPSE)
class PPSE {

    class Select(private val isoDep: IsoDep){
        private var response:Response? = null
        private val SELECT_COMMAND = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04, 0x00, 0x0E.toByte(), 0x32.toByte(), 0x50.toByte(),
            0x41.toByte(), 0x59.toByte(), 0x2E.toByte(), 0x53.toByte(), 0x59.toByte(),
            0x53.toByte(), 0x2E.toByte(), 0x44.toByte(), 0x44.toByte(), 0x46.toByte(),
            0x30.toByte(), 0x31.toByte(), 0x00
        )

        init {
            when (isoDep.isConnected) {
                true -> {
                    with(isoDep.transceive(SELECT_COMMAND)) {
                        kotlin.runCatching {
                            val bytes = this
                            with(ISOUtil.hexString(this)) {
                                val hex = this.uppercase()
                                val decoded = RootDecoder().decode(this, "EMV", "constructed")
                                response = Response(
                                    rawBytes = bytes,
                                    rawHex = hex,
                                    decoded = decoded
                                )
                            }

                        }.onFailure {
                            throw EdfaException(type = Error.TLV_DECODING_WHILE_PPSE_SELECT, it)
                        }
                    }
                }
                false -> throw EdfaException(type = Error.CARD_DISCONNECTED_WHILE_PROCESSING)
            }
        }

        fun paymentCard(transactionParameters: TxnParams): PaymentCard{
            if(response?.decoded == null)
                throw EdfaException(Error.TLV_DECODED_NULL_WHILE_PPSE_SELECT)

            val paymentCard = createPaymentCard(isoDep, response!!)
            paymentCard.transactionParameters = transactionParameters
            return paymentCard
        }


        private val tag61 = Tag.fromHex("61") //  Application Template
        private fun createPaymentCard(isoDep: IsoDep, response:Response) : PaymentCard{
            try {
                val appletsTlv = response.decoded.findAllForTag(tag61)
                if (appletsTlv.isEmpty())
                    throw Exception("(Invalid Card) No AID exist")

                val applets = appletsTlv.map { Applet(it) }.validate()
                applets.sortBy { it.priority }

                if(applets.isEmpty())
                    throw Exception("(Invalid Card) No valid AID exist")

                return PaymentCard(isoDep, response, applets)
            }catch (e:Exception){
                throw EdfaException(Error.PROCESSING_AID_WHILE_PPSE_SELECT.by(e), e)
            }
        }
    }

    data class Response(
        val rawBytes:ByteArray,
        val rawHex:String,
        val decoded:List<DecodedData>
    )
}




























