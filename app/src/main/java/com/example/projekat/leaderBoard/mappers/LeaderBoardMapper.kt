package com.example.projekat.leaderBoard.mappers

import com.example.projekat.leaderBoard.api.model.LeaderBoardApiModel
import com.example.projekat.leaderBoard.list.model.LeaderBoardUiModel

fun LeaderBoardApiModel.asLeaderboardUiModel(id: Int): LeaderBoardUiModel {
    return LeaderBoardUiModel(
        id = id,
        nickname = this.nickname,
        result = this.result
    )
}