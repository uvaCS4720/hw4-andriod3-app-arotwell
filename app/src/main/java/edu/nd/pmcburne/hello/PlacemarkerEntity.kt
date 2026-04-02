package edu.nd.pmcburne.hello

import androidx.room.Entity
import androidx.room.PrimaryKey



@Entity(tableName = "placemarkers")
data class PlacemarkerEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)