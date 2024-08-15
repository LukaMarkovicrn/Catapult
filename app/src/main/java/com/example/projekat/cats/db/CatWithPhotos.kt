package com.example.projekat.cats.db

import androidx.room.Embedded
import androidx.room.Relation

data class CatWithPhotos(

    @Embedded val user: Cat,

    @Relation(
        parentColumn = "id",
        entityColumn = "catOwnerId",
    )
    val albums: List<String> // Photos
)
