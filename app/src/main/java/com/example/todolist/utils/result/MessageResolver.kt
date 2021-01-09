package com.example.todolist.utils.result

import android.content.Context
import com.example.todolist.R

object MessageResolver {

    fun resolveSuccessMessage(context: Context, messageId: SuccessMessageId): String? {
        val id = when (messageId) {
            SuccessMessageId.NONE -> null
            SuccessMessageId.MESSAGE_TASK_DELETED -> R.string.task_delete_text
            SuccessMessageId.MESSAGE_TASK_ADDED -> R.string.task_add_text
            SuccessMessageId.MESSAGE_TASK_UPDATED -> R.string.task_update_text
        }
        return id?.let { context.getString(it) }
    }

    fun resolveErrorMessage(context: Context, messageId: ErrorMessageId): String? {
        val id = when (messageId) {
            ErrorMessageId.NONE -> null
            ErrorMessageId.MESSAGE_TASK_DELETE_FAILED -> R.string.task_delete_fail_text
            ErrorMessageId.MESSAGE_TASK_ADD_FAILED -> R.string.task_add_fail_text
            ErrorMessageId.MESSAGE_TASK_UPDATE_FAILED -> R.string.task_update_fail_text
        }
        return id?.let { context.getString(it) }
    }
}
