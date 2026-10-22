package ru.malyshihvost.request

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.malyshihvost.data.Dog
import ru.malyshihvost.data.DogRepository

// ViewModel формы заявки: хранит собаку, ошибки и отправленную заявку.
class RequestViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DogRepository(application)

    private val _dog = MutableLiveData<Dog>()
    val dog: LiveData<Dog> = _dog

    private val _errors = MutableLiveData(FormErrors())
    val errors: LiveData<FormErrors> = _errors

    // Когда здесь появляется заявка — показываем экран «Спасибо».
    private val _sentForm = MutableLiveData<RequestForm?>(null)
    val sentForm: LiveData<RequestForm?> = _sentForm

    fun load(dogId: Int): Boolean {
        val found = repository.getDog(dogId) ?: return false
        _dog.value = found
        return true
    }

    fun submit(form: RequestForm) {
        val result = RequestValidator.validate(form)
        _errors.value = result
        if (!result.hasErrors) {
            // По заданию заявка никуда не отправляется — просто показываем «Спасибо».
            _sentForm.value = form
        }
    }
}
