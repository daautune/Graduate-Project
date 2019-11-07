package com.dtu.capstone2.ereading.ui.utils

import android.app.Dialog
import android.content.DialogInterface
import android.support.v4.app.FragmentManager
import android.widget.TextView
import com.dtu.capstone2.ereading.R

class ErrorDialog : BaseDialog() {
    private var isShowing = false

    internal var titleDialog = ""

    override fun setContentDialog(dialog: Dialog) {
        dialog.setContentView(R.layout.dialog_error)
    }

    override fun initListeners(dialog: Dialog) {
        val tvOk = dialog.findViewById<TextView>(R.id.tvErrorDialogOk)
        val tvTitle = dialog.findViewById<TextView>(R.id.tvErrorTitle)
        tvOk?.setOnClickListener {
            this.dismiss()
        }
        tvTitle.text = titleDialog
    }

    override fun show(manager: FragmentManager?, tag: String?) {
        if (!isShowing) {
            isShowing = true
            super.show(manager, tag)
        }
    }

    override fun dismiss() {
        if (isShowing) {
            isShowing = false
            super.dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        RxBusTransport.publish(Transport(TypeTransportBus.CALL_BACK_DIALOG_ERROR_MESSAGE, activity?.javaClass?.simpleName
                ?: ""))
    }
}
