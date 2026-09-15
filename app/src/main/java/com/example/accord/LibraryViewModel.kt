package com.example.accord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class ScentFilter(val label: String) {
    ALL("All"),
    FLORAL("Floral"),
    WOODY("Woody"),
    FRESH("Fresh"),
    SPICY("Spicy")
}

data class LibraryUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val perfumes: List<Perfume> = emptyList(),
    val selectedFilter: ScentFilter = ScentFilter.ALL
) {
    val visiblePerfumes: List<Perfume>
        get() = if (selectedFilter == ScentFilter.ALL) {
            perfumes
        } else {
            perfumes.filter { it.matchesFilter(selectedFilter) }
        }
}

class LibraryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        loadPerfumes()
    }

    fun loadPerfumes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val perfumes = fetchAllPerfumes()
                _uiState.update {
                    it.copy(isLoading = false, perfumes = perfumes, errorMessage = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Could not load perfumes"
                    )
                }
            }
        }
    }

    fun selectFilter(filter: ScentFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    private suspend fun fetchAllPerfumes(): List<Perfume> {
        val all = mutableListOf<Perfume>()
        var offset = 0
        val pageSize = 50
        var total = Int.MAX_VALUE

        while (offset < total) {
            val page = RetrofitInstance.api.getPerfumes(limit = pageSize, offset = offset)
            total = page.total
            if (page.perfumes.isEmpty()) break
            all += page.perfumes
            offset += page.perfumes.size
        }
        return all
    }
}

private val filterKeywords = mapOf(
    ScentFilter.FLORAL to listOf(
        "floral", "flower", "rose", "jasmine", "lily", "iris", "violet", "magnolia",
        "tuberose", "ylang", "neroli", "orange blossom", "peony", "gardenia", "orchid",
        "geranium", "heliotrope", "mimosa", "lilac", "freesia", "hyacinth", "carnation",
        "osmanthus", "champaca", "blossom"
    ),
    ScentFilter.WOODY to listOf(
        "wood", "woody", "cedar", "sandalwood", "vetiver", "oud", "agarwood", "guaiac",
        "cypress", "patchouli", "oak", "pine", "cashmere", "iso e", "oakmoss"
    ),
    ScentFilter.FRESH to listOf(
        "fresh", "citrus", "bergamot", "lemon", "lime", "orange", "grapefruit", "mint",
        "aquatic", "marine", "calone", "green", "aldehyde", "ozonic", "water", "cucumber",
        "tea", "herbal"
    ),
    ScentFilter.SPICY to listOf(
        "spicy", "spice", "pepper", "cinnamon", "clove", "nutmeg", "cardamom", "saffron",
        "ginger", "cumin", "thyme", "chili", "pimento"
    )
)

fun Perfume.matchesFilter(filter: ScentFilter): Boolean {
    if (filter == ScentFilter.ALL) return true
    val keywords = filterKeywords[filter] ?: return false
    val haystack = buildString {
        notes_top.forEach { append(it).append(' ') }
        notes_middle.forEach { append(it).append(' ') }
        notes_base.forEach { append(it).append(' ') }
        description?.let { append(it) }
    }.lowercase()
    return keywords.any { haystack.contains(it) }
}
