package com.example.todolist.utils

import com.example.todolist.utils.result.ErrorMessageId
import com.example.todolist.utils.result.SuccessMessageId

sealed class Resource<out T> {
    data class Failure(val exception: Throwable, val errorMessageId: ErrorMessageId = ErrorMessageId.NONE) : Resource<Nothing>()
    data class Success<T>(val data: T, val successMessageId: SuccessMessageId = SuccessMessageId.NONE) : Resource<T>()
    object Loading : Resource<Nothing>()
}
