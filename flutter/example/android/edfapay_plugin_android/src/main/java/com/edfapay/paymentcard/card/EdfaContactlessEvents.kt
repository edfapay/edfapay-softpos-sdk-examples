package com.edfapay.paymentcard.card

import android.provider.ContactsContract.CommonDataKinds.Contactables
import androidx.annotation.RestrictTo
import com.edfapay.paymentcard.EdfaException
import com.edfapay.paymentcard.kernel_integration.EdfaContactless
import com.edfapay.paymentcard.model.NfcStatus
import com.edfapay.paymentcard.model.TerminalStatus
import com.edfapay.paymentcard.model.TerminalStatus.*
import com.edfapay.paymentcard.model.TerminalStatus.Detail.*


@RestrictTo(RestrictTo.Scope.LIBRARY)
interface EdfaContactlessEvents {
    fun onNfcStatus(status: NfcStatus)
    fun onCardTap()
    fun waitingForCardToTap()
    fun onCardConnected(paymentCard: PaymentCard)
    fun supportedSchemeNotFound()
    fun onCardSchemeSelected(paymentCard: PaymentCard)
    fun onCardProcessComplete(paymentCard: PaymentCard)
    fun onTerminalStatus(status: TerminalStatus)
    fun onError(throwable: EdfaException)

    fun ready(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(READY.update(detail, data))
    }

    fun waitingForCard(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(WAITING_FOR_CARD.update(detail, data))
        waitingForCardToTap()
    }

    fun cardTap(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(CARD_TAPPED.update(detail, data))
        onCardTap()
    }

    fun cardConnecting(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(CARD_CONNECTING.update(detail, data))
    }

    fun cardConnectSuccess(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(CARD_CONNECT_SUCCESS.update(detail, data))
    }

    fun cardConnectFail(detail:Detail? = null, data:Throwable){
        onTerminalStatus(CARD_CONNECT_FAILURE.update(detail, data))
        EdfaContactless.getInstance().reset(1000)
        when ((data is EdfaException)) {
            true -> onError(data)
            false -> onError(EdfaException.with(data))
        }
    }

    fun cardSchemesFetching(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(CARD_SCHEMES_FETCHING.update(detail, data))
    }

    fun cardSchemesFound(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(CARD_SCHEMES_FOUND.update(detail, data))
    }

    fun cardSchemesSorted(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(CARD_SCHEMES_SORTED.update(detail, data))
    }

    fun cardSchemesFiltered(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(CARD_SCHEMES_FILTERED.update(detail, data))
    }

    fun cardSchemeSelected(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(CARD_SCHEME_SELECTED.update(detail, data))
    }

    fun transactionProcessing(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(TRANSACTION_PROCESSING.update(detail, data))
    }

    fun transactionStarted(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(TRANSACTION_STARTED.update(detail, data))
    }

    fun transactionFail(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(TRANSACTION_FAILED.update(detail, data))
        EdfaContactless.getInstance().reset(1000)
    }

    fun transactionSuccess(detail:Detail? = null, data:Any? = null){
        onTerminalStatus(TRANSACTION_SUCCESS.update(detail, data))
        EdfaContactless.getInstance().close()
    }


}