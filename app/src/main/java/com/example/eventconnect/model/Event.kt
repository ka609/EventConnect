package com.example.eventconnect.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: Date = Date(),
    val location: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val category: Category = Category.OTHER,
    val organizer: String = "",
    val imageUrl: String = "",
    val isExternal: Boolean = false // Pour différencier les événements locaux de ceux de l’API
) : Parcelable
