package com.example.projekat.leaderBoard.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.auth.AuthStore
import com.example.projekat.leaderBoard.mappers.asLeaderboardUiModel
import com.example.projekat.leaderBoard.repository.LeaderBoardRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LeaderBoardListViewModel @Inject constructor(
    private val repository: LeaderBoardRepository,
    private val authStore: AuthStore
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderBoardListContract.LeaderboardListState())
    val state = _state.asStateFlow()
    private fun setState(reducer: LeaderBoardListContract.LeaderboardListState.() -> LeaderBoardListContract.LeaderboardListState) = _state.update(reducer)


    init {
        fetchLeaderboard()
    }

    private fun fetchLeaderboard() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                val userProfile = authStore.data.first()
                val data = withContext(Dispatchers.IO) {
                    repository.fetchLeaderboard().mapIndexed { index, it ->
                        it.asLeaderboardUiModel(index + 1)
                    }
                }
                Log.e("FETCH", "Fetch Leaderboard")
                setState { copy(results = data) }
                setState { copy(nickname = userProfile.nickname) }
            } catch (error: Exception) {
                Log.d("FETCH", "Fetch Leaderboard Error", error)
            } finally {
                setState { copy(loading = false) }
            }
        }
    }
}