package com.example.todolist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.rxjava2.cachedIn
import com.example.todolist.data.repository.TasksRepository
import com.example.todolist.model.TaskModel
import com.example.todolist.utils.Event
import com.example.todolist.utils.Resource
import com.example.todolist.utils.asLiveData
import com.example.todolist.utils.ioCall
import com.example.todolist.utils.result.ErrorMessageId
import com.example.todolist.utils.result.Result
import com.example.todolist.utils.result.SuccessMessageId
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TaskListViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _tasks = MutableLiveData<Event<Resource<PagingData<TaskModel>>>>()
    val tasks: LiveData<Event<Resource<PagingData<TaskModel>>>> get() = _tasks

    private val _taskDeleted = MutableLiveData<Event<Resource<Unit>>>()
    val taskDeleted: LiveData<Event<Resource<Unit>>> get() = _taskDeleted

    private val _tasksChanged = MutableLiveData<Event<Resource<Unit>>>()
    val tasksChanged: LiveData<Event<Resource<Unit>>> get() = _tasksChanged

    private fun getTasks() {
        tasksRepository.getTasksFromPagingSource().cachedIn(viewModelScope)
            .asLiveData(_tasks)
            .let(disposables::add)
    }

    fun deleteTask(taskModel: TaskModel) {
        tasksRepository.deleteTask(taskModel)
            .ioCall()
            .asLiveData(_taskDeleted, Result(SuccessMessageId.MESSAGE_TASK_DELETED, ErrorMessageId.MESSAGE_TASK_DELETE_FAILED))
            .let(disposables::add)
    }

    private fun observeItemsChanged() {
        tasksRepository.observeTasksChanged()
            .asLiveData(_tasksChanged)
            .let(disposables::add)
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    init {
        getTasks()
        observeItemsChanged()
    }
}
