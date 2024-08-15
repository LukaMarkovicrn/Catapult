package com.example.projekat.cats.api

import com.example.projekat.cats.api.model.CatApiModel
import retrofit2.http.GET
import retrofit2.http.Path


interface CatsApi {

    @GET("breeds")
    suspend fun fetchAllCats(): List<CatApiModel>

    @GET("breeds/{breed_id}")
    suspend fun getCat(
        @Path("breed_id") catId: String,
    ): CatApiModel


//    @GET("images/{imageId}")
//    suspend fun getImage(
//        @Path("imageId") imageId: String,
//    ): ImageApiModel

//    @GET("users/{id}/albums")
//    suspend fun getUserAlbums(
//        @Path("id") userId: Int,
//    ): List<AlbumApiModel>

//    @DELETE("users/{id}")
//    suspend fun deleteUser()
}
