package com.dtu.capstone2.ereading.ui.utils

import android.content.Context
import android.support.v4.app.Fragment
import com.dtu.capstone2.ereading.R
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment() {
    private var nameActivity = "null"
    protected val managerSubscribe: CompositeDisposable = CompositeDisposable()

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        nameActivity = activity?.javaClass?.simpleName ?: "null"
    }

    override fun onPause() {
        super.onPause()
        managerSubscribe.clear()
    }

    protected fun showLoadingDialog() {
        RxBusTransport.publish(Transport(TypeTransportBus.DIALOG_LOADING, nameActivity))
    }

    protected fun dismissLoadingDialog() {
        RxBusTransport.publish(Transport(TypeTransportBus.DISMISS_DIALOG_LOADING, nameActivity))
    }

    protected fun showApiErrorDialog() {
        RxBusTransport.publish(Transport(TypeTransportBus.DIALOG_API_ERROR, nameActivity))
    }

    protected fun showSuccessDialog(senderName: String, isDelayDismissLoading: Boolean) {
        RxBusTransport.publish(Transport(TypeTransportBus.DIALOG_SUCCESS, senderName, isDelayDismissLoading))
    }

    protected fun showMessageErrorDialog(message: String) {
        RxBusTransport.publish(Transport(TypeTransportBus.DIALOG_ERROR_MESSAGE, message = message))
    }

    protected fun showToastRequirementLogin(message: String = "") {
        RxBusTransport.publish(Transport(TypeTransportBus.TOAST_REQUIREMENT_LOGIN, message = message))
    }

    protected fun replaceFragment(container: Int, fragment: Fragment, addBackStack: Boolean = true, enableAnimation: Boolean = true) {
        activity?.supportFragmentManager?.beginTransaction()?.apply {
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
        activity?.supportFragmentManager?.beginTransaction()?.apply {
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

    protected fun onBackPressed() {
        activity?.onBackPressed()
    }
}
