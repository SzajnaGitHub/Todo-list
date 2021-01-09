package com.example.todolist.db.room.converter

import com.example.todolist.db.room.entities.TaskEntity
import com.example.todolist.model.TaskModel

fun TaskModel.toEntity() = TaskEntity(
    id = id,
    title = title,
    description = description,
    dateInMillis = dateInMillis,
    iconUrl = iconUrl
)

fun TaskEntity.toModel() = TaskModel(
    id = id,
    title = title,
    description = description,
    dateInMillis = dateInMillis,
    iconUrl = iconUrl
)
