package com.lenakurasheva.jpgtopngconverter.mvp.presenter

import com.lenakurasheva.jpgtopngconverter.mvp.model.Repository
import com.lenakurasheva.jpgtopngconverter.mvp.view.MainView
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import moxy.MvpPresenter
import java.io.File
import java.io.FileOutputStream
import java.util.*


class MainPresenter(val repository: Repository, val uiScheduler: Scheduler): MvpPresenter<MainView>() {

    lateinit var imageAdress: String
    lateinit var byteArrayPngImage: ByteArray
    var disposables = CompositeDisposable()
    var convertDisposable: Disposable? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    fun openImgBtnClicked(){
        repository.getJpgImage()
        viewState.disableConvertBtn(false)
        viewState.disableSaveBtn(false)
    }

    fun showImage(data: String?){
        data?.let {
            imageAdress = it
            viewState.convertStringToBitmap(imageAdress)
            viewState.showImage()
            viewState.enableConvertBtn(true)
        }
    }

    fun convertBtnClicked(){
        viewState.showAlert()
        viewState.showConvertStatus("In Progress...")
        viewState.disableBtnOk(false)

        convertDisposable = repository.convertJpgToPng()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        byteArrayPngImage = it
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
            repository.saveImage(byteArrayPngImage)
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

    fun saveImage(byteArray: ByteArray, file: File) {
        val savingFile = File(file, "${UUID.randomUUID()}.png")
        val fos = FileOutputStream(savingFile)
            fos.write(byteArray)
            fos.flush()
            fos.close()
            println("PATH: " + savingFile.absolutePath)
    }

    override fun onDestroy() {
        super.onDestroy()
        disposables.dispose()
    }
}