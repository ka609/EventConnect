package com.example.eventconnect.data

import com.example.eventconnect.model.Event
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository(
    private val eventDao: EventDao,
    private val apiService: EventApiService
) {

    fun getAllEvents(): Flow<List<Event>> {
        return eventDao.getAllEvents().map { entities ->
            entities.map { it.toEvent() }
        }
    }

    fun getEventsByCategory(category: String): Flow<List<Event>> {
        return eventDao.getEventsByCategory(category).map { entities ->
            entities.map { it.toEvent() }
        }
    }

    suspend fun syncWithApi() {
        try {
            val apiEvents = apiService.getEvents()
            val eventEntities = apiEvents.map { it.toEventEntity() }
            eventEntities.forEach { eventDao.insertEvent(it) }
        } catch (e: Exception) {
            // TODO: Gérer l'erreur (logs, retry, etc.)
        }
    }

    suspend fun createEvent(event: Event) {
        val entity = event.toEventEntity()
        eventDao.insertEvent(entity)
    }

    // Extensions : conversion entre EventEntity et Event
    private fun EventEntity.toEvent(): Event {
        return Event(
            id = id,
            title = title,
            description = description,
            date = date,
            location = location,
            latitude = latitude,
            longitude = longitude,
            category = category,
            organizer = organizer,
            imageUrl = imageUrl,
            isExternal = isExternal
        )
    }

    private fun Event.toEventEntity(): EventEntity {
        return EventEntity(
            id = id,
            title = title,
            description = description,
            date = date,
            location = location,
            latitude = latitude,
            longitude = longitude,
            category = category,
            organizer = organizer,
            imageUrl = imageUrl,
            isExternal = isExternal
        )
    }
}
