package com.example.projekat.leaderBoard.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderBoardPost(
    val nickname: String,
    val result: Float,
    val category: Int,
)

@Serializable
data class LeaderBoardResponse(
    val result: LeaderBoardApiModel,
    val ranking: Int
)
