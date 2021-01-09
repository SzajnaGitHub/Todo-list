package com.example.todolist.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar


fun Fragment.showSafeSnackbar(view: View, message: String?, duration: Int = Snackbar.LENGTH_SHORT) {
    if (message == null) return
    context?.let {
        Snackbar.make(view, message, duration).show()
    }
}
