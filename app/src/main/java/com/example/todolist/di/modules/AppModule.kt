package com.example.todolist.di.modules

import android.content.Context
import android.net.ConnectivityManager
import com.example.todolist.di.annotations.NetworkAvailable
import com.example.todolist.ui.Application
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.Subject
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideFirestoreDatabase() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun connectivityManager(context: Application) = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    @NetworkAvailable
    fun provideNetworkAvailableSubject(): Subject<Boolean> = BehaviorSubject.create()

}
