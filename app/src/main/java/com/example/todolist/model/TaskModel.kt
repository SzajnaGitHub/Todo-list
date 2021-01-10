package com.example.todolist.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class TaskModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val dateInMillis: Long = System.currentTimeMillis(),
    val iconUrl: String? = null
) : Parcelable
