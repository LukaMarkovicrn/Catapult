package com.example.projekat.photos.gallery

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.navigation.catId
import com.example.projekat.photos.mappers.asPhotoUiModel
import com.example.projekat.photos.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoGalleryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val photoRepository: PhotoRepository,
) : ViewModel() {

    private val catId = savedStateHandle.catId

    private val _state = MutableStateFlow(PhotoGalleryContract.PhotoGalleryState())
    val state = _state.asStateFlow()
    private fun setState(reducer: PhotoGalleryContract.PhotoGalleryState.() -> PhotoGalleryContract.PhotoGalleryState) = _state.update(reducer)

    init {
        observeCatPhotos()
    }

    private fun observeCatPhotos() {
        viewModelScope.launch {
            photoRepository.observeCatPhotos(catId = catId)
                .distinctUntilChanged()
                .collect {
                    setState { copy(photos = it.map { it.asPhotoUiModel() }) }
                    Log.e("OBSERVE", "Observe cat photos ${state.value.photos}")
                }
        }
    }

}