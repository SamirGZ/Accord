package com.example.accord

enum class QuestionType { SINGLE, MULTI }

data class SurveyOption(
    val id: String,
    val title: String,
    val subtitle: String
)

data class SurveyQuestion(
    val prompt: String,
    val type: QuestionType,
    val options: List<SurveyOption>
)

enum class ScentFamily(val id: String, val label: String, val keywords: List<String>) {
    CITRUS_FRESH(
        "citrus_fresh",
        "Citrus & Fresh",
        listOf(
            "citrus", "bergamot", "lemon", "lime", "orange", "grapefruit", "mandarin",
            "fresh", "aldehyde", "neroli", "petitgrain"
        )
    ),
    FLORAL(
        "floral",
        "Floral",
        listOf(
            "floral", "flower", "rose", "jasmine", "lily", "iris", "violet", "magnolia",
            "tuberose", "ylang", "peony", "gardenia", "orchid", "blossom"
        )
    ),
    WOODY_EARTHY(
        "woody_earthy",
        "Woody & Earthy",
        listOf(
            "wood", "woody", "cedar", "sandalwood", "vetiver", "oud", "agarwood",
            "patchouli", "oak", "earth", "moss", "oakmoss", "guaiac"
        )
    ),
    ORIENTAL_SPICY(
        "oriental_spicy",
        "Oriental & Spicy",
        listOf(
            "oriental", "amber", "incense", "spice", "spicy", "cinnamon", "clove",
            "cardamom", "saffron", "pepper", "oud", "resin", "incense"
        )
    ),
    GOURMAND_SWEET(
        "gourmand_sweet",
        "Gourmand & Sweet",
        listOf(
            "gourmand", "vanilla", "chocolate", "caramel", "honey", "tonka", "praline",
            "coffee", "sweet", "sugar", "cocoa", "almond"
        )
    ),
    AQUATIC_GREEN(
        "aquatic_green",
        "Aquatic & Green",
        listOf(
            "aquatic", "marine", "calone", "green", "galbanum", "grass", "leaf",
            "cucumber", "tea", "mint", "herbal", "ozonic"
        )
    );

    companion object {
        fun fromId(id: String): ScentFamily? = entries.find { it.id == id }
    }
}

object SurveyCatalog {
    val familyOptions = ScentFamily.entries.map {
        SurveyOption(id = it.id, title = it.label, subtitle = familySubtitle(it))
    }

    val questions = listOf(
        SurveyQuestion(
            prompt = "When will you mostly wear this?",
            type = QuestionType.SINGLE,
            options = listOf(
                SurveyOption("everyday", "Everyday", "Easy, versatile, for regular wear"),
                SurveyOption("evenings", "Evenings out", "Richer presence after dark"),
                SurveyOption("special", "Special occasions", "Memorable and distinctive")
            )
        ),
        SurveyQuestion(
            prompt = "How noticeable do you want it to be?",
            type = QuestionType.SINGLE,
            options = listOf(
                SurveyOption("subtle", "Subtle and close to skin", "A private scent, just for you"),
                SurveyOption("noticeable", "Noticeable", "Present without overwhelming"),
                SurveyOption("entrance", "Makes an entrance", "A trail people remember")
            )
        ),
        SurveyQuestion(
            prompt = "How long should it last?",
            type = QuestionType.SINGLE,
            options = listOf(
                SurveyOption("few_hours", "A few hours is fine", "Light wear, easy to refresh"),
                SurveyOption("all_day", "All day", "Stays with you from morning on")
            )
        ),
        SurveyQuestion(
            prompt = "Which of these appeal to you?",
            type = QuestionType.MULTI,
            options = familyOptions
        ),
        SurveyQuestion(
            prompt = "Anything you want to avoid?",
            type = QuestionType.MULTI,
            options = familyOptions
        ),
        SurveyQuestion(
            prompt = "Any preference?",
            type = QuestionType.SINGLE,
            options = listOf(
                SurveyOption("unisex", "Unisex", "Balanced, works across styles"),
                SurveyOption("masculine", "Leaning masculine", "Woods, spice, and depth"),
                SurveyOption("feminine", "Leaning feminine", "Florals, softness, brightness"),
                SurveyOption("none", "No preference", "Match me on scent, not gender")
            )
        )
    )

    private fun familySubtitle(family: ScentFamily): String = when (family) {
        ScentFamily.CITRUS_FRESH -> "Bright, zesty, and clean"
        ScentFamily.FLORAL -> "Petals, bouquets, and softness"
        ScentFamily.WOODY_EARTHY -> "Forest, resins, and soil"
        ScentFamily.ORIENTAL_SPICY -> "Warm spice, amber, and incense"
        ScentFamily.GOURMAND_SWEET -> "Vanilla, dessert, and warmth"
        ScentFamily.AQUATIC_GREEN -> "Water, leaves, and air"
    }
}

fun Perfume.scentText(): String = buildString {
    notes_top.forEach { append(it).append(' ') }
    notes_middle.forEach { append(it).append(' ') }
    notes_base.forEach { append(it).append(' ') }
    description?.let { append(it) }
}.lowercase()

fun Perfume.matchesFamily(family: ScentFamily): Boolean {
    val haystack = scentText()
    return family.keywords.any { haystack.contains(it) }
}

fun Perfume.numericLongevity(): Double? = longevity?.toDoubleOrNull()
fun Perfume.numericSillage(): Double? = sillage?.toDoubleOrNull()
