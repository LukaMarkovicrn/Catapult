package com.example.projekat.leaderBoard.list

import com.example.projekat.auth.AuthStore
import com.example.projekat.leaderBoard.list.model.LeaderBoardUiModel

interface LeaderBoardListContract {
    data class LeaderboardListState(
        val nickname: String = "",
        val loading: Boolean = true,
        val results: List<LeaderBoardUiModel> = emptyList(),
        val error: Boolean = false,
    )
}