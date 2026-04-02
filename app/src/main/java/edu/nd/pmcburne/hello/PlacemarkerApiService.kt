package edu.nd.pmcburne.hello

import retrofit2.http.GET

interface PlacemarkApiService {
    @GET("/placemarks.json")
    suspend fun getPlacemarks(): List<PlacemarkerResponse>
}