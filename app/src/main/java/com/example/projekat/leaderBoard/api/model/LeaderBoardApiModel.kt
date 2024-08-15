package com.example.projekat.leaderBoard.api.model

import kotlinx.serialization.Serializable

@Serializable
data class LeaderBoardApiModel(
    val nickname: String = "",
    val result: Float = 0f
)
