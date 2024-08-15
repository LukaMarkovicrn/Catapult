package com.example.projekat.cats.list

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.auth.AuthStore
import com.example.projekat.cats.db.Cat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

import com.example.projekat.cats.list.CatListContract.CatListState
import com.example.projekat.cats.list.CatListContract.CatListUiEvent
import com.example.projekat.cats.list.model.CatUiModel
import com.example.projekat.cats.mappers.asCatUiModel
import com.example.projekat.cats.repository.CatsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class CatListViewModel @Inject constructor(
    private val repository: CatsRepository,
    private val authStore: AuthStore
) : ViewModel() {

    private val _state = MutableStateFlow(CatListState())
    val state = _state.asStateFlow()
    private fun setState(reducer: CatListState.() -> CatListState) = _state.update(reducer)

    private val events = MutableSharedFlow<CatListUiEvent>()
    fun setEvent(event: CatListUiEvent) = viewModelScope.launch { events.emit(event) }



    init {
        observeEvents()
        fetchAllCats()
        observeSearchQuery()
        observeCats()
    }

    @OptIn(FlowPreview::class)
    private fun observeSearchQuery() {
        viewModelScope.launch {
            events
                .filterIsInstance<CatListUiEvent.SearchQueryChanged>()
                .debounce(2.seconds)
                .collect {
                    // Called only when search query was changed
                    // and once 2 seconds have passed after the last char


                    setState { copy(filteredCats = state.value.cats.filter { item ->
                        item.name.contains(state.value.query, ignoreCase = true)
                    }) }

                    setState { copy(loading = false) }
                }
        }
    }

    private fun observeEvents() {
        viewModelScope.launch {
            events.collect {
                when (it) {
//                    CatListUiEvent.ClearSearch -> Unit
                    CatListUiEvent.CloseSearchMode -> setState { copy(isSearchMode = false) }

                    is CatListUiEvent.SearchQueryChanged -> {
                        println("search query changed")
                        println(it.query)
                        setState { copy(query = it.query) }
                        setState { copy(isSearchMode = true) }
                        setState { copy(loading = true) }
                        // onValueChange from OutlinedTextField is called for every character

                        // We should handle the query text state update here, but make the api call
                        // or any other expensive call somewhere else where we debounce the text changes
//                        it.query // this should be added to state
                    }

                    CatListUiEvent.Dummy -> Unit

//                    is UserListUiEvent.RemoveUser -> {
////                        handleUserDeletion(userId = it.userId)
//                    }
                }
            }
        }
    }

    private fun observeCats() {
        viewModelScope.launch {
//            setState { copy(initialLoading = true) }
            repository.observeCats()
                .distinctUntilChanged()
                .collect {
                    val userProfile = authStore.data.first()
                    Log.i("VIDEO", "Users table updated.")
                    setState {
                        copy(
//                            initialLoading = false,
                            cats = it.map { it.asCatUiModel() },
                        )
                    }
                    setState { copy(nickname = userProfile.nickname) }
                }
        }
    }

    private fun fetchAllCats() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            try {
                withContext(Dispatchers.IO) {
                    repository.fetchAllCats()
                }

            } catch (error: Exception) {
                // TODO Handle error
            } finally {
                setState { copy(loading = false) }
            }
        }
    }

//    private fun Cat.asCatUiModel() = CatUiModel(
//        id = this.id,
//        name = this.name,
//        temperament = this.temperament,
//        origin = this.origin,
//        description = this.description,
//        wikipedia_url = this.wikipedia_url,
//        alt_names = this.alt_names,
//        life_span = this.life_span,
//        weight = this.weight,
//        reference_image_id = this.reference_image_id,
//        rare = this.rare,
//        adaptability = this.adaptability,
//        intelligence = this.intelligence,
//        affection_level = this.affection_level,
//        child_friendly = this.child_friendly,
//        social_needs = this.social_needs
//
//    )



//    private fun CatApiModel.asCatApiModel() = CatUiModel(
//        id = this.id,
//        name = this.name,
//        description = this.description,
//        origin = this.origin
//
////        username = this.username,
//    )
}
