package com.example.projekat.Kviz.ui

import com.example.projekat.Kviz.model.KvizQuestion3

interface KvizContract {
    data class QuizState(
        val questions: List<KvizQuestion3> = emptyList(),
        val loading: Boolean = false,
        val currentQuestionIndex: Int = 0,
        val correctAnswers: Int = 0,
        val selectedOption: String? = null,
        val quizFinished: Boolean = false,
        val score: Float = 0f,
        val timeRemaining: Long = 300000L,
        val error: Exception? = null,
        val showExitDialog: Boolean = false
    )
    sealed class QuizEvent {
        data object StopQuiz: QuizEvent()
        data object ContinueQuiz: QuizEvent()
        data object FinishQuiz : QuizEvent()
        data class OptionSelected(val optionIndex: Int) : QuizEvent()
        data class PublishScore(val score: Float) : QuizEvent()
    }
}