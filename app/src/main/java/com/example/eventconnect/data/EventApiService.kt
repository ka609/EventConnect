package com.example.eventconnect.data

import retrofit2.http.GET

interface EventApiService {
    @GET("events")
    suspend fun getEvents(): List<ApiEvent>
}
