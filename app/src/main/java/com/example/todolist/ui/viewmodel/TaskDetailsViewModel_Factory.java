// Generated by Dagger (https://dagger.dev).
package com.example.todolist.ui.viewmodel;

import com.example.todolist.repository.TasksRepository;
import dagger.internal.Factory;
import javax.inject.Provider;

@SuppressWarnings({
    "unchecked",
    "rawtypes"
})
public final class TaskDetailsViewModel_Factory implements Factory<TaskDetailsViewModel> {
  private final Provider<TasksRepository> tasksRepositoryProvider;

  public TaskDetailsViewModel_Factory(Provider<TasksRepository> tasksRepositoryProvider) {
    this.tasksRepositoryProvider = tasksRepositoryProvider;
  }

  @Override
  public TaskDetailsViewModel get() {
    return newInstance(tasksRepositoryProvider.get());
  }

  public static TaskDetailsViewModel_Factory create(
      Provider<TasksRepository> tasksRepositoryProvider) {
    return new TaskDetailsViewModel_Factory(tasksRepositoryProvider);
  }

  public static TaskDetailsViewModel newInstance(TasksRepository tasksRepository) {
    return new TaskDetailsViewModel(tasksRepository);
  }
}