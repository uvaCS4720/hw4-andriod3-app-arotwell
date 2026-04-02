package edu.nd.pmcburne.hello

import retrofit2.http.GET
import retrofit2.http.Path

interface PlacemarkApiService {
    @GET("/placemarks.json")
    suspend fun getPlacemarks(): List<PlacemarkerResponse>
}