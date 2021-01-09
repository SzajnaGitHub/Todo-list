package com.example.todolist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.db.room.converter.toModel
import com.example.todolist.model.TaskModel
import com.example.todolist.repository.TasksRepository
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

    private val _tasks = MutableLiveData<Event<Resource<List<TaskModel>>>>()
    val tasks: LiveData<Event<Resource<List<TaskModel>>>> get() = _tasks

    private val _taskDeleted = MutableLiveData<Event<Resource<Unit>>>()
    val taskDeleted: MutableLiveData<Event<Resource<Unit>>> get() = _taskDeleted

    private fun getAllTasks() {
        tasksRepository.getAllTasks()
            .map { list -> list.map { entity -> entity.toModel() } }
            .ioCall()
            .asLiveData(_tasks)
            .let(disposables::add)
    }

    fun deleteTask(taskModel: TaskModel) {
        tasksRepository.deleteTask(taskModel)
            .ioCall()
            .asLiveData(_taskDeleted, Result(SuccessMessageId.MESSAGE_TASK_DELETED, ErrorMessageId.MESSAGE_TASK_DELETE_FAILED))
            .let(disposables::add)
    }

    private fun insertTask() {
        //firestore.insertTask(TaskModel(Random.nextInt(10,30), System.currentTimeMillis().toString(),"adsfasdf","yes","UEREL"))
        // firestore.deleteTask()
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    init {
        getAllTasks()
        insertTask()
    }
}
