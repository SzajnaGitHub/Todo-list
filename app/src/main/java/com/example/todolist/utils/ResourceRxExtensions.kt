package com.example.todolist.utils

import androidx.lifecycle.MutableLiveData
import com.example.todolist.utils.result.Result
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

fun <T : Any> Observable<T>.asLiveData(liveData: MutableLiveData<Event<Resource<T>>>): Disposable = this
    .doOnSubscribe { liveData.postValue(Event(Resource.Loading)) }
    .doOnError { liveData.postValue(Event(Resource.Failure(it))) }
    .subscribe { liveData.postValue(Event(Resource.Success(it))) }

fun Completable.asLiveData(liveData: MutableLiveData<Event<Resource<Unit>>>, result: Result): Disposable = this
    .doOnSubscribe { liveData.postValue(Event(Resource.Loading)) }
    .doOnError { liveData.postValue(Event(Resource.Failure(it, result.errorMessage))) }
    .subscribe { liveData.postValue(Event(Resource.Success(Unit, result.successMessage))) }
