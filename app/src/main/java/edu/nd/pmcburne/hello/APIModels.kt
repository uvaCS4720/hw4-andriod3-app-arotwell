package edu.nd.pmcburne.hello

data class PlacemarkerResponse(
    val id: Int,
    val name: String,
    val description: String,
    val tag_list: List<String>,
    val visual_center: VisualCenter
)

data class VisualCenter(
    val latitude: Double,
    val longitude: Double
)