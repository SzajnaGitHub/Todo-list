package com.example.todolist.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.todolist.di.viewmodels.DaggerViewModelFactory
import com.example.todolist.di.viewmodels.ViewModelKey
import com.example.todolist.ui.viewmodel.TaskDetailsViewModel
import com.example.todolist.ui.viewmodel.TaskListViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    internal abstract fun bindViewModelFactory(factoryDagger: DaggerViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(TaskListViewModel::class)
    internal abstract fun bindTaskListViewModel(viewModel: TaskListViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TaskDetailsViewModel::class)
    internal abstract fun bindTaskDetailsViewModel(viewModel: TaskDetailsViewModel): ViewModel

}
