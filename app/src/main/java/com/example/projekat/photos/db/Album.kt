package com.example.projekat.photos.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Album(
    @PrimaryKey val photoId: String,
    val catId: String,
    val url: String? = "",
    val width: Int,
    val height: Int,
)
