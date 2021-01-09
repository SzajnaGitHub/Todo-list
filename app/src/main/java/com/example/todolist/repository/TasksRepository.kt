package com.example.todolist.repository

import com.example.todolist.db.room.converter.toEntity
import com.example.todolist.db.firestore.FirestoreTaskDatabase
import com.example.todolist.db.room.dao.TaskDao
import com.example.todolist.model.TaskModel
import io.reactivex.Completable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksRepository @Inject constructor(
    private val taskDao: TaskDao,
    private val firestore: FirestoreTaskDatabase
) {

    fun insertTask(task: TaskModel): Completable = taskDao.insert(task.toEntity())

    fun insertTask2(task: TaskModel) = taskDao.insert(task.toEntity())
        .concatWith(firestore.insertTask(task))

    fun deleteTask(task: TaskModel): Completable = taskDao.delete(task.toEntity())

    fun updateTask(task: TaskModel): Completable = taskDao.updateTask(task.toEntity())

    fun getAllTasks() = taskDao.getAllTasks()

}
