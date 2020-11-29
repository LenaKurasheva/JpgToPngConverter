package com.lenakurasheva.jpgtopngconverter.mvp.presenter

import com.lenakurasheva.jpgtopngconverter.mvp.model.IDataConverter
import com.lenakurasheva.jpgtopngconverter.mvp.model.Image
import com.lenakurasheva.jpgtopngconverter.mvp.view.MainView
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter


class MainPresenter(val uiScheduler: Scheduler, val dataConverter: IDataConverter): MvpPresenter<MainView>() {

    var disposables = CompositeDisposable()
    var convertDisposable: Disposable? = null

    var imageJpg: Image? = null
    var imagePng: Image? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun openImgBtnClicked(){
        viewState.getImage()
        viewState.disableConvertBtn(false)
        viewState.disableSaveBtn(false)
    }

    fun showImage(image: Image){
        imageJpg = image
        viewState.showImage(image)
        viewState.enableConvertBtn(true)
    }

    fun convertBtnClicked(){
        viewState.showAlert()
        viewState.showConvertStatus("In Progress...")
        viewState.disableBtnOk(false)

        convertDisposable = dataConverter.convert(imageJpg)
                .observeOn(uiScheduler)
                .subscribe(
                    {   imagePng = Image(it)
                        viewState.showConvertStatus("Success")
                        viewState.enableBtnOk(true)
                        viewState.enableSaveBtn(true)
                    },
                    {
                        viewState.showConvertStatus(it.message)
                        viewState.enableBtnOk(true)
                    })
        disposables.add(convertDisposable)
    }

    fun closeDialog() = viewState.dismissAlert()

    fun cancelConvertation(){
        convertDisposable?.dispose()
        closeDialog()
    }

    fun saveBtnClicked() {
        disposables.add(
            dataConverter.saveImage(imagePng)
            .observeOn(uiScheduler)
            .subscribe(
                {
                    viewState.showSavingStatus("File saved")
                },
                {
                    viewState.showSavingStatus(it.message.toString())
                }
            )
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}