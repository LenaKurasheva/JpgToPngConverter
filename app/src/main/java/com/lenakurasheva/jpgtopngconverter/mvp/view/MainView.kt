package com.lenakurasheva.jpgtopngconverter.mvp.view

import android.os.Parcelable
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

@AddToEndSingle
interface MainView: MvpView{

    fun showConvertStatus(status: String?)
    @Skip
    fun showSavingStatus(status: String)

    fun enableSaveBtn(enable: Boolean)
    fun disableSaveBtn(disable: Boolean)
    fun enableBtnOk(enable: Boolean)
    fun disableBtnOk(disable: Boolean)

    fun showImage()
    fun enableConvertBtn(enable: Boolean)
    fun disableConvertBtn(disable: Boolean)

    fun showAlert()
    fun dismissAlert()

    fun convertStringToBitmap(imageAddress: String?)
    fun convertByteArrayToBitmap(byteArr: ByteArray)
}
