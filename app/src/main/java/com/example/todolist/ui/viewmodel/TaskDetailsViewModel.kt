package com.example.todolist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.todolist.model.TaskModel
import com.example.todolist.repository.TasksRepository
import com.example.todolist.utils.Event
import com.example.todolist.utils.Resource
import com.example.todolist.utils.asLiveData
import com.example.todolist.utils.input.InputType
import com.example.todolist.utils.ioCall
import com.example.todolist.utils.result.ErrorMessageId
import com.example.todolist.utils.result.Result
import com.example.todolist.utils.result.SuccessMessageId
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskDetailsViewModel @Inject constructor(
    private val tasksRepository: TasksRepository
) : ViewModel() {

    private val disposables = CompositeDisposable()

    private val _titleInputValid = MutableLiveData<InputType>()
    val titleInputValid: LiveData<InputType> get() = _titleInputValid

    private val _taskAddedOrUpdated = MutableLiveData<Event<Resource<Unit>>>()
    val taskAddedOrUpdate: LiveData<Event<Resource<Unit>>> get() = _taskAddedOrUpdated

    var title: String = ""
    var description: String = ""
    var url: String = ""

    var taskModel: TaskModel? = null
        set(value) {
            field = value
            title = value?.title ?: ""
            description = value?.description ?: ""
            url = value?.iconUrl ?: ""
        }

    private fun addTask(task: TaskModel) {
        tasksRepository.insertTask(task)
            .ioCall()
            .asLiveData(_taskAddedOrUpdated, Result(successMessage = SuccessMessageId.MESSAGE_TASK_ADDED, errorMessage = ErrorMessageId.MESSAGE_TASK_ADD_FAILED))
            .let(disposables::add)
    }

    private fun updateTask(task: TaskModel) {
        tasksRepository.updateTask(task)
            .ioCall()
            .asLiveData(_taskAddedOrUpdated, Result(successMessage = SuccessMessageId.MESSAGE_TASK_UPDATED, errorMessage = ErrorMessageId.MESSAGE_TASK_UPDATE_FAILED))
            .let(disposables::add)
    }

    private fun isInputValid(input: String) = when {
        input.isBlank() || input.isEmpty() -> InputType.BLANK_OR_EMPTY
        else -> InputType.VALID
    }

    fun isTitleValid(title: String): Boolean {
        val inputType = isInputValid(title)
        _titleInputValid.postValue(inputType)
        return inputType == InputType.VALID
    }

    fun validateInput() {
        if (isTitleValid(title)) {
            addOrEditTask()
        }
    }

    private fun addOrEditTask() {
        taskModel?.let { model ->
            updateTask(model.copy(title = title, description = description, iconUrl = url))
        } ?: addTask(TaskModel(title = title, description = description, iconUrl = url))
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }
}
