package com.lenakurasheva.jpgtopngconverter.ui.activity

import android.app.AlertDialog
import android.content.ContextWrapper
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.lenakurasheva.jpgtopngconverter.R
import com.lenakurasheva.jpgtopngconverter.mvp.model.IDataConverter
import com.lenakurasheva.jpgtopngconverter.mvp.model.IDataProvider
import com.lenakurasheva.jpgtopngconverter.mvp.model.Repository
import com.lenakurasheva.jpgtopngconverter.mvp.presenter.MainPresenter
import com.lenakurasheva.jpgtopngconverter.mvp.view.MainView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import java.io.ByteArrayOutputStream


class MainActivity : MvpAppCompatActivity(), MainView, IDataProvider, IDataConverter {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openImgBtn.setOnClickListener { presenter.openImgBtnClicked() }
        convertBtn.setOnClickListener { presenter.convertBtnClicked() }
        saveBtn.setOnClickListener { presenter.saveBtnClicked() }
    }

    val presenter by moxyPresenter{
        MainPresenter(Repository(this, this), AndroidSchedulers.mainThread())
    }

    var imageBitmap: Bitmap? = null
    var alertDialog: AlertDialog? = null

    override fun showConvertStatus(status: String?) {
        alertDialog?.setMessage(status)
    }

    override fun showSavingStatus(status: String) {
        Toast.makeText(this, status, Toast.LENGTH_SHORT ).show()
    }

    override fun enableBtnOk(enable: Boolean){ alertDialog?.getButton(BUTTON_POSITIVE)?.isEnabled = enable }
    override fun disableBtnOk(disable: Boolean){ alertDialog?.getButton(BUTTON_POSITIVE)?.isEnabled = disable }
    override fun enableSaveBtn(enable: Boolean) { saveBtn.isEnabled = enable }
    override fun disableSaveBtn(disable: Boolean) { saveBtn.isEnabled = disable }
    override fun enableConvertBtn(enable: Boolean) { convertBtn.isEnabled = enable }
    override fun disableConvertBtn(disable: Boolean) { convertBtn.isEnabled = disable }
    override fun dismissAlert() { alertDialog?.dismiss() }

    override fun getJpgImage() {
        val intent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "open file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == -1) {
            presenter.showImage(data?.data.toString())
        }
    }

    override fun showImage() {
        imageBitmap?.let { imageView.setImageBitmap(it) }
    }

    override fun convertStringToBitmap(imageAddress: String?) {
        val uriImg = Uri.parse(imageAddress)
        uriImg?.let {
            val source: ImageDecoder.Source =
                ImageDecoder.createSource(this.contentResolver, it)
            imageBitmap = ImageDecoder.decodeBitmap(source)
        }
    }

    override fun convertByteArrayToBitmap(byteArr: ByteArray) {
            imageBitmap = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.size)
    }

    override fun showAlert() {
        alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.alert_convertation))
            .setMessage("")
            .setPositiveButton(getString(R.string.ok)) { dialog, which ->
                presenter.closeDialog()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, which ->
                presenter.cancelConvertation()
            }
            .setCancelable(false)
            .show()
    }

    override fun convertJpgToPng(): ByteArray {
        val out = ByteArrayOutputStream()
        try {
                imageBitmap?.compress(Bitmap.CompressFormat.PNG, 100, out) //100-best quality
                out.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        return out.toByteArray()

    }

    override fun saveImage(byteArray: ByteArray) {
        val wrapper = ContextWrapper(applicationContext)
        val file = wrapper.getDir("PNG", MODE_PRIVATE)
        presenter.saveImage(byteArray, file)
    }
}

