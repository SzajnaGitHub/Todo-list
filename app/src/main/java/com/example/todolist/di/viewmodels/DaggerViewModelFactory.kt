package com.example.todolist.di.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DaggerViewModelFactory @Inject constructor(
    private val classMapping: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        classMapping[modelClass]?.get() as T
        val creator: @JvmSuppressWildcards Provider<ViewModel> = classMapping[modelClass] ?: throw IllegalArgumentException("Unregistered model class $modelClass")
        return creator.get() as T
    }
}
