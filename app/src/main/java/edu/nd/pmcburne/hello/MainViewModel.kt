package edu.nd.pmcburne.hello

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repo: PlacemarkerRepo



    private val _allPlacemarks = MutableStateFlow<List<PlacemarkerWithTags>>(emptyList())
    private val _allTags       = MutableStateFlow<List<String>>(emptyList())
    private val _selectedTag   = MutableStateFlow("core")   // default per spec
    private val _isLoading     = MutableStateFlow(true)
    private val _errorMessage  = MutableStateFlow<String?>(null)


    val allTags:     StateFlow<List<String>> = _allTags.asStateFlow()
    val selectedTag: StateFlow<String>       = _selectedTag.asStateFlow()
    val isLoading:   StateFlow<Boolean>      = _isLoading.asStateFlow()
    val errorMessage: StateFlow<String?>     = _errorMessage.asStateFlow()


    val filteredPlacemarks: StateFlow<List<PlacemarkerWithTags>> =
        _allPlacemarks.combine(_selectedTag) { placemarks, tag ->
            placemarks.filter { pwt -> pwt.tags.any { it.tag == tag } }
        }.stateIn(
            scope          = viewModelScope,
            started        = SharingStarted.WhileSubscribed(5_000),
            initialValue   = emptyList()
        )


    init {
        val db = AppDatabase.getDatabase(application)
        repo = PlacemarkerRepo(db.placemarkDao())
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repo.syncFromApi()
            } catch (e: Exception) {
                _errorMessage.value = "Could not refresh data: ${e.localizedMessage}"
            }
            _allPlacemarks.value = repo.getAllWithTags()
            _allTags.value       = repo.getAllUniqueTags()
            _isLoading.value     = false
        }
    }


    fun selectTag(tag: String) {
        _selectedTag.value = tag
    }
}