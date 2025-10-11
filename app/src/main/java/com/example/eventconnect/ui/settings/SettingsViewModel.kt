package com.example.eventconnect.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.AppPreferences
import com.example.eventconnect.model.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: AppPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            preferences.isDarkTheme.collectLatest { isDarkTheme ->
                _uiState.value = _uiState.value.copy(isDarkTheme = isDarkTheme)
            }
        }

        viewModelScope.launch {
            preferences.favoriteCategories.collectLatest { categories ->
                _uiState.value = _uiState.value.copy(favoriteCategories = categories)
            }
        }
    }

    fun toggleDarkTheme(isEnabled: Boolean) {
        viewModelScope.launch {
            preferences.setDarkTheme(isEnabled)
        }
    }

    fun updateFavoriteCategories(categories: Set<Category>) {
        viewModelScope.launch {
            preferences.setFavoriteCategories(categories)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            preferences.clearAll()
        }
    }
}

data class SettingsState(
    val isDarkTheme: Boolean = false,
    val favoriteCategories: Set<Category> = emptySet()
)
