package com.example.accord

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DiscoverUiState(
    val questionIndex: Int = 0,
    val answers: Map<Int, Set<String>> = emptyMap(),
    val showingResults: Boolean = false,
    val isLoadingCatalog: Boolean = true,
    val isMatching: Boolean = false,
    val matches: List<Perfume> = emptyList(),
    val errorMessage: String? = null
) {
    val questions get() = SurveyCatalog.questions
    val currentQuestion get() = questions[questionIndex]
    val totalQuestions get() = questions.size
    val progress get() = (questionIndex + 1) / totalQuestions.toFloat()
    val selectedIds get() = answers[questionIndex].orEmpty()
    val canContinue: Boolean
        get() {
            if (showingResults) return false
            val selected = selectedIds
            return if (currentQuestion.type == QuestionType.MULTI && questionIndex == 4) {
                true
            } else {
                selected.isNotEmpty()
            }
        }
}

class DiscoverViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    private var catalog: List<Perfume> = emptyList()

    init {
        loadCatalog()
    }

    fun loadCatalog() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCatalog = true, errorMessage = null) }
            try {
                catalog = PerfumeRepository.fetchAllPerfumes()
                _uiState.update { it.copy(isLoadingCatalog = false) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoadingCatalog = false,
                        errorMessage = e.message ?: "Could not load perfumes"
                    )
                }
            }
        }
    }

    fun toggleOption(optionId: String) {
        val state = _uiState.value
        if (state.showingResults) return
        val question = state.currentQuestion
        val current = state.selectedIds
        val updated = if (question.type == QuestionType.SINGLE) {
            setOf(optionId)
        } else if (optionId in current) {
            current - optionId
        } else {
            current + optionId
        }
        _uiState.update {
            it.copy(answers = it.answers + (it.questionIndex to updated))
        }
    }

    fun continueSurvey() {
        val state = _uiState.value
        if (!state.canContinue) return
        if (state.questionIndex < state.totalQuestions - 1) {
            _uiState.update { it.copy(questionIndex = it.questionIndex + 1) }
        } else {
            showMatches()
        }
    }

    fun restart() {
        _uiState.update {
            it.copy(
                questionIndex = 0,
                answers = emptyMap(),
                showingResults = false,
                isMatching = false,
                matches = emptyList()
            )
        }
    }

    private fun showMatches() {
        viewModelScope.launch {
            _uiState.update { it.copy(showingResults = true, isMatching = true) }
            if (catalog.isEmpty() && _uiState.value.errorMessage == null) {
                try {
                    catalog = PerfumeRepository.fetchAllPerfumes()
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isMatching = false,
                            errorMessage = e.message ?: "Could not load perfumes"
                        )
                    }
                    return@launch
                }
            }
            val matches = rankPerfumes(catalog, _uiState.value.answers)
            _uiState.update { it.copy(isMatching = false, matches = matches) }
        }
    }

    private fun rankPerfumes(
        perfumes: List<Perfume>,
        answers: Map<Int, Set<String>>
    ): List<Perfume> {
        val occasion = answers[0]?.firstOrNull()
        val sillagePref = answers[1]?.firstOrNull()
        val longevityPref = answers[2]?.firstOrNull()
        val liked = answers[3].orEmpty().mapNotNull(ScentFamily::fromId)
        val avoided = answers[4].orEmpty().mapNotNull(ScentFamily::fromId)
        val genderPref = answers[5]?.firstOrNull()

        return perfumes
            .map { it to score(it, occasion, sillagePref, longevityPref, liked, avoided, genderPref) }
            .sortedByDescending { it.second }
            .take(5)
            .map { it.first }
    }

    private fun score(
        perfume: Perfume,
        occasion: String?,
        sillagePref: String?,
        longevityPref: String?,
        liked: List<ScentFamily>,
        avoided: List<ScentFamily>,
        genderPref: String?
    ): Double {
        var score = (perfume.rating ?: 3.5) * 2
        val sillage = perfume.numericSillage()
        val longevity = perfume.numericLongevity()
        val gender = perfume.gender.orEmpty().trim().lowercase()

        liked.forEach { family ->
            if (perfume.matchesFamily(family)) score += 12
        }
        if (liked.isNotEmpty() && liked.none { perfume.matchesFamily(it) }) {
            score -= 16
        }
        avoided.forEach { family ->
            if (perfume.matchesFamily(family)) score -= 22
        }

        when (sillagePref) {
            "subtle" -> score += closeness(sillage, target = 4.0, spread = 2.0)
            "noticeable" -> score += closeness(sillage, target = 6.2, spread = 2.0)
            "entrance" -> score += closeness(sillage, target = 8.0, spread = 2.2)
        }

        when (longevityPref) {
            "few_hours" -> score += closeness(longevity, target = 5.0, spread = 2.2)
            "all_day" -> score += closeness(longevity, target = 8.0, spread = 2.0)
        }

        when (occasion) {
            "everyday" -> if (sillage != null && sillage < 6.5) score += 5
            "evenings" -> if (sillage != null && sillage >= 6.0) score += 5
            "special" -> if ((perfume.rating ?: 0.0) >= 4.1) score += 5
        }

        val isMasculine = gender in setOf("men", "man", "male")
        val isFeminine = gender in setOf("women", "woman", "female")
        val isUnisex = gender.contains("unisex")

        when (genderPref) {
            "unisex" -> score += if (isUnisex) 8 else 1
            "masculine" -> score += when {
                isMasculine -> 10
                isUnisex -> 5
                isFeminine -> -18
                else -> 0
            }
            "feminine" -> score += when {
                isFeminine -> 10
                isUnisex -> 5
                isMasculine -> -18
                else -> 0
            }
        }

        return score
    }

    private fun closeness(value: Double?, target: Double, spread: Double): Double {
        if (value == null) return 2.0
        val distance = kotlin.math.abs(value - target)
        return (8.0 - (distance / spread) * 4.0).coerceIn(0.0, 8.0)
    }
}
