package com.example.eventconnect.data

import com.example.eventconnect.model.Category
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ApiEvent(
    val id: String,
    val title: String,
    val description: String,
    val date: String, // String envoyé par l'API
    val location: String,
    val lat: Double,
    val lon: Double,
    val category: String,
    val organizer: String,
    val imageUrl: String
) {
    fun toEventEntity(): EventEntity {
        val parsedDate = try {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date) ?: Date()
        } catch (e: Exception) {
            Date()
        }

        return EventEntity(
            id = id,
            title = title,
            description = description,
            date = parsedDate,
            location = location,
            latitude = lat,
            longitude = lon,
            category = Category.valueOf(category.uppercase()),
            organizer = organizer,
            imageUrl = imageUrl,
            isExternal = true
        )
    }
}
