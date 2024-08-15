package com.example.projekat.photos.grid.model

data class PhotoUiModel(
    val photoId: String,
    val url: String? = "",
    val catId: String,
    val width: Int,
    val height: Int
)
