package edu.nd.pmcburne.hello

class PlacemarkerRepo(private val dao: PlacemarkerDao) {


    suspend fun syncFromApi() {
        val responses = RetrofitClient.apiService.getPlacemarks()
        for (response in responses) {
            val (entity, tags) = response.toEntities()
            dao.insertPlacemarker(entity)   // REPLACE handles duplicates
            dao.insertTags(tags)            // REPLACE handles duplicates
        }
    }
    suspend fun getAllWithTags(): List<PlacemarkerWithTags> =
        dao.getAllWithTags()

    suspend fun getAllUniqueTags(): List<String> =
        dao.getAllUniqueTags()
}