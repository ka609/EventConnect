package com.example.eventconnect.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _isParticipating = MutableStateFlow(false)
    val isParticipating: StateFlow<Boolean> = _isParticipating

    fun checkParticipation(eventId: String) {
        viewModelScope.launch {
            // Exemple : récupérer la participation depuis le repo
            _isParticipating.value = eventRepository.isUserParticipating(eventId)
        }
    }

    fun toggleParticipation(eventId: String) {
        viewModelScope.launch {
            val newState = !_isParticipating.value
            _isParticipating.value = newState
            eventRepository.setParticipation(eventId, newState)
        }
    }
}
