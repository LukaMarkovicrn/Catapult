package com.example.projekat.photos.grid


import com.example.projekat.photos.grid.model.PhotoUiModel

interface AlbumGridContract {
    data class AlbumGridUiState(
        val updating: Boolean = false,
        val photos: List<PhotoUiModel> = emptyList(),
    )
}