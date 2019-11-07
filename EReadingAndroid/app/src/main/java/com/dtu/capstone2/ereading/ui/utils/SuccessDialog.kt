package com.dtu.capstone2.ereading.ui.utils

import android.app.Dialog
import android.support.v4.app.FragmentManager
import com.dtu.capstone2.ereading.R

class SuccessDialog : BaseDialog() {
    private var isShowing = false

    override fun setContentDialog(dialog: Dialog) {
        dialog.setContentView(R.layout.dialog_success)
    }

    override fun initListeners(dialog: Dialog) {}

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
}
