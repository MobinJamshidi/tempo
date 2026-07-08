package com.mobinjam.tempo.feature.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class StudyLauncher : ViewModel() {

    private val _pendingCategory = MutableStateFlow<String?>(null)
    val pendingCategory: StateFlow<String?> = _pendingCategory.asStateFlow()

    private val _navigateToStudy = MutableStateFlow(false)
    val navigateToStudy: StateFlow<Boolean> = _navigateToStudy.asStateFlow()

    fun requestStartStudy(category: String?) {
        _pendingCategory.value = category
        _navigateToStudy.value = true
    }

    fun consumeNavigation() {
        _navigateToStudy.value = false
    }

    fun consumeCategory(): String? {
        val c = _pendingCategory.value
        _pendingCategory.value = null
        return c
    }
}