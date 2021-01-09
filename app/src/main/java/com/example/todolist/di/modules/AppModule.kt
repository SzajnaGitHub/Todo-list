package com.example.todolist.di.modules

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {

    @Provides
    @Singleton
    fun provideFirestoreDatabase() = FirebaseFirestore.getInstance()

}
