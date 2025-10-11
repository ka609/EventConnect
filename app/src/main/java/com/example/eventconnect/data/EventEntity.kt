package com.example.eventconnect.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.eventconnect.model.Category
import java.util.Date

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val date: Date,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val category: Category,
    val organizer: String,
    val imageUrl: String,
    val isExternal: Boolean = false
)