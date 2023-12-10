package com.example.words.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.words.network.ApiFactory
import kotlinx.coroutines.launch

class GameViewModel : ViewModel() {

    private val wordsList: MutableList<String> = mutableListOf()

    private val _score = MutableLiveData(0)
    val score: LiveData<Int> get() = _score

    private val _currentWordCount = MutableLiveData(0)
    val currentWordCount: LiveData<Int> get() = _currentWordCount

    private val _currentScrambledWord = MutableLiveData<String>()
    val currentScrambledWord: LiveData<String> get() = _currentScrambledWord

    private lateinit var currentWord: String

    init {
        Log.d("GameFragment", "GameViewModel created!")
        getNextWord()
    }

    private fun getNextWord() {
        viewModelScope.launch {
            val response = ApiFactory.wordApi.getRandomWord()
            val error = response.errorBody().toString()
            val word = response.body()?.word.toString()
            Log.d("GameViewModel_TAG", error)
            currentWord = word.lowercase()
            val tempWord = currentWord.toCharArray()
            tempWord.shuffle()

            while (String(tempWord).equals(currentWord, false)) {
                tempWord.shuffle()
            }

            if (wordsList.contains(currentWord)) {
                getNextWord()
            }
            else {
                _currentScrambledWord.value = String(tempWord)
                _currentWordCount.value = (_currentWordCount.value)?.inc() //аналогично _currentWordCount++
                wordsList.add(currentWord)
            }
        }
    }

    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWord()
            true
        } else false
    }

    private fun increaseScore() {
        _score.value = (_score.value)?.plus(SCORE_INCREASE)
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWord()
    }

}