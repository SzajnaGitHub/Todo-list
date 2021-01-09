package com.example.todolist.utils.input

import android.content.Context
import com.example.todolist.R

object InputTypeResolver {

    fun resolve(context: Context, inputType: InputType): String? {
        val messageId = when (inputType) {
            InputType.BLANK_OR_EMPTY -> R.string.message_error_empty_input
            InputType.VALID -> null
        }
        return messageId?.let { context.getString(messageId) }
    }
}
