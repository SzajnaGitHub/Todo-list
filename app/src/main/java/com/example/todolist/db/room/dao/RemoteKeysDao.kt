package com.example.todolist.db.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.todolist.db.room.entities.RemoteKeysEntity
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(remoteKeys: List<RemoteKeysEntity>): Completable

    @Query("DELETE FROM remote_keys")
    fun clearRemoteKeys(): Completable

    @Query("SELECT * FROM remote_keys WHERE repoId = :repoId")
    fun remoteKeysRepoId(repoId: Long): Single<RemoteKeysEntity>

}
