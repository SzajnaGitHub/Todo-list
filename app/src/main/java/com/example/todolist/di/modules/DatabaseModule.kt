package com.example.todolist.di.modules

import androidx.room.Room
import com.example.todolist.db.room.database.TaskDatabase
import com.example.todolist.ui.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideTaskDatabase(context: Application) = Room.databaseBuilder(context, TaskDatabase::class.java, TaskDatabase.TASKS_DATABASE_NAME)
        .fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideTaskDao(taskDatabase: TaskDatabase) = taskDatabase.taskDao()

}
