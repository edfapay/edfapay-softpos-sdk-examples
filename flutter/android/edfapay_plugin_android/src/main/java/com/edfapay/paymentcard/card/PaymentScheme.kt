package com.edfapay.paymentcard.card

import android.util.Log
import com.edfapay.paymentcard.R
import io.github.binaryfoo.DecodedData
import java.util.*

enum class PaymentScheme(
    internal var ridsString: List<String>, var kernel:String?, var schemeID: String, var networkName: String, var label: String = "", var priority:Int = 0
) {

    MADA(aidsMada, "2D","P1", "mada"),
    MADA_VISA(aidsMadaVisa, "03","P1", "mada_visa"),
    MADA_MASTERCARD(aidsMadaMastercard,"02", "P1", "mada_mastercard"),

    VISA(aidsVisa, "03","VC", "visa"),
    MASTERCARD(aidsMastercard,"02", "MC", "mastercard"),
    MAESTRO(aidsMaestro,"02", "DM", "maestro"),

    UNION_PAY(aidsUnionPay, "","UP", "unionpay"),
    PAY_PAK(aidsPayPak, "","PP", "paypak"),

    AMEX(aidsAmex, "05","AX", "amex"),
    DISCOVER(aidsDiscover,"", "DC", "discover"),
    MEEZA(aidsMeeza,"", "Meeza", "meeza"),
    UNKNOWN(emptyList(), "","", "unknown");

    companion object {
        fun findBy(aid: String): PaymentScheme {
            REVERSE_LOOKUP.forEach {
                if (aid.contentEquals(it.key)) {
                    return it.value
                }
            }
            return UNKNOWN
        }

        fun findManyBy(aids: List<String?>): List<PaymentScheme> {
            val list = mutableListOf<PaymentScheme>()
            REVERSE_LOOKUP.forEach {
                if (aids.contains(it.key)) {
                    list.add( it.value)
                    if(list.size == aids.size)
                        return list
                }
            }
            return list
        }

        private val REVERSE_LOOKUP: MutableMap<String, PaymentScheme> = HashMap()

        init {
            EnumSet.allOf(PaymentScheme::class.java).forEach { network ->
                network.ridsString.forEach {
                    REVERSE_LOOKUP[it] = network
                }
            }
        }


        fun getSchemeByID(schemeID: String) = values().firstOrNull { it.schemeID.lowercase() == schemeID.lowercase() }
    }

    fun sound() = when (this) {
        MADA -> R.raw.applepay
        MADA_VISA -> R.raw.visa_sound
        MADA_MASTERCARD -> R.raw.mastercard_sound
        VISA -> R.raw.visa_sound
        MASTERCARD -> R.raw.mastercard_sound
        MAESTRO -> R.raw.mastercard_sound
        UNION_PAY -> R.raw.applepay
        PAY_PAK -> R.raw.applepay
        AMEX -> R.raw.applepay
        DISCOVER -> R.raw.applepay
        MEEZA -> R.raw.applepay
        UNKNOWN -> R.raw.applepay
        else -> R.raw.applepay
    }

    fun animation() = when (this) {
        MADA -> R.raw.mada_animation
        MADA_VISA -> R.raw.visa_animation
        MADA_MASTERCARD -> R.raw.mastercard_animation
        VISA -> R.raw.visa_animation
        MASTERCARD -> R.raw.mastercard_animation
        MAESTRO -> R.raw.mastercard_animation
        UNION_PAY -> R.raw.generic_scheme_animation // https://lottiefiles.com/animations/credit-card-payment-PUitsSma6q
        PAY_PAK -> R.raw.generic_scheme_animation
        AMEX -> R.raw.generic_scheme_animation
        DISCOVER -> R.raw.generic_scheme_animation
        MEEZA -> R.raw.generic_scheme_animation
        UNKNOWN -> R.raw.generic_scheme_animation
        else -> R.raw.generic_scheme_animation
    }

}

fun MutableList<PaymentScheme>.selectScheme(supported:List<PaymentScheme>) : PaymentScheme?{
    val notSupported = filter { supported.contains(it).not() }

    Log.e("***EdfaPay***", "[PaymentScheme] Supported schemes by backend: $supported")
    Log.e("***EdfaPay***", "[PaymentScheme] All schemes in card: $this ")

    if(notSupported.isNotEmpty()){
        Log.e("***EdfaPay***", "[PaymentScheme] Unsupported schemes in card: $notSupported")
        this.removeAll(notSupported)
        Log.e("***EdfaPay***", "[PaymentScheme] Removed unsupported schemes: $this ")
    }

    this.sortBy { it.priority }
    Log.e("***EdfaPay***", "[PaymentScheme] Prioritized schemes: $this ")

    val selectedScheme = firstOrNull()
    Log.e("***EdfaPay***", "[PaymentScheme] Selected schemes: $selectedScheme ")
    return  selectedScheme
}

private val aidsMastercard = listOf(
    "A000000004",          //   mastercard
    "A000000010",           //   Europay International	Belgium	Maestro-CH
    "A0000000040000",     //  Mastercard International	United States	MasterCard Card Manager
    "A00000000401",         //  Mastercard International	United States	MasterCard PayPass
    "A0000000041010",       //  Mastercard International	United States	MasterCard Credit/Debit (Global)
    "A00000000410101213",   //  Mastercard International	United States	MasterCard Credit
    "A00000000410101215",   //  Mastercard International	United States	MasterCard Credit
    "A0000000041010BB5449435301",       //  Mastercard International	United States	[UNKNOWN]
    "A0000000041010C0000301",       //  Mastercard International	United States	Comptant (CarreFour Banque)
    "A0000000041010C0000302",       //  Mastercard International	United States	Credit Pass (CarreFour Banque)
    "A0000000041010C123456789",     // Test Card
    "A0000000042010",       //  Mastercard International	United States	MasterCard Specific
    "A0000000042203",       //  Mastercard International	United States	MasterCard Specific
    "A0000000043010",       //  Mastercard International	United States	MasterCard Specific
    "A0000000044010",       //  Mastercard International	United States	MasterCard Specific
    "A0000000045000",       //  Mastercard International	United States	MODS
    "A0000000045010",       //  Mastercard International	United States	MasterCard Specific
    "A0000000045555",       //  Mastercard International	United States	APDULogger
    "A0000000046000",       //  Mastercard International	United States	Cirrus
    "A0000000046010",       //  Mastercard International	United States	Cirrus
    "A0000000048002",       //  Mastercard International	United States	SecureCode Auth EMV-CAP
    "A0000000049999",      //  Mastercard International	United States	MasterCard PayPass??

    ///* Maestro
    "A0000000043060",       //  Mastercard International	United States	Maestro (Debit)
    "A000000004306001",     //  Mastercard International	United States	Maestro (Debit)
    "A0000000043060C123456789"
    // */
)

private val aidsMaestro = listOf(
    "A0000000043060",       //  Mastercard International	United States	Maestro (Debit)
    "A000000004306001",     //  Mastercard International	United States	Maestro (Debit)
    "A0000000043060C123456789"
)

private val aidsMadaMastercard = listOf(
    "A0000002281010"       //  Saudi Arabian Monetary Agency (SAMA)	Kingdom of Saudi Arabia	SPAN (M/Chip)
)

private val aidsMadaVisa = listOf(
    "A0000002282010",       //  Saudi Arabian Monetary Agency (SAMA)	Kingdom of Saudi Arabia	SPAN (VIS)
    "A00000022820101010"   //  Saudi Arabian Monetary Agency (SAMA)	Kingdom of Saudi Arabia	SPAN
)

private val aidsVisa = listOf(
    "A000000003",           //  visa
    "A0000000031010",       //	Visa International	United States	VISA Debit/Credit (Classic)
    "A000000003101001",
    "A000000003101002",
    "A0000000032010",       //  Visa International	United States	VISA Electron
    "A0000000032020",
    "A0000000033010",       // 	Visa International	United States	VISA Interlink
    "A0000000034010",       //  Visa International	United States	VISA Specific
    "A0000000035010",       //  Visa International	United States	VISA Specific	Visa Specific
    "A0000000036010",       //  Visa International	United States	Domestic Visa Cash Stored Value
    "A0000000036020",
    "A0000000038010",       //  Visa International	United States	VISA Plus
    "A0000000039010",
    "A000000003999910"
)

private val aidsMada = listOf(
    /*"A0000002281010",       //  Saudi Arabian Monetary Agency (SAMA)	Kingdom of Saudi Arabia	SPAN (M/Chip)
    "A0000002282010",       //  Saudi Arabian Monetary Agency (SAMA)	Kingdom of Saudi Arabia	SPAN (VIS)
    "A00000022820101010",   //  Saudi Arabian Monetary Agency (SAMA)	Kingdom of Saudi Arabia	SPAN
    */"A000000228"            //  mada
)

private val aidsMeeza = listOf(
    "A000000732100123"
)

private val aidsAmex = listOf(
    "A000000025",           //  American Express	United Kingdom	American Express
    "A0000000250000",       //  American Express	United Kingdom	American Express (Credit/Debit)
    "A00000002501",         //  American Express	United Kingdom	AEIPS-compliant (A-E contact EMV) payment application
    "A000000025010104",     //  American Express	United Kingdom	American Express
    "A000000025010402",     //  American Express	United Kingdom	American Express
    "A000000025010701",     //  American Express	United Kingdom	ExpressPay
    "A000000025010801",      //  American Express	United Kingdom	American Express
    "A000000025020000",
    // For Testing.
    "A000000025010403",
    "A000000025040303",
    "A000000025010901"
)

private val aidsDiscover = listOf<String>(
)

private val aidsPayPak = listOf<String>(
)

private val aidsUnionPay = listOf(
    "A000000333010101",
    "A000000333010102",
    "A000000333010103",
    "A000000333010106",
    "A000000333010108",
)
