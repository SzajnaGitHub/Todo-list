package com.example.todolist.utils

import android.animation.ValueAnimator
import android.view.View
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

fun View.bounce(max: Float = 1.2f) {
    ValueAnimator.ofFloat(1.0f, 0.95f, max).apply {
        duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        repeatCount = 1
        repeatMode = ValueAnimator.REVERSE
        interpolator = FastOutSlowInInterpolator()
        addUpdateListener {
            (it.animatedValue as? Float)?.let { value ->
                scaleX = value
                scaleY = value
                invalidate()
            }
        }
    }.start()
}
