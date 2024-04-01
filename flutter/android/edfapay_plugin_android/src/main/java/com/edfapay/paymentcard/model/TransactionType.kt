package com.edfapay.paymentcard.model

enum class TransactionType(val value: Int) : java.io.Serializable{
    PURCHASE(0x00),

    /**
     * '09' for a purchase with cashback
     */
    CASHBACK(0x09),

    /**
     * '20' for a refund transaction
     */
    REFUND(0x20);
}