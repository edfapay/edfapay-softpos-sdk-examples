package com.edfapay.paymentcard.model

import com.edfapay.paymentcard.R
import com.edfapay.paymentcard.model.TerminalStatus.Detail.*
enum class TerminalStatus(detail: Detail, data:Any?){
    ERROR(D_ERROR,null),
    READY(D_READY,null),

    WAITING_FOR_CARD(D_WAITING_FOR_CARD,null),
    CARD_TAPPED(D_CARD_TAPPED,null),

    CARD_CONNECTING(D_CARD_TAPPED,null),
    CARD_CONNECT_SUCCESS(D_CARD_TAPPED,null),
    CARD_CONNECT_FAILURE(D_CARD_TAPPED,null),

    CARD_SCHEMES_FETCHING(D_CARD_SCHEMES_FOUND,null),
    CARD_SCHEMES_FOUND(D_CARD_SCHEMES_FOUND,null),
    CARD_SCHEMES_SORTED(D_CARD_SCHEMES_SORTED,null),
    CARD_SCHEMES_FILTERED(D_CARD_SCHEMES_FILTERED,null),
    CARD_SCHEME_SELECTED(D_CARD_SCHEME_SELECTED,null),

    TRANSACTION_STARTED(D_TRANSACTION_STARTED,null),
    TRANSACTION_PROCESSING(D_TRANSACTION_PROCESSING,null),
    TRANSACTION_SUCCESS(D_TRANSACTION_SUCCESS,null),
    TRANSACTION_FAILED(D_TRANSACTION_FAILED,null),
    ;

    fun update(detail: Detail?, data:Any?)  = with(this) {
        this
    }

    // TODO: Update message string res (R.string.*)
    enum class Detail(code:Int, message:Int){
        D_ERROR(-1, R.string.stww),
        D_READY(1, R.string.stww),

        D_WAITING_FOR_CARD(2, R.string.stww),
        D_CARD_TAPPED(3, R.string.stww),
        D_CARD_SCHEMES_FOUND(4, R.string.stww),
        D_CARD_SCHEMES_FILTERED(5, R.string.stww),
        D_CARD_SCHEMES_SORTED(6, R.string.stww),
        D_CARD_SCHEME_SELECTED(7, R.string.stww),

        D_TRANSACTION_STARTED(8, R.string.stww),
        D_TRANSACTION_PROCESSING(9, R.string.stww),
        D_TRANSACTION_SUCCESS(10, R.string.stww),
        D_TRANSACTION_FAILED(11, R.string.stww),
        D_TRANSACTION_FINISHED(12, R.string.stww);
    }
}