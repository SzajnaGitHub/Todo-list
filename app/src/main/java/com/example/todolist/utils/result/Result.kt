package com.example.todolist.utils.result

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Result(val successMessage: SuccessMessageId, val errorMessage: ErrorMessageId) : Parcelable
