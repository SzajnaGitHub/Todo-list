package com.example.todolist.db.room.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.example.todolist.db.room.entities.TaskEntity
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: TaskEntity): Completable

    @Delete
    fun delete(entity: TaskEntity): Completable

    @Update
    fun updateTask(entity: TaskEntity): Completable

    @Query("SELECT * FROM task_table ORDER BY dateInMillis DESC")
    fun getAllTasks(): Observable<List<TaskEntity>>

    @Query("SELECT * FROM task_table ORDER BY dateInMillis DESC")
    fun getTasks(): PagingSource<Int, List<TaskEntity>>
}
