package com.example.projekat.leaderBoard.api

import com.example.projekat.leaderBoard.api.model.LeaderBoardApiModel
import com.example.projekat.leaderBoard.api.model.LeaderBoardPost
import com.example.projekat.leaderBoard.api.model.LeaderBoardResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface LeaderBoardApi {

    @GET("leaderboard?category=3")
    suspend fun fetchLeaderBoard(): List<LeaderBoardApiModel>

    @POST("leaderboard")
    suspend fun postLeaderBoard(@Body leaderboardPost: LeaderBoardPost) : LeaderBoardResponse
}