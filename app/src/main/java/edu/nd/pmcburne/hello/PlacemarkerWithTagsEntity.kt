package edu.nd.pmcburne.hello

import androidx.room.Embedded
import androidx.room.Relation


data class PlacemarkerWithTags(
    @Embedded val placemarker: PlacemarkerEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "placemarkerID"
    )
    val tags: List<PlacemarkerTag>
)

