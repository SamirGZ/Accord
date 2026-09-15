package com.example.accord

data class Perfume(
    val id: String,
    val name: String,
    val brand: String?,
    val release_year: Int?,
    val gender: String?,
    val notes_top: List<String>,
    val notes_middle: List<String>,
    val notes_base: List<String>,
    val rating: Double?,
    val votes: Int?,
    val description: String?,
    val longevity: String?,
    val sillage: String?,
    val image_url: String?,
    val perfume_url: String?
)

// Wrapper shape returned by GET /perfumes
data class PerfumeListResponse(
    val total: Int,
    val limit: Int,
    val offset: Int,
    val perfumes: List<Perfume>
)