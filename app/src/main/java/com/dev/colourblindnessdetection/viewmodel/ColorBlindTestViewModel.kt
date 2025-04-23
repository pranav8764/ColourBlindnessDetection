package com.dev.colourblindnessdetection.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dev.colourblindnessdetection.data.TestRepository
import com.dev.colourblindnessdetection.model.ColorBlindTestQuestion
import com.dev.colourblindnessdetection.model.TestResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for the color blindness test.
 */
class ColorBlindTestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = TestRepository(application.applicationContext)

    // State for the current question index
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    // State for the list of questions
    private val _questions = MutableStateFlow<List<ColorBlindTestQuestion>>(emptyList())
    val questions: StateFlow<List<ColorBlindTestQuestion>> = _questions.asStateFlow()

    // State for the current question
    private val _currentQuestion = MutableStateFlow<ColorBlindTestQuestion?>(null)
    val currentQuestion: StateFlow<ColorBlindTestQuestion?> = _currentQuestion.asStateFlow()

    // State for the user's responses
    private val _responses = MutableStateFlow<MutableList<Pair<ColorBlindTestQuestion, String>>>(mutableListOf())
    val responses: StateFlow<List<Pair<ColorBlindTestQuestion, String>>> = _responses.asStateFlow()

    // State for the test result
    private val _testResult = MutableStateFlow<TestResult?>(null)
    val testResult: StateFlow<TestResult?> = _testResult.asStateFlow()

    // State to track if the test is complete
    private val _isTestComplete = MutableStateFlow(false)
    val isTestComplete: StateFlow<Boolean> = _isTestComplete.asStateFlow()

    // Initialize the test
    init {
        loadQuestions()
    }

    /**
     * Loads the test questions from the repository.
     */
    private fun loadQuestions() {
        viewModelScope.launch {
            val testQuestions = repository.getTestQuestions()
            _questions.value = testQuestions
            
            if (testQuestions.isNotEmpty()) {
                _currentQuestion.value = testQuestions[0]
            }
        }
    }

    /**
     * Records the user's answer to the current question and advances to the next question.
     */
    fun answerQuestion(answer: String) {
        val currentQuestion = _currentQuestion.value ?: return
        
        // Record the user's response
        _responses.update { responses ->
            val newResponses = responses.toMutableList()
            newResponses.add(Pair(currentQuestion, answer))
            newResponses
        }
        
        // Move to the next question or complete the test
        if (_currentQuestionIndex.value < _questions.value.size - 1) {
            _currentQuestionIndex.update { it + 1 }
            updateCurrentQuestion()
        } else {
            completeTest()
        }
    }

    /**
     * Updates the current question based on the current index.
     * This should be called after changing the current index.
     */
    fun updateCurrentQuestion() {
        val index = _currentQuestionIndex.value
        if (index >= 0 && index < _questions.value.size) {
            _currentQuestion.value = _questions.value[index]
        }
    }

    /**
     * Completes the test and calculates the results.
     */
    private fun completeTest() {
        _isTestComplete.value = true
        _testResult.value = repository.analyzeResults(_responses.value)
    }

    /**
     * Restarts the test.
     */
    fun restartTest() {
        _currentQuestionIndex.value = 0
        _responses.value = mutableListOf()
        _isTestComplete.value = false
        _testResult.value = null
        updateCurrentQuestion()
    }
}
