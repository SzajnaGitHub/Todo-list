package com.example.todolist.db.room.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "task_table")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val dateInMillis: Long = System.currentTimeMillis(),
    val iconUrl: String? = null
)
