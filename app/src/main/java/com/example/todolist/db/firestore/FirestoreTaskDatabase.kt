package com.example.todolist.db.firestore

import com.example.todolist.model.TaskModel
import com.google.firebase.firestore.*
import durdinapps.rxfirebase2.RxFirestore
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreTaskDatabase @Inject constructor(private val firestore: FirebaseFirestore) {

    private val taskCollection = firestore.collection(TASKS_COLLECTION)

    fun insertTask(task: TaskModel) = RxFirestore.addDocument(taskCollection, task)
        .ignoreElement()

    fun deleteTaskById(id: Int) {
        taskCollection.whereEqualTo("id", id).get()
            .addOnSuccessListener {
                println("TEKST SUCCESS LISTENER DOCUMENTS ${it.documents}")
                it.documents.firstOrNull()?.let {
                    println("TEKST TASK DELETED")
                    RxFirestore.deleteDocument(it.reference)
                        .subscribe()
                }
            }
    }

    fun getAllTasks() {
        taskCollection
            .get()
            .addOnSuccessListener {
                println("TEKST ${it.size()}")
                val result = it.toObjects(TaskModel::class.java)
                println("TEKST ${result}")
            }
            .addOnFailureListener {
                println("TEKST FAILURE $it")
            }
    }

    fun observeTasks() {
        taskCollection
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                snapshots?.documentChanges?.forEach { singleChange ->
                    println("TEKST OLDINDEX ${singleChange.oldIndex} NEWINDEX ${singleChange.newIndex} }")
                    when (singleChange.type) {
                        DocumentChange.Type.ADDED -> println("TEKST DOCUMENT ADDED")
                        DocumentChange.Type.MODIFIED -> println("TEKST DOCUMENT MODIFIED")
                        DocumentChange.Type.REMOVED -> println("TEKST DOCUMENT REMOVED")
                    }
                }
            }
    }

    init {
        observeTasks()
    }

    companion object {
        private const val TASKS_COLLECTION = "tasks"
    }
}
