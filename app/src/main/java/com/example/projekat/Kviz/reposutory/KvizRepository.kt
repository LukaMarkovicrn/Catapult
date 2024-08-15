package com.example.projekat.Kviz.reposutory

import com.example.projekat.Kviz.dp.Kviz
import com.example.projekat.db.AppDatabase
import javax.inject.Inject

class KvizRepository @Inject constructor(
    private val database: AppDatabase
){
    suspend fun insertQuizResult(result: Kviz) {
        database.kvizDao().insertQuizResult(result)
    }

    fun getAllQuizResults(nickname: String) = database.kvizDao().getQuizResultsForUser(nickname)

    suspend fun getBestScore(userId: String) = database.kvizDao().getBestScore(userId)

    suspend fun getBestPosition(userId: String) = database.kvizDao().getBestRanking(userId)

}