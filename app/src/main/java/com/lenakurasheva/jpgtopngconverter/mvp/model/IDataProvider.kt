package com.lenakurasheva.jpgtopngconverter.mvp.model

interface IDataProvider {
    fun getJpgImage()
    fun saveImage(byteArray: ByteArray)
}