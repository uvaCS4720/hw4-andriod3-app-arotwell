package edu.nd.pmcburne.hello

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface PlacemarkerDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlacemarker(placemarker: PlacemarkerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTags(tags: List<PlacemarkerTag>)

    @Transaction
    @Query("SELECT * FROM placemarkers")
    suspend fun getAllWithTags(): List<PlacemarkerWithTags>

    @Transaction
    @Query("SELECT * FROM placemarkers WHERE id = :id")
    suspend fun getByIdWithTags(id: Int): PlacemarkerWithTags?

    @Query("SELECT DISTINCT tag FROM tags ORDER BY tag ASC")
    suspend fun getAllUniqueTags(): List<String>
}