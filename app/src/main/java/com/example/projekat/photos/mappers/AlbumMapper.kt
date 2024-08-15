package com.example.projekat.photos.mappers

import com.example.projekat.photos.api.model.PhotoApiModel
import com.example.projekat.photos.db.Album
import com.example.projekat.photos.grid.model.PhotoUiModel

fun PhotoApiModel.asAlbumDbModel(catId: String): Album {
    return Album(
        photoId = this.photoId,
        catId = catId,
        url = this.url,
        width = this.width,
        height = this.height,
    )
}

fun Album.asPhotoUiModel(): PhotoUiModel {
    return PhotoUiModel(
        photoId = this.photoId,
        catId = this.catId,
        url = this.url,
        width = this.width,
        height = this.height,
    )
}