package com.edfapay.paymentcard.kernel_integration

import com.mastercard.terminalsdk.objects.OutcomeParameterSet
import com.visa.app.ttpkernel.TtpOutcome
import javax.net.ssl.SSLEngineResult.Status

enum class TransactionStatus{
    ONLINE_REQUEST,
    DECLINED,
    SELECT_NEXT,
    TRY_AGAIN,
    ABORTED,
    ERROR,
    NA;

    companion object{
        fun forVisa(ttpOutcome: TtpOutcome)  = when (ttpOutcome) {
            TtpOutcome.COMPLETED -> ONLINE_REQUEST
            TtpOutcome.DECLINED -> DECLINED
            TtpOutcome.ABORTED -> ABORTED
            TtpOutcome.TRYNEXT -> SELECT_NEXT
            TtpOutcome.SELECTAGAIN -> TRY_AGAIN
            else -> ERROR
        }

        fun forMasterCard(ttpOutcome: OutcomeParameterSet) = when (ttpOutcome.status) {
            OutcomeParameterSet.Status.DECLINED -> DECLINED
            OutcomeParameterSet.Status.ONLINE_REQUEST -> ONLINE_REQUEST
            OutcomeParameterSet.Status.END_APPLICATION -> ABORTED
            OutcomeParameterSet.Status.SELECT_NEXT -> SELECT_NEXT
            OutcomeParameterSet.Status.TRY_AGAIN -> TRY_AGAIN
            OutcomeParameterSet.Status.TRY_ANOTHER_INTERFACE -> ABORTED
            OutcomeParameterSet.Status.APPROVED -> NA
            OutcomeParameterSet.Status.NA -> NA
        }
    }
}


enum class CVMStatus{
    NO_CVM,
    ONLINE_PIN,
    SIGNATURE,
    NA
}

data class KernelResponse(var kernel: String?){
    var aid: String? = null
    var status: TransactionStatus? = null
    var cvmStatus: CVMStatus? = null
    var sequenceNumber: String? = null
    var track2Data: String? = null
    var icc: String? = null
    var cryptogram: String? = null
    var cryptogramData: String? = null
    var tvr: String? = null
    var ctq: String? = null
    var ttq: String? = null
    var cvm: String? = null
    var cardholderName: String? = null

    var allIccTags:Map<String, String> = mapOf()

    val isSuccess get() = status == TransactionStatus.ONLINE_REQUEST

}