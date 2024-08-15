package com.example.projekat.Kviz.dp

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_results")
data class Kviz(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nickname: String,
    val score: Float,
    val date: String,
    val ranking: Int? = null
)