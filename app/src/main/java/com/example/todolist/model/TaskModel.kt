package com.example.todolist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Parcelize
data class TaskModel(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val dateInMillis: Long = System.currentTimeMillis(),
    val iconUrl: String? = ""
) : Parcelable {
    val date: String get() = DateFormat.getDateTimeInstance().format(dateInMillis)
}
