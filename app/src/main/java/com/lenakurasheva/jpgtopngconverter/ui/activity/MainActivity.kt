package com.lenakurasheva.jpgtopngconverter.ui.activity

import android.app.AlertDialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.lenakurasheva.jpgtopngconverter.R
import com.lenakurasheva.jpgtopngconverter.mvp.model.Image
import com.lenakurasheva.jpgtopngconverter.mvp.presenter.MainPresenter
import com.lenakurasheva.jpgtopngconverter.mvp.view.MainView
import com.lenakurasheva.jpgtopngconverter.ui.AndroidConverter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter


class MainActivity : MvpAppCompatActivity(), MainView {

    var androidConverter = AndroidConverter(this)
    val presenter by moxyPresenter{
        MainPresenter(AndroidSchedulers.mainThread(), androidConverter)
    }
    var alertDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openImgBtn.setOnClickListener { presenter.openImgBtnClicked() }
        convertBtn.setOnClickListener { presenter.convertBtnClicked() }
        saveBtn.setOnClickListener { presenter.saveBtnClicked() }
    }

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

    override fun getImage() {
        val intent = Intent()
            .setType("image/jpeg")
            .setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(intent, "open file"), 111)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 111 && resultCode == -1) {
            val imageBitmap = androidConverter.convertUriToBitmap(data?.data, this)
            val imageByteArr = androidConverter.convertBitmapToByteArray(imageBitmap)
            presenter.showImage(Image(imageByteArr))
        }
    }

    override fun showImage(image: Image) {
        val imageBitmap = androidConverter.convertByteArrayToBitmap(image.byteArray)
        imageView.setImageBitmap(imageBitmap)
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

}



