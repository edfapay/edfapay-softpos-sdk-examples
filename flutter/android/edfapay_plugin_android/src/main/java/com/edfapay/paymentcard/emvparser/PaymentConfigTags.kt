package com.edfapay.paymentcard.emvparser

import com.mastercard.terminalsdk.utility.ByteUtility

internal enum class PaymentConfigTags(val hex: String) {

    // inputs tag
    ACCOUNT_TYPE("5F57"),
    AMOUNT_AUTHORIZED_NUMERIC("9F02"),
    AMOUNT_AUTHORIZED_BINARY("81"),
    AMOUNT_OTHER_NUMERIC("9F03"),
    AMOUNT_OTHER_BINARY("9F04"),
    CURRENCY("5F2A"),
    TRANSACTION_TYPE("9C"),
    TRANSACTION_DATE("9A"),
    UNPREDICTABLE_NUMBER("9f37"),
    TERMINAL_FLOOR_LIMIT("9F1B"),
    TERMINAL_COUNTRY_CODE("9F1A"),
    MERCHANT_CATEGORY_CODE("9F15"),
    COUNTRY_CODE("9F1A"),
    TERMINAL_TYPE("9F35"),
    POS_ENTRY_MODE("9F39"),
    CVM_LIMIT("DF01"),
    CVM_STATUS("DF03"),
    MERCHANT_NAME_AND_LOCATION("9F4E"),
    AID("4F"),
    ICC("80A8"),
    TRACK_2_DATA("57"),
    TRACK_2_DATA_ALTERNATIVE("9F6B"),
    CARD_SEQUENCE_NUMBER("5F34"),
    TAG_UPDATE_DATA("FF8111"),
    FCI_DISC_DATA("BF0C"),
    CRYPTOGRAM("9F26"),
    CRYPTOGRAM_DATA("9F27"),
    TVR("95"),
    INTERFACE_DEVICE_SERIAL_NUMBER("9F1E"),
    DEDICATED_FILE_NAME("84"),
    TRANSACTION_STATUS_INFO("9B"),
    CVM("9F34"),
    CARDHOLDER_NAME("5F20"),
    CTQ("9F6C"),
    TTQ("9F66"),
    PRIORITY("87"),
    TSC("9F41"),
    TXN_CURRENCY_EXPONENT("5F36"),
    APPLICATION_VERSION_NO("9F09"),
    TERMINAL_CAPABILITIES("9F33"),
    PUNATC_TRACK1("9F63");

    val tagAsBytes: ByteArray = ByteUtility.hexStringToByteArray(hex)
    val tagAsInt: Int = ByteUtility.byteArrayToInt(*tagAsBytes)
}