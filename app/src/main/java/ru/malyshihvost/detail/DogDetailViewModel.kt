package ru.malyshihvost.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.malyshihvost.data.Dog
import ru.malyshihvost.data.DogRepository

// ViewModel страницы собаки: хранит выбранную собаку и отметку «избранное».
class DogDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DogRepository(application)

    private val _dog = MutableLiveData<Dog>()
    val dog: LiveData<Dog> = _dog

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    // Загружает собаку по id. Возвращает false, если такой собаки нет.
    fun load(dogId: Int): Boolean {
        val found = repository.getDog(dogId) ?: return false
        _dog.value = found
        _isFavorite.value = found.id in repository.getFavoriteIds()
        return true
    }

    fun onFavoriteClick() {
        val current = _dog.value ?: return
        repository.toggleFavorite(current.id)
        _isFavorite.value = current.id in repository.getFavoriteIds()
    }
}
