package com.mindflip.memorygame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _isPremium = MutableLiveData<Boolean>(false)
    val isPremium: LiveData<Boolean> = _isPremium

    private val _completedGamesCount = MutableLiveData<Int>(0)
    val completedGamesCount: LiveData<Int> = _completedGamesCount

    fun setPremium(premium: Boolean) {
        _isPremium.value = premium
    }

    fun incrementCompletedGames() {
        _completedGamesCount.value = (_completedGamesCount.value ?: 0) + 1
    }
}
