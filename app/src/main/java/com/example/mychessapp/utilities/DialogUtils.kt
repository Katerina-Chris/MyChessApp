package com.example.mychessapp.utilities

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import com.example.mychessapp.databinding.ViewLoaderBinding

class DialogUtils {

    private var dialog: AlertDialog? = null

    // Displays loading dialog
    fun showLoading(context: Context) {
        val builder = AlertDialog.Builder(context)
        val loadingBinding = ViewLoaderBinding.inflate(LayoutInflater.from(context))
        builder.setView(loadingBinding.root)
        dialog = builder.create()
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        dialog?.show()
    }

    // Hides ths loading dialog if it's showing
    fun hideLoading() {
        dialog?.dismiss()
    }
}