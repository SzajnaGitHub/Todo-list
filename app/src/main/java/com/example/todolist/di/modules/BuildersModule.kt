package com.example.todolist.di.modules

import com.example.todolist.ui.MainActivity
import com.example.todolist.ui.TaskDetailsFragment
import com.example.todolist.ui.TaskListFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BuildersModule {

    @ContributesAndroidInjector
    internal abstract fun provideMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun provideListFragment(): TaskListFragment

    @ContributesAndroidInjector
    internal abstract fun provideDetailsFragment(): TaskDetailsFragment

}
