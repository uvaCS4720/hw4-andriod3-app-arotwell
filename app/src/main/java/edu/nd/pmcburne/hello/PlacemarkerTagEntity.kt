package edu.nd.pmcburne.hello

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "placemarker_tags",
    foreignKeys = [ForeignKey(
        entity = PlacemarkerEntity::class,
        parentColumns = ["id"],
        childColumns = ["placemarkerID"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("placemarkerID")]
)
data class PlacemarkerTag(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val placemarkerID: Int,
    val tag: String
)