package com.example.projekat.photos.gallery

import com.example.projekat.photos.grid.model.PhotoUiModel

interface PhotoGalleryContract {
    data class PhotoGalleryState(
        val photos: List<PhotoUiModel> = emptyList(),
    )
}