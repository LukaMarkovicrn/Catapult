package com.example.projekat.Kviz.ui

import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekat.Kviz.dp.Kviz
import com.example.projekat.Kviz.model.KvizQuestion3
import com.example.projekat.Kviz.reposutory.KvizRepository
import com.example.projekat.auth.AuthStore
import com.example.projekat.cats.repository.CatsRepository
import com.example.projekat.leaderBoard.api.model.LeaderBoardPost
import com.example.projekat.leaderBoard.repository.LeaderBoardRepository
import com.example.projekat.photos.repository.PhotoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class KvizViewModel @Inject constructor(
    private val kvizRepository: KvizRepository,
    private val photoRepository: PhotoRepository,
    private val catsRepository: CatsRepository,
    private val leaderboardRepository: LeaderBoardRepository,
    private val authStore: AuthStore
) : ViewModel() {

    private val _state = MutableStateFlow(KvizContract.QuizState())
    val state = _state.asStateFlow()
    private fun setState(reducer: KvizContract.QuizState.() -> KvizContract.QuizState) = _state.update(reducer)


    private val events = MutableSharedFlow<KvizContract.QuizEvent>()
    fun setEvent(event: KvizContract.QuizEvent) { viewModelScope.launch { events.emit(event) } }

    private var timer: CountDownTimer? = null

    init {
        observeEvents()
        getQuestions()
    }

    private fun getQuestions(){
        viewModelScope.launch {
            setState { copy(loading = true) }
            try{
                withContext(Dispatchers.IO){
                    val questions = generateQuestions1()

                    setState { copy(questions = questions, loading = false) }
                }
                startTimer()
            } catch (error: Exception){
                Log.d("QuizViewModel", "Exception", error)
                setState { copy(loading = false, error = error) }
            }
            finally {
                Log.d("QUIZ", "Questions generated: ${state.value.questions}")
            }
        }
    }

    private fun observeEvents(){
        viewModelScope.launch {
            events.collect { event ->
                when(event){
                    is KvizContract.QuizEvent.StopQuiz -> {
                        timer?.cancel()
                        setState { copy(showExitDialog = true) }
                    }
                    is KvizContract.QuizEvent.ContinueQuiz -> {

                        startTimer(state.value.timeRemaining)
                        setState { copy(showExitDialog = false) }
                    }
                    is KvizContract.QuizEvent.FinishQuiz -> finishQuiz()
                    is KvizContract.QuizEvent.OptionSelected -> {}
                    is KvizContract.QuizEvent.PublishScore -> publish()
                }
            }
        }
    }

    private fun startTimer(time: Long = 300000) {
        timer = object : CountDownTimer(time, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                setState { copy(timeRemaining = millisUntilFinished) }
            }

            override fun onFinish() {
                setState { copy(questions = emptyList()) }
                setState { copy(quizFinished = true)}
                val score = calculateScore().coerceAtMost(100.00f)
                setState { copy(score = score) }
            }
        }
        timer?.start()
    }

    fun submitAnswer(option: String){
        viewModelScope.launch {
            val currentQuestion = state.value.questions[state.value.currentQuestionIndex]
            val correctAnswer = currentQuestion.correctAnswer
            setState { copy(selectedOption = option)}
            var isCorrect = false
            if(option == correctAnswer){
                isCorrect = true
            }



            if (isCorrect) {
                setState {
                    copy(
                        correctAnswers = state.value.correctAnswers + 1,
                    )
                }
            }



            delay(1000)

            if (_state.value.currentQuestionIndex < state.value.questions.size - 1) {
                setState {
                    copy(
                        currentQuestionIndex = currentQuestionIndex + 1,
                        selectedOption = null
                    )
                }
            }
            else {
                setState { copy(questions = emptyList()) }
                setState { copy(quizFinished = true)}
                val score = calculateScore().coerceAtMost(100.00f)
                setState { copy(score = score) }
                timer?.cancel()
            }
        }
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            Log.d("QUIZ", "Quiz completed with score: ${state.value.score}")

            val userProfile = authStore.data.first()

            val quizResult = Kviz(
                nickname = userProfile.nickname,
                score = state.value.score,
                date = Date().toString(),
            )
            kvizRepository.insertQuizResult(quizResult)
        }
    }

    private fun publish(){
        viewModelScope.launch {
            Log.d("QUIZ", "Quiz completed with score: ${state.value.score}")

            val userProfile = authStore.data.first()

            val leaderboardPost = LeaderBoardPost(
                nickname = userProfile.nickname,
                result = state.value.score,
                category = 3
            )
            val response = leaderboardRepository.postLeaderboard(leaderboardPost)

            val quizResult = Kviz(
                nickname = userProfile.nickname,
                score = state.value.score,
                date = Date().toString(),
                ranking = response.ranking
            )
            kvizRepository.insertQuizResult(quizResult)
        }
    }

    private fun calculateScore(): Float {
        Log.d("QUIZ", "Correct answers: ${state.value.correctAnswers}, Time remaining: ${state.value.timeRemaining}")

        return (state.value.correctAnswers * 2.5 * (1 + ((state.value.timeRemaining / 1000) + 120) / 300)).toFloat()
    }

    private suspend fun generateQuestions1(): List<KvizQuestion3>{
        val cats = catsRepository.getAllCats()
        val questionCats = cats.shuffled().take(30)
        val questions = mutableListOf<KvizQuestion3>()

        questionCats.forEach { cat ->
            var photos = photoRepository.getAllPhotos().filter { it.catId == cat.id }.shuffled()

            if(photos.isEmpty()){
                try {
                    photoRepository.fetchPhoto(cat.reference_image_id, cat.id)
                } catch (error: Exception){
                    Log.d("QUIZ", "Error fetching photo", error)
                }

//                photos = photoRepository.getAllPhotos().filter { it.catId == cat.id }.shuffled()
            }
            Log.d("QUIZ", "Question Photos: $photos")



            when ((1..2).random()) {
                1 -> if(photos.isNotEmpty()){


                    val cat2 = cats.filter { it.id != cat.id  && it.weight != cat.weight }.shuffled().first()

                    var photos2 = photoRepository.getAllPhotos().filter { it.catId == cat2.id }.shuffled()

                    if(photos2.isEmpty()){
                        try {
                            photoRepository.fetchPhoto(cat2.reference_image_id, cat2.id)
                        } catch (error: Exception){
                            Log.d("QUIZ", "Error fetching photo", error)
                        }
//                        photos2 = photoRepository.getAllPhotos().filter { it.catId == cat2.id }.shuffled()
                    }

                    if(photos2.isNotEmpty() ) {




//                    val options1 = mutableMapOf<String,String>()
//
//                    options1.put(cat.name,photos.first().url.toString())
//                    options1.put(cat2.name,photos2.first().url.toString())
//                    val shuffledOptionsList = options1.entries.shuffled()
//                    val shuffledOptionsMap = shuffledOptionsList.associate { it.toPair() }

                        var options1 = mutableListOf<String>()
                        options1.add(cat.name + "|" + photos.first().url)
                        options1.add(cat2.name + "|" + photos2.first().url)

                        options1 = options1.shuffled().toMutableList()

                        val options = mutableListOf<String>()
                        options.add(options1[0].split('|')[0])
                        options.add(options1[1].split('|')[0])


                        var correctCat = cat2.name
                        var inCorrectCat = cat.name
                        if(cat.weight > cat2.weight){
                            correctCat = cat.name
                            inCorrectCat = cat2.name
                        }


                        questions.add(
                            KvizQuestion3(
                                question = "What breed is heavier on average?",
                                correctAnswer = correctCat,
                                incorrectAnswer = inCorrectCat,
                                options = options1,
                                optionValues = options,
                                correctImageUrl = photos.first().url,
                                inCorrectImageUrl = photos2.first().url


                            )
                        )
                    }
                }

                2 -> if(photos.isNotEmpty()){


                    val cat2 = cats.filter { it.id != cat.id  && it.life_span != cat.life_span }.shuffled().first()

                    var photos2 = photoRepository.getAllPhotos().filter { it.catId == cat2.id }.shuffled()

                    if(photos2.isEmpty()){
                        try {
                            photoRepository.fetchPhoto(cat2.reference_image_id, cat2.id)
                        } catch (error: Exception){
                            Log.d("QUIZ", "Error fetching photo", error)
                        }
//                        photos2 = photoRepository.getAllPhotos().filter { it.catId == cat2.id }.shuffled()
                    }

                    if(photos2.isNotEmpty() ) {




//                    val options1 = mutableMapOf<String,String>()
//
//                    options1.put(cat.name,photos.first().url.toString())
//                    options1.put(cat2.name,photos2.first().url.toString())
//                    val shuffledOptionsList = options1.entries.shuffled()
//                    val shuffledOptionsMap = shuffledOptionsList.associate { it.toPair() }

                        var options1 = mutableListOf<String>()
                        options1.add(cat.name + "|" + photos.first().url)
                        options1.add(cat2.name + "|" + photos2.first().url)

                        options1 = options1.shuffled().toMutableList()

                        val options = mutableListOf<String>()
                        options.add(options1[0].split('|')[0])
                        options.add(options1[1].split('|')[0])

                        questions.add(
                            KvizQuestion3(
                                question = "Which cat on average lives longer?",
                                correctAnswer = cat.name,
                                incorrectAnswer = cat2.name,
                                optionValues = options,
                                options = options1,
                                correctImageUrl = photos.first().url,
                                inCorrectImageUrl = photos2.first().url
                            )
                        )
                    }
                }


            }
        }





        return questions.take(20)
    }

}