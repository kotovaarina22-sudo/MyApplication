package ru.malyshihvost.catalog

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.malyshihvost.data.AgeGroup
import ru.malyshihvost.data.Dog
import ru.malyshihvost.data.DogRepository
import ru.malyshihvost.data.Gender

// ViewModel каталога: хранит фильтры и готовит список карточек для экрана.
// Переживает поворот экрана, поэтому фильтры не сбрасываются.
class CatalogViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DogRepository(application)

    private val _filter = MutableLiveData(CatalogFilter())
    val filter: LiveData<CatalogFilter> = _filter

    private val _cards = MutableLiveData<List<DogCard>>()
    val cards: LiveData<List<DogCard>> = _cards

    init {
        refresh()
    }

    fun onSearchChanged(text: String) {
        setFilter(currentFilter().copy(query = text.trim()))
    }

    // Повторное нажатие на выбранный чип снимает фильтр.
    fun onGenderClick(gender: Gender) {
        val current = currentFilter()
        val newGender = if (current.gender == gender) null else gender
        setFilter(current.copy(gender = newGender))
    }

    fun onAgeClick(ageGroup: AgeGroup) {
        val current = currentFilter()
        val newAge = if (current.ageGroup == ageGroup) null else ageGroup
        setFilter(current.copy(ageGroup = newAge))
    }

    fun onSmallOnlyClick() {
        val current = currentFilter()
        setFilter(current.copy(smallOnly = !current.smallOnly))
    }

    fun onFavoritesOnlyClick() {
        val current = currentFilter()
        setFilter(current.copy(favoritesOnly = !current.favoritesOnly))
    }

    fun onQuizFinished(answers: QuizAnswers) {
        setFilter(currentFilter().copy(quiz = answers, ageGroup = answers.ageGroup))
    }

    fun onQuizReset() {
        setFilter(currentFilter().copy(quiz = null, ageGroup = null))
    }

    fun onFavoriteClick(dog: Dog) {
        repository.toggleFavorite(dog.id)
        refresh()
    }

    // Заново фильтрует собак и отдаёт экрану новый список.
    fun refresh() {
        val filter = currentFilter()
        val favoriteIds = repository.getFavoriteIds()
        _cards.value = repository.getDogs()
            .filter { dog -> filter.matches(dog, dog.id in favoriteIds) }
            .map { dog -> DogCard(dog, dog.id in favoriteIds) }
    }

    private fun currentFilter(): CatalogFilter = _filter.value ?: CatalogFilter()

    private fun setFilter(newFilter: CatalogFilter) {
        _filter.value = newFilter
        refresh()
    }
}
