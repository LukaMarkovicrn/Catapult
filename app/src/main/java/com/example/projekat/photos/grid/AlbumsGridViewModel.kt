package com.example.projekat.photos.grid

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.navigation.catId
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.example.projekat.photos.grid.AlbumGridContract.AlbumGridUiState
import com.example.projekat.photos.mappers.asPhotoUiModel
import com.example.projekat.photos.repository.PhotoRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlbumsGridViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository
) : ViewModel() {

    private val catId: String = savedStateHandle.catId

    private val _state = MutableStateFlow(AlbumGridUiState())
    val state = _state.asStateFlow()
    private fun setState(reducer: AlbumGridUiState.() -> AlbumGridUiState) = _state.update(reducer)

    init {
        fetchAlbums()
        observeAlbums()
    }


    private fun fetchAlbums() {
        viewModelScope.launch {
            setState { copy(updating = true) }
            try {
                withContext(Dispatchers.IO) {
                    photoRepository.fetchCatPhotos(catId = catId)
                    Log.e("FETCH", "Fetch Photos")
                }
            } catch (error: Exception) {
                Log.d("FETCH", "Fetch Photos Error", error)
            }
            setState { copy(updating = false) }
        }
    }

    private fun observeAlbums() {
        viewModelScope.launch {
            photoRepository.observeCatPhotos(catId = catId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(photos = it.map { it.asPhotoUiModel() }) }
                }
        }
    }



}