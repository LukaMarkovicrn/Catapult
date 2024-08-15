package com.example.projekat.cats.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CatDao {


    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(cat: Cat)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(list: List<Cat>)

    @Query("SELECT * FROM Cat")
    suspend fun getAll(): List<Cat>

    @Query("SELECT * FROM Cat")
    fun observeAll(): Flow<List<Cat>>

    @Query("SELECT * FROM Cat WHERE id = :catId")
    fun get(catId: String): Cat

    @Query("SELECT * FROM Cat WHERE id = :catId")
    fun observeCatDetails(catId: String): Flow<Cat>


//    @Transaction
//    @Query(
//        """
//            SELECT * FROM Cat WHERE id = :catId
//        """
//    )
//    suspend fun getUserWithAlbums(userId: Int): UserWithAlbums
}