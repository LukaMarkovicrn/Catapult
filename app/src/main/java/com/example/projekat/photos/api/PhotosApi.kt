package com.example.projekat.photos.api

import com.example.projekat.photos.api.model.PhotoApiModel
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PhotosApi {

    @GET("images/search?limit=25&format=json")
    suspend fun fetchPhotos(@Query("breed_ids") catId: String): List<PhotoApiModel>

    @GET("images/{photoId}")
    suspend fun fetchPhotoById(
        @Path("photoId") photoId: String,
    ): PhotoApiModel

}

//  https://api.thecatapi.com/v1/images/search?breed_ids=BREED_ID&format=json