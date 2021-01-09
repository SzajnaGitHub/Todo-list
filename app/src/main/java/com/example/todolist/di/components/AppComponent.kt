package com.example.todolist.di.components

import com.example.todolist.ui.Application
import com.example.todolist.di.modules.AppModule
import com.example.todolist.di.modules.BuildersModule
import com.example.todolist.di.modules.DatabaseModule
import com.example.todolist.di.modules.ViewModelModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Component(
    modules = [
        AndroidSupportInjectionModule::class,
        BuildersModule::class,
        AppModule::class,
        DatabaseModule::class,
        ViewModelModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<Application> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun app(app: Application): Builder

        fun build(): AppComponent
    }

}
