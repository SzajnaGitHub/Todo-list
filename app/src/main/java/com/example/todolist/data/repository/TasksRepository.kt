package com.example.todolist.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.rxjava2.observable
import com.example.todolist.data.paginssource.TaskPagingSource
import com.example.todolist.db.firestore.FirestoreTaskDatabase
import com.example.todolist.model.TaskModel
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TasksRepository @Inject constructor(
    private val firestore: FirestoreTaskDatabase,
) {
    fun updateTask(task: TaskModel): Completable = firestore.updateTask(task)

    fun insertTask(task: TaskModel): Completable = firestore.insertTask(task)

    fun deleteTask(task: TaskModel): Completable = firestore.deleteTask(task)

    fun observeTasksChanged(): Observable<Unit> = firestore.observeUserInput()

    fun getTasksFromPagingSource() = Pager(
        config = PagingConfig(pageSize = PAGE_SIZE),
        pagingSourceFactory = { TaskPagingSource(firestore) }
    ).observable

    companion object {
        const val PAGE_SIZE = 30
    }
}
