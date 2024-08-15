package com.example.projekat.cats.details

import com.example.projekat.cats.list.model.CatUiModel
import com.example.projekat.photos.grid.model.PhotoUiModel

interface CatDetailsContract {
    data class CatDetailsState(
        val fetching: Boolean = true,
        val cat: CatUiModel? = null,
        val image: PhotoUiModel? = null,
        val error: Boolean = false
    )
}