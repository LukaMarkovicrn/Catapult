package com.example.projekat.photos.repository

import androidx.room.withTransaction
import com.example.projekat.db.AppDatabase
import com.example.projekat.photos.api.PhotosApi
import com.example.projekat.photos.mappers.asAlbumDbModel
import javax.inject.Inject

class PhotoRepository @Inject constructor(
    private val photosApi: PhotosApi,
    private val database: AppDatabase,
) {



    suspend fun fetchCatPhotos(catId: String) {
        val allPhotos = photosApi.fetchPhotos(catId = catId)
            .map { it.asAlbumDbModel(catId) }
            .toMutableList()

        database.withTransaction {
            database.albumDao().insertAll(list = allPhotos)
        }
    }

    suspend fun fetchPhoto(photoId: String, catId: String) {
        val photo = photosApi.fetchPhotoById(photoId = photoId).asAlbumDbModel(catId)
        database.albumDao().insert(photo)
    }

    suspend fun getAllPhotos() = database.albumDao().getAll()

    suspend fun getPhotosByCatId(catId: String) = database.albumDao().getPhotosByCatId(catId = catId)

    fun observeCatPhotos(catId: String) = database.albumDao().observeCatPhotos(catId = catId)
}