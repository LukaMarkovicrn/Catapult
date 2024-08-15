package com.example.projekat.leaderBoard.repository

import com.example.projekat.leaderBoard.api.LeaderBoardApi
import com.example.projekat.leaderBoard.api.model.LeaderBoardPost
import javax.inject.Inject

class LeaderBoardRepository @Inject constructor(
    private val leaderboardApi: LeaderBoardApi,
) {
    suspend fun fetchLeaderboard() = leaderboardApi.fetchLeaderBoard()
    suspend fun postLeaderboard(leaderboardPost : LeaderBoardPost) =
        leaderboardApi.postLeaderBoard(leaderboardPost)
}