package com.example.todolist.db.room.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolist.db.room.dao.TaskDao
import com.example.todolist.db.room.entities.TaskEntity

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {

    abstract fun getTaskDao(): TaskDao

    companion object {
        const val TASKS_DATABASE_NAME = "tasks_db.db"
    }
}
