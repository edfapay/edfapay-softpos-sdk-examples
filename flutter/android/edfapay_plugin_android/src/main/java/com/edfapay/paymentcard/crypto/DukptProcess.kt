package com.edfapay.paymentcard.crypto

import com.mastercard.terminalsdk.utility.ByteUtility
import org.apache.commons.lang.StringUtils

object DukptProcess {
    private const val KEY_REGISTER_BITMASK = "C0C0C0C000000000C0C0C0C000000000"
    private val DEFAULT_KEY_REGISTER_BITMASK = Dukpt.toBitSet(
        Dukpt.toByteArray(KEY_REGISTER_BITMASK)
    )

    @Throws(Exception::class)
    internal fun generateProcessingKey(
        initialKey: String, ksn: String, variantBitMask: String
    ): ByteArray {
        val initialKeyBitSet = Dukpt.toBitSet(Dukpt.toByteArray(initialKey))
        val ksnBitSet = Dukpt.toBitSet(Dukpt.toByteArray(ksn))
        val variantBitSet = Dukpt.toBitSet(Dukpt.toByteArray(variantBitMask))
        return Dukpt.computeKey(
            initialKeyBitSet, ksnBitSet, DEFAULT_KEY_REGISTER_BITMASK, variantBitSet
        )
    }

    @Throws(Exception::class)
    internal fun panBlock(pan: String, panKey: String): ByteArray {
        var xPan = pan
        if (xPan.length < 12)
            xPan = StringUtils.leftPad(xPan, 12, '0')

        val panData: String = (xPan.length - 12).toString() + xPan
        val panBlock = StringUtils.rightPad(panData, 32, '0')
        return Dukpt.encryptAes(Dukpt.toByteArray(panKey), Dukpt.toByteArray(panBlock))
    }

    @Throws(Exception::class)
    internal fun track2DataEncryption(track2Data: String, panKey: ByteArray): ByteArray =
        Dukpt.encryptAes(panKey, Dukpt.toByteArray(track2Data), true)

    @Throws(Exception::class)
    internal fun iccEncryption(icc: String, panKey: ByteArray): ByteArray =
        Dukpt.encryptAes(panKey, Dukpt.toByteArray(icc), true)

    @Throws(Exception::class)
    internal fun amexKeyEncryption(
        initialKey: BitSet, plainData: ByteArray, keyID: Int
    ): ByteArray {
        val keyIDAsByteArray = ByteUtility.intToByteArray(keyID)
        val keyGenerator = Dukpt.toByteArray(
            Dukpt.generateAmexKey(
                initialKey, Dukpt.toBitSet(keyIDAsByteArray), DEFAULT_KEY_REGISTER_BITMASK
            )
        )

        return Dukpt.encryptAes(keyGenerator, plainData, true)
    }

    @Throws(Exception::class)
    internal fun amexKeyDecryption(
        initialKey: BitSet, encryptedData: ByteArray, keyID: Int
    ): ByteArray {
        val keyIDAsByteArray = ByteUtility.intToByteArray(keyID)
        val keyGenerator = Dukpt.toByteArray(
            Dukpt.generateAmexKey(
                initialKey, Dukpt.toBitSet(keyIDAsByteArray), DEFAULT_KEY_REGISTER_BITMASK
            )
        )

        return Dukpt.decryptAes(keyGenerator, encryptedData, true)
    }

}