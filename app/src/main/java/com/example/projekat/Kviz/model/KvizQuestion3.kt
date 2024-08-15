package com.example.projekat.Kviz.model

data class KvizQuestion3(
    val question: String,
    val correctAnswer: String,
    val incorrectAnswer: String,
    val options: List<String>,
    val optionValues: List<String>,
    val correctImageUrl: String? = null,
    val inCorrectImageUrl: String? = null,

)
