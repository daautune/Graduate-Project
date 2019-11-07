package com.dtu.capstone2.ereading.ui.utils

import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.dtu.capstone2.ereading.R
import io.reactivex.disposables.CompositeDisposable

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        const val TIME_DELAY_DISMISS_DIALOG_SUCCESS = 1300L
    }

    private lateinit var loadingDialog: LoadingDialog
    private lateinit var apiErrorDialog: ApiErrorDialog
    private lateinit var successDialog: SuccessDialog
    private lateinit var errorDialog: ErrorDialog
    private val managerSubscribe: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loadingDialog = LoadingDialog()
        apiErrorDialog = ApiErrorDialog()
        successDialog = SuccessDialog()
        errorDialog = ErrorDialog()

    }

    override fun onResume() {
        super.onResume()
        initListener()
    }

    override fun onPause() {
        super.onPause()
        managerSubscribe.clear()
    }

    private fun initListener() {
        managerSubscribe.add(RxBusTransport.listen().observeOnUiThread().subscribe({
            when (it.typeTransport) {
                TypeTransportBus.DIALOG_LOADING -> {
                    loadingDialog.show(supportFragmentManager, TypeTransportBus.DIALOG_LOADING.typeValue)
                }
                TypeTransportBus.DIALOG_API_ERROR -> {
                    loadingDialog.dismiss()
                    apiErrorDialog.show(supportFragmentManager, TypeTransportBus.DIALOG_API_ERROR.typeValue)
                }
                TypeTransportBus.DIALOG_SUCCESS -> {
                    loadingDialog.dismiss()
                    successDialog.show(supportFragmentManager, TypeTransportBus.DIALOG_SUCCESS.typeValue)
                    (it.message as? Boolean)?.let { isDelay ->
                        if (isDelay) {
                            Handler().postDelayed({
                                successDialog.dismiss()
                            }, TIME_DELAY_DISMISS_DIALOG_SUCCESS)
                        }
                    }
                }
                TypeTransportBus.DISMISS_DIALOG_LOADING -> {
                    loadingDialog.dismiss()
                }
                TypeTransportBus.DIALOG_ERROR_MESSAGE -> {
                    loadingDialog.dismiss()
                    errorDialog.titleDialog = it.message.toString()
                    errorDialog.show(supportFragmentManager, TypeTransportBus.DIALOG_API_ERROR.typeValue)
                }
                TypeTransportBus.TOAST_WITH_MESSAGE_SELECT_WORD -> {
                    Toast.makeText(this, getString(R.string.translate_new_feed_toast_select_word, it.message.toString()), Toast.LENGTH_SHORT).show()
                }
                TypeTransportBus.TOAST_REQUIREMENT_LOGIN -> {
                    loadingDialog.dismiss()
                    it.message.toString().let { message ->
                        if (message.isEmpty()) {
                            Toast.makeText(this, getString(R.string.toast_requirement_login), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }, {
            Log.w("BaseActivity", ":$it")
        }))
    }

    protected fun replaceFragment(container: Int, fragment: Fragment, addBackStack: Boolean = true, enableAnimation: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            if (enableAnimation) {
                this.setCustomAnimations(R.animator.anim_slide_new_in_right, R.animator.anim_slide_old_out_left,
                        R.animator.anim_slide_new_in_left, R.animator.anim_slide_old_out_right)
            }
            if (addBackStack) {
                this.addToBackStack(null)
            }
            this.replace(container, fragment)
            this.commit()
        }
    }

    protected fun addFragment(container: Int, fragment: Fragment, addBackStack: Boolean = true, enableAnimation: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            if (enableAnimation) {
                this.setCustomAnimations(R.animator.anim_slide_new_in_right, R.animator.anim_slide_old_out_left,
                        R.animator.anim_slide_new_in_left, R.animator.anim_slide_old_out_right)
            }
            if (addBackStack) {
                this.addToBackStack(null)
            }
            this.add(container, fragment)
            this.commit()
        }
    }
}
