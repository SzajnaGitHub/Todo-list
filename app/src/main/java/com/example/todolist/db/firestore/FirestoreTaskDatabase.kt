package com.example.todolist.db.firestore

import com.example.todolist.data.repository.TasksRepository.Companion.PAGE_SIZE
import com.example.todolist.model.TaskModel
import com.example.todolist.utils.NetworkManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import durdinapps.rxfirebase2.RxFirestore
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreTaskDatabase @Inject constructor(
    firestore: FirebaseFirestore,
    private val networkManager: NetworkManager
) {
    private val taskCollection = firestore.collection(TASKS_COLLECTION)

    /**
     * FUNCTION BELOW IS A BAD PRACTISE, IN PRODUCTION APP I WOULD NEVER COMPLETE THE STREAM LIKE THAT
     * BUT ACCORDING TO THE FIREBASE DOCUMENTATION THIS IS THE RIGHT WAY TO HANDLE INPUT WHEN THERE IS NO NETWORK CONNECTION
     * https://firebase.google.com/docs/firestore/manage-data/enable-offline [VIDEO TIMESTAMP: 11:17]
     */
    fun insertTask(task: TaskModel): Completable = if (networkManager.hasInternetConnection()) {
        RxFirestore.addDocument(taskCollection, task).ignoreElement()
    } else {
        taskCollection.add(task)
        Completable.complete()
    }

    fun deleteTask(task: TaskModel) =
        RxFirestore.getCollection(taskCollection.whereEqualTo(TASK_ID, task.id))
            .flatMapCompletable {
                it.documents.firstOrNull()?.reference?.let { reference ->
                    RxFirestore.deleteDocument(reference)
                } ?: Completable.error(NoSuchElementException())
            }

    /**
     * FUNCTION BELOW IS A BAD PRACTISE, IN PRODUCTION APP I WOULD NEVER COMPLETE THE STREAM LIKE THAT
     * BUT ACCORDING TO THE FIREBASE DOCUMENTATION THIS IS THE RIGHT WAY TO HANDLE INPUT WHEN THERE IS NO NETWORK CONNECTION
     * https://firebase.google.com/docs/firestore/manage-data/enable-offline [VIDEO TIMESTAMP: 11:17]
     */
    fun updateTask(task: TaskModel) =
        RxFirestore.getCollection(taskCollection.whereEqualTo(TASK_ID, task.id))
            .flatMapCompletable {
                if (networkManager.hasInternetConnection()) {
                    it.documents.firstOrNull()?.reference?.let { reference ->
                        RxFirestore.updateDocument(
                            reference, mapOf(
                                TASK_TITLE to task.title,
                                TASK_DESCRIPTION to task.description,
                                TASK_ICON_URL to task.iconUrl
                            )
                        )
                    } ?: Completable.error(NoSuchElementException())
                } else {
                    it.documents.firstOrNull()?.let { document ->
                        taskCollection.document(document.id).set(
                            mapOf(
                                TASK_TITLE to task.title,
                                TASK_DESCRIPTION to task.description,
                                TASK_ICON_URL to task.iconUrl
                            ), SetOptions.merge()
                        )
                        Completable.complete()
                    } ?: Completable.error(NoSuchElementException())
                }
            }

    fun getInitialCollection() = Single.create<QuerySnapshot> { emitter ->
        taskCollection
            .orderBy(TASK_DATE, Query.Direction.DESCENDING)
            .limit(PAGE_SIZE.toLong())
            .get()
            .addOnSuccessListener { emitter.onSuccess(it) }
            .addOnFailureListener { emitter.onError(it) }
    }

    fun getNextList(lastDocumentSnapshot: QuerySnapshot) = Single.create<QuerySnapshot> { emitter ->
        taskCollection
            .orderBy(TASK_DATE, Query.Direction.DESCENDING)
            .limit(PAGE_SIZE.toLong())
            .startAfter(lastDocumentSnapshot.documents[lastDocumentSnapshot.size() - 1])
            .get()
            .addOnSuccessListener { emitter.onSuccess(it) }
            .addOnFailureListener { emitter.onError(it) }
    }

    fun observeUserInput(): Observable<Unit> = Observable.create<Unit> { emitter ->
        taskCollection.addSnapshotListener { _, error ->
            if (error != null) {
                emitter.onError(error)
            } else {
                emitter.onNext(Unit)
            }
        }
    }
        .subscribeOn(Schedulers.io())

    companion object {
        private const val TASK_DATE = "dateInMillis"
        private const val TASK_ID = "id"
        private const val TASK_TITLE = "title"
        private const val TASK_DESCRIPTION = "description"
        private const val TASK_ICON_URL = "iconUrl"
        private const val TASKS_COLLECTION = "tasks"
    }
}
