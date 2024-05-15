package com.rexrama.aficionado.utils

import android.app.AlertDialog
import android.content.Context

class DialogUtils(private val context: Context) {
    fun showResultDialog(title: String, message: String?, positiveButtonAction: () -> Unit) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Proceed") { _, _ ->
                positiveButtonAction.invoke()
            }
            setCancelable(false)
            create()
            show()
        }
    }

    fun showErrorDialog(title: String, message: String?) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton("Retry") { _, _ ->
                // Retry action if needed
            }
            setCancelable(false)
            create()
            show()
        }
    }
}