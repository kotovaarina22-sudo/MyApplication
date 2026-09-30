package ru.malyshihvost.catalog

import ru.malyshihvost.data.AgeGroup
import ru.malyshihvost.data.Dog
import ru.malyshihvost.data.Gender

// Ответы из анкеты «Подобрать собаку».
data class QuizAnswers(
    val hasKids: Boolean,
    val hasCats: Boolean,
    val littleTime: Boolean,   // мало времени на прогулки
    val ageGroup: AgeGroup?    // null — «неважно»
) {
    fun suits(dog: Dog): Boolean {
        if (hasKids && !dog.goodWithKids) return false
        if (hasCats && !dog.goodWithCats) return false
        if (littleTime && dog.isActive) return false
        return true
    }
}

// Все фильтры каталога в одном объекте. null означает «фильтр выключен».
data class CatalogFilter(
    val query: String = "",
    val gender: Gender? = null,
    val ageGroup: AgeGroup? = null,
    val smallOnly: Boolean = false,      // только до 5 кг
    val favoritesOnly: Boolean = false,
    val quiz: QuizAnswers? = null
) {
    fun matches(dog: Dog, isFavorite: Boolean): Boolean {
        if (query.isNotEmpty() && !dog.name.contains(query, ignoreCase = true)) return false
        if (gender != null && dog.gender != gender) return false
        if (ageGroup != null && dog.ageGroup != ageGroup) return false
        if (smallOnly && dog.weightKg > 5.0) return false
        if (favoritesOnly && !isFavorite) return false
        if (quiz != null && !quiz.suits(dog)) return false
        return true
    }
}
