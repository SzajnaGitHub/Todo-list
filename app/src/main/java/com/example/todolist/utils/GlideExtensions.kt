package com.example.todolist.utils

import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.todolist.R

fun ImageView.loadImage(url: String?) {
    if (!url.isNullOrEmpty()) {
        Glide.with(context)
            .load(url)
            .error(R.drawable.ic_error_image_24)
            .placeholder(CircularProgressDrawable(context).apply {
                strokeWidth = 5f
                centerRadius = 60f
                start()
            })
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    } else {
        Glide.with(context)
            .load(R.drawable.ic_todo_24)
            .into(this)
    }
}
