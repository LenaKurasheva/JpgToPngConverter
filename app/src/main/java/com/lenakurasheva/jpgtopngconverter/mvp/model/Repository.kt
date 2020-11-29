package com.lenakurasheva.jpgtopngconverter.mvp.model

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.Exception
import java.lang.RuntimeException

class Repository(val dataProvider: IDataProvider, val dataConverter: IDataConverter)  {
    fun getJpgImage() = dataProvider.getJpgImage()

    fun convertJpgToPng() =  Single.create<ByteArray> { emitter ->
        val result = dataConverter.convertJpgToPng()
        if (result != null && result is ByteArray) {
            emitter.onSuccess(result)
        } else {
            emitter.onError(RuntimeException("Fail"))
        }
    }.subscribeOn(Schedulers.computation())

    fun saveImage(byteArr: ByteArray) = Completable.create { emitter ->
        try {
            dataProvider.saveImage(byteArr)
            emitter.onComplete()
        } catch (e: Throwable){
            emitter.onError(Exception("Saving error"))
        }
    }.subscribeOn(Schedulers.io())

}