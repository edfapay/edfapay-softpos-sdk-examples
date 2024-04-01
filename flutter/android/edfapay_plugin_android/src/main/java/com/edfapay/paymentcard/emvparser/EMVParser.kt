package com.edfapay.paymentcard.emvparser

import io.github.binaryfoo.DecodedData
import io.github.binaryfoo.RootDecoder

/**
 * Class to perform EMV decoding and interpretation
 */
// https://cert.api2.heartlandportico.com/Gateway/PorticoDevGuide/build/PorticoDeveloperGuide/EMV%20Request%20Tags.html
class EMVParser {

    /**
     * Init root decoder
     */
    private val _decoder = RootDecoder()

    /**
     * Parses and decodes a BER TLV data string used for EMV transactions
     *
     * @param String emv
     * @return ArrayList<DecodedData>
     */
    fun decodeTLV(emv: String):ArrayList<Tag> {
        return parseDecodedData(_decoder.decode(emv,"EMV","constructed"))
    }

    /**
     * Convert the DecodeData Collection object to a parcelable ArrayList
     *
     * @param List<DecodedData>
     * @return ArrayList<Tag>
     */
    private fun parseDecodedData(tagList: List<DecodedData>): ArrayList<Tag> {

        val parcelable: ArrayList<Tag> = ArrayList()
        var tag: Tag?
        for (i in 0..tagList.count() - 1) {
            tag = Tag(tagList.get(i).rawData,tagList.get(i).fullDecodedData)
            parcelable.add(tag)
        }
        return parcelable;
    }

}