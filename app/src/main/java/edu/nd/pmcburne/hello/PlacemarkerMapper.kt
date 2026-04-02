package edu.nd.pmcburne.hello

fun PlacemarkerResponse.toEntities(): Pair<PlacemarkerEntity, List<PlacemarkerTag>> {
    val placemarker = PlacemarkerEntity(
        id = id,
        name = name,
        description = description,
        latitude = visual_center.latitude,
        longitude = visual_center.longitude
    )
    val tags = tag_list.map { tag ->
        PlacemarkerTag(placemarkerID = id, tag = tag)
    }
    return Pair(placemarker, tags)
}