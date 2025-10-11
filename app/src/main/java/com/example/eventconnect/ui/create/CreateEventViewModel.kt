package com.example.eventconnect.ui.create

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

data class CreateEventState(
    val title: String = "",
    val description: String = "",
    val date: Date = Date(),
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: String = "OTHER",
    val imageUrl: String = ""
)

class CreateEventViewModel : ViewModel() {

    private val _uiState = MutableLiveData(CreateEventState())
    val uiState: LiveData<CreateEventState> = _uiState

    private val _isCreated = MutableLiveData(false)
    val isCreated: LiveData<Boolean> = _isCreated

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value?.copy(title = title)
    }

    fun updateDescription(description: String) {
        _uiState.value = _uiState.value?.copy(description = description)
    }

    fun updateDate(date: Date) {
        _uiState.value = _uiState.value?.copy(date = date)
    }

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value?.copy(location = location)
    }

    fun updateCoordinates(lat: Double, lon: Double) {
        _uiState.value = _uiState.value?.copy(latitude = lat, longitude = lon)
    }

    fun updateCategory(category: String) {
        _uiState.value = _uiState.value?.copy(category = category)
    }

    fun updateImageUrl(url: String) {
        _uiState.value = _uiState.value?.copy(imageUrl = url)
    }

    fun createEvent() {
        val state = _uiState.value!!
        if (state.title.isNotBlank()) {
            _isCreated.value = true
        }
    }

    fun resetCreationState() {
        _isCreated.value = false
    }
}
