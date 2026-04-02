package edu.nd.pmcburne.hello

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PlacemarkerEntity::class, PlacemarkerTag::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placemarkDao(): PlacemarkerDao
}