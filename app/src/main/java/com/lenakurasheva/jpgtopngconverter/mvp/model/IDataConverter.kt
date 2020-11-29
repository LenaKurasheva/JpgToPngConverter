package com.lenakurasheva.jpgtopngconverter.mvp.model


interface IDataConverter {
    fun convertJpgToPng(): ByteArray
//    fun convertStringToBitmap(imgAddress: String?)
}