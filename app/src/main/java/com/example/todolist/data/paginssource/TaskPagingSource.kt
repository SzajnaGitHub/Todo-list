package com.example.todolist.data.paginssource

import androidx.paging.rxjava2.RxPagingSource
import com.example.todolist.db.firestore.FirestoreTaskDatabase
import com.example.todolist.model.TaskModel
import com.google.firebase.firestore.QuerySnapshot
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class TaskPagingSource(
    private val db: FirestoreTaskDatabase
) : RxPagingSource<QuerySnapshot, TaskModel>() {

    override fun loadSingle(params: LoadParams<QuerySnapshot>): Single<LoadResult<QuerySnapshot, TaskModel>> =
        (params.key?.let { Single.just(it) } ?: db.getInitialCollection())
            .subscribeOn(Schedulers.io())
            .flatMap { currentPage ->

                if (currentPage.size() < 1) {
                    Single.just(Pair(currentPage, null))
                } else {
                    db.getNextList(currentPage).map { nextPage ->
                        Pair(currentPage, nextPage)
                    }
                }
            }
            .map { (currentPage, nextPage) ->
                LoadResult.Page(
                    data = currentPage.toObjects(TaskModel::class.java),
                    prevKey = null,
                    nextKey = if (nextPage == null || nextPage.isEmpty) null else nextPage
                )
            }
}
