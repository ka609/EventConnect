package com.example.eventconnect.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.EventRepository
import com.example.eventconnect.model.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadEvents()
        syncWithApi()
    }

    fun loadEvents() {
        viewModelScope.launch {
            eventRepository.getAllEvents().collect { eventsList ->
                _events.value = eventsList
            }
        }
    }

    fun filterEventsByCategory(category: String) {
        viewModelScope.launch {
            eventRepository.getEventsByCategory(category).collect { eventsList ->
                _events.value = eventsList
            }
        }
    }

    private fun syncWithApi() {
        viewModelScope.launch {
            _isLoading.value = true
            eventRepository.syncWithApi()
            _isLoading.value = false
        }
    }
}
