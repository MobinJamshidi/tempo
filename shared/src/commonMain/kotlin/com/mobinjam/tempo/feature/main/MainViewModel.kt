package com.mobinjam.tempo.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobinjam.tempo.feature.social.domain.ProfileRepository
import kotlinx.coroutines.launch

class MainViewModel(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    init {
        ensureProfile()
    }

    private fun ensureProfile() {
        viewModelScope.launch {
            profileRepository.ensureProfileExists().fold(
                onSuccess = { println("TEMPO_PROFILE: OK") },
                onFailure = { e -> println("TEMPO_PROFILE: FAILED -> ${e.message}") },
            )
        }
    }
}