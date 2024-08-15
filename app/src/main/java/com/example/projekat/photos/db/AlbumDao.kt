package com.example.projekat.photos.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow


@Dao
interface AlbumDao {

    @Query("SELECT * FROM Album")
    suspend fun getAll(): List<Album>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<Album>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Album)

    @Query("SELECT * FROM Album WHERE Album.catId = :catId")
    fun observeCatPhotos(catId: String): Flow<List<Album>>

    @Query("SELECT * FROM Album")
    fun observePhotos(): Flow<List<Album>>

    @Query("SELECT * FROM Album WHERE Album.catId = :catId")
    suspend fun getPhotosByCatId(catId: String): List<Album>

    @Query("SELECT * FROM Album WHERE Album.photoId = :photoId")
    fun observePhoto(photoId: String): Flow<Album>
}