package com.example.projekat.cats.repository

import com.example.projekat.cats.api.CatsApi
import com.example.projekat.cats.mappers.asCatDbModel
import com.example.projekat.db.AppDatabase
import javax.inject.Inject

class CatsRepository @Inject constructor(
    private val catsApi: CatsApi,
    private val database: AppDatabase
){

    suspend fun fetchAllCats() {
        val cats = catsApi.fetchAllCats()
        database.catDao().insertAll(list = cats.map { it.asCatDbModel() })
    }

    suspend fun getAllCats() = database.catDao().getAll()

    suspend fun getCatDetails(catId: String){
        database.catDao().get(catId = catId)
    }

    fun observeCats() = database.catDao().observeAll()
    fun observeCatDetails(catId: String) = database.catDao().observeCatDetails(catId = catId)
}