package com.dtu.capstone2.ereading.ui.utils

import android.app.Dialog
import android.support.v4.app.FragmentManager
import android.widget.TextView
import com.dtu.capstone2.ereading.R
import kotlinx.android.synthetic.main.dialog_error_detail.*

class ApiErrorDialog : BaseDialog() {
    private var isShowing = false

    override fun setContentDialog(dialog: Dialog) {
        dialog.setContentView(R.layout.dialog_error_detail)
    }

    override fun initListeners(dialog: Dialog) {
        val tvOk = dialog.findViewById<TextView>(R.id.tvApiErrorDialogOk)
        tvOk?.setOnClickListener {
            this.dismiss()
        }
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

    internal fun setContentError(title: String) {
        tvApiErrorDialogTitle?.text = title
    }
}
