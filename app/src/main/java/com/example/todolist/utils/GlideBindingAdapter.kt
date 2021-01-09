package com.example.todolist.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter

object GlideBindingAdapter {

    @JvmStatic
    @BindingAdapter("imgUrl")
    fun bindUrl(view: ImageView, imgUrl: String? = null) {
        if (imgUrl == null) {
            return
        }
        view.loadImage(imgUrl)
    }
}
