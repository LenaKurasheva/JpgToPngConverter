package com.lenakurasheva.jpgtopngconverter.mvp.view

import com.lenakurasheva.jpgtopngconverter.mvp.model.Image
import moxy.MvpView
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.Skip

@AddToEndSingle
interface MainView: MvpView{
    @Skip
    fun getImage()

    fun showConvertStatus(status: String?)
    @Skip
    fun showSavingStatus(status: String)

    fun enableSaveBtn(enable: Boolean)
    fun disableSaveBtn(disable: Boolean)
    fun enableBtnOk(enable: Boolean)
    fun disableBtnOk(disable: Boolean)

    fun showImage(image: Image)
    fun enableConvertBtn(enable: Boolean)
    fun disableConvertBtn(disable: Boolean)

    fun showAlert()
    fun dismissAlert()
}
