package com.edfapay.paymentcard.crypto

import java.math.BigInteger
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.DESKeySpec
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.or
import kotlin.math.ceil

object Dukpt {
    private const val NUM_OVERWRITES = 3

    @Throws(Exception::class)
    internal fun computeKey(
        ipek: BitSet, ksn: BitSet, keyRegisterBitmask: BitSet, dataVariantBitmask: BitSet
    ): ByteArray {
        val key = getCurrentKey(ipek, ksn, keyRegisterBitmask, dataVariantBitmask)
        val rKey = toByteArray(key)

        obliviate(ksn)
        obliviate(ipek)
        obliviate(key)
        return rKey
    }

    internal fun generateAmexKey(
        initialKey: BitSet, keyID: BitSet, keyRegisterBitmask: BitSet
    ): BitSet {
        var key = initialKey[0, initialKey.bitSize()]
        val counter = BitSet(keyID.bitSize())

        for (i in 0 until keyID.bitSize()) {
            if (keyID[i]) {
                counter.set(i)
                val tmp =
                    nonReversibleKeyGenerationProcess(key, counter, keyRegisterBitmask, true)
                obliviate(key)
                key = tmp
            }
        }
        obliviate(counter)
        return key
    }

    @Throws(Exception::class)
    internal fun getCurrentKey(
        initialKey: BitSet, ksn: BitSet, keyRegisterBitmask: BitSet,
        dataVariantBitmask: BitSet
    ): BitSet {
        var key = initialKey[0, initialKey.bitSize()]
        val counter = ksn[0, ksn.bitSize()]
        counter.clear(59, ksn.bitSize())
        for (i in 59 until ksn.bitSize()) {
            if (ksn[i]) {
                counter.set(i)
                val tmp =
                    nonReversibleKeyGenerationProcess(key, counter[16, 80], keyRegisterBitmask)
                obliviate(key)
                key = tmp
            }
        }
        key.xor(dataVariantBitmask)

        obliviate(counter)
        return key
    }

    @Throws(Exception::class)
    private fun nonReversibleKeyGenerationProcess(
        p_key: BitSet, data: BitSet, keyRegisterBitmask: BitSet, padding: Boolean = false
    ): BitSet {
        val keyreg = p_key[0, p_key.bitSize()]
        var reg1 = data[0, data.bitSize()]
        var reg2 = reg1[0, 64]
        reg2.xor(keyreg[64, 128])
        reg2 = toBitSet(encryptDes(toByteArray(keyreg[0, 64]), toByteArray(reg2), padding))
        reg2.xor(keyreg[64, 128])

        keyreg.xor(keyRegisterBitmask)
        reg1.xor(keyreg[64, 128])
        reg1 = toBitSet(encryptDes(toByteArray(keyreg[0, 64]), toByteArray(reg1), padding))
        reg1.xor(keyreg[64, 128])

        val reg1b = toByteArray(reg1)
        val reg2b = toByteArray(reg2)
        val key = concat(reg1b, reg2b)
        val rkey = toBitSet(key)

        obliviate(reg1)
        obliviate(reg2)
        obliviate(reg1b)
        obliviate(reg2b)
        obliviate(key)
        obliviate(keyreg)
        return rkey
    }

    @JvmOverloads
    @Throws(Exception::class)
    internal fun encryptDes(
        key: ByteArray, data: ByteArray, padding: Boolean = false
    ): ByteArray {
        val iv = IvParameterSpec(ByteArray(8))
        val encryptKey = SecretKeyFactory.getInstance("DES").generateSecret(DESKeySpec(key))
        val encryptor: Cipher = if (padding) {
            Cipher.getInstance("DES/CBC/PKCS5Padding")
        } else {
            Cipher.getInstance("DES/CBC/NoPadding")
        }
        encryptor.init(Cipher.ENCRYPT_MODE, encryptKey, iv)
        return encryptor.doFinal(data)
    }

    @JvmOverloads
    @Throws(Exception::class)
    internal fun encryptAes(
        key: ByteArray, data: ByteArray, padding: Boolean = false
    ): ByteArray {
        val iv = IvParameterSpec(ByteArray(16))
        val encryptKey = SecretKeySpec(key, "AES")
        val encryptor: Cipher = if (padding) {
            Cipher.getInstance("AES/CBC/PKCS5Padding")
        } else {
            Cipher.getInstance("AES/CBC/NoPadding")
        }
        encryptor.init(Cipher.ENCRYPT_MODE, encryptKey, iv)
        return encryptor.doFinal(data)
    }

    @Throws(Exception::class)
    internal fun decryptAes(key: ByteArray, data: ByteArray, padding: Boolean): ByteArray {
        val iv = IvParameterSpec(ByteArray(16))
        val decryptKey = SecretKeySpec(key, "AES")
        val decryptor: Cipher = if (padding) {
            Cipher.getInstance("AES/CBC/PKCS5Padding")
        } else {
            Cipher.getInstance("AES/CBC/NoPadding")
        }
        decryptor.init(Cipher.DECRYPT_MODE, decryptKey, iv)
        return decryptor.doFinal(data)
    }

    internal fun toBitSet(b: ByteArray): BitSet {
        val bs = BitSet(8 * b.size)
        for (i in b.indices) {
            for (j in 0..7) {
                if ((b[i].toLong() and (1L shl j)) > 0L)
                    bs.set(8 * i + (7 - j))
            }
        }
        return bs
    }

    private fun toByte(b: BitSet): Byte {
        var value: Byte = 0
        for (i in 0 until b.bitSize()) {
            if (b[i]) value = (value or (1L shl (7 - i)).toByte())
        }
        return value
    }

    internal fun toByteArray(b: BitSet): ByteArray {
        val size = ceil(b.bitSize() / 8.0).toInt()
        val value = ByteArray(size)
        for (i in 0 until size) {
            value[i] = toByte(b[i * 8, b.bitSize().coerceAtMost((i + 1) * 8)])
        }
        return value
    }

    internal fun toByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] =
                ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    internal fun toHex(bytes: ByteArray): String {
        val bi = BigInteger(1, bytes)
        return String.format("%0" + (bytes.size shl 1) + "X", bi)
    }

    private fun concat(a: ByteArray, b: ByteArray): ByteArray {
        val c = ByteArray(a.size + b.size)
        System.arraycopy(a, 0, c, 0, a.size)
        System.arraycopy(b, 0, c, a.size, b.size)
        return c
    }

    private fun obliviate(b: BitSet) = obliviate(b, NUM_OVERWRITES)

    private fun obliviate(b: ByteArray) = obliviate(b, NUM_OVERWRITES)

    private fun obliviate(b: BitSet, n: Int) {
        val r = SecureRandom()
        for (i in 0 until n) {
            for (j in 0 until b.bitSize()) {
                b[j] = r.nextBoolean()
            }
        }
    }

    private fun obliviate(b: ByteArray, n: Int) {
        for (i in 0 until n) {
            b[i] = 0x00
            b[i] = 0x01
        }
        val r = SecureRandom()
        for (i in 0 until n) {
            r.nextBytes(b)
        }
    }

}


internal class BitSet : java.util.BitSet {

    private var size: Int

    constructor() : super(DEFAULT_SIZE) {
        size = DEFAULT_SIZE
    }

    constructor(nBits: Int) : super(nBits) {
        size = nBits
    }

    override fun get(low: Int, high: Int): BitSet {
        val n = BitSet(high - low)
        for (i in 0 until (high - low)) {
            n[i] = this[low + i]
        }
        return n
    }

    internal fun bitSize(): Int {
        return size
    }

    companion object {
        private const val DEFAULT_SIZE = 8
        private const val serialVersionUID = 1L
    }
}