package com.example.eventconnect.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventconnect.data.EventRepository
import com.example.eventconnect.data.EventEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val eventRepository: EventRepository
) : ViewModel() {

    private val _events = MutableLiveData<List<EventEntity>>(emptyList())
    val events: LiveData<List<EventEntity>> = _events

    private val _userLocation = MutableLiveData<Pair<Double, Double>?>(null)
    val userLocation: LiveData<Pair<Double, Double>?> = _userLocation

    fun loadEvents() {
        viewModelScope.launch {
            eventRepository.getAllEvents().collectLatest { eventsList ->
                _events.value = eventsList.map { event ->
                    EventEntity(
                        id = event.id,
                        title = event.title,
                        description = event.description,
                        date = event.date,
                        location = event.location,
                        latitude = event.latitude,
                        longitude = event.longitude,
                        category = event.category,
                        organizer = event.organizer,
                        imageUrl = event.imageUrl,
                        isExternal = event.isExternal
                    )
                }
            }
        }
    }

    fun updateUserLocation(lat: Double, lon: Double) {
        _userLocation.value = Pair(lat, lon)
    }

    fun getNearbyEvents(radius: Double): List<EventEntity> {
        val currentLocation = _userLocation.value ?: return emptyList()
        return _events.value?.filter { event ->
            calculateDistance(
                currentLocation.first,
                currentLocation.second,
                event.latitude,
                event.longitude
            ) <= radius
        } ?: emptyList()
    }

    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371.0 // Rayon de la Terre en km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2).pow(2) +
                Math.cos(Math.toRadians(lat1)) *
                Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2).pow(2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return r * c
    }

    private fun Double.pow(exp: Int): Double = Math.pow(this, exp.toDouble())
}
