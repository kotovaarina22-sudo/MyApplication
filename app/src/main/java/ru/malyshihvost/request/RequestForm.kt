package ru.malyshihvost.request

import android.util.Patterns
import ru.malyshihvost.R

// Тип заявки — что человек хочет сделать с собакой.
enum class RequestType(val title: String) {
    ADOPT("Забрать домой"),
    FOSTER("Взять на передержку"),
    WALK("Прийти погулять")
}

// Всё, что человек ввёл в форму.
data class RequestForm(
    val type: RequestType,
    val walkDate: String,
    val name: String,
    val phone: String,
    val email: String,
    val housing: String,
    val animals: String,
    val comment: String,
    val consent: Boolean
)

// Ошибки формы: для каждого поля — id текста ошибки или null, если ошибки нет.
data class FormErrors(
    val walkDate: Int? = null,
    val name: Int? = null,
    val phone: Int? = null,
    val email: Int? = null,
    val housing: Int? = null,
    val animals: Int? = null,
    val consent: Boolean = false   // true — галочка согласия не поставлена
) {
    val hasErrors: Boolean
        get() = walkDate != null || name != null || phone != null || email != null ||
                housing != null || animals != null || consent
}

// Проверка формы. Комментарий необязателен, поэтому его не проверяем.
// Для прогулки не проверяем дом и животных — этих полей на экране нет.
object RequestValidator {

    fun validate(form: RequestForm): FormErrors {
        return FormErrors(
            walkDate = if (form.type == RequestType.WALK && form.walkDate.isBlank()) R.string.error_date else null,
            name = if (!isNameValid(form.name)) R.string.error_name else null,
            phone = if (!isPhoneValid(form.phone)) R.string.error_phone else null,
            email = if (!Patterns.EMAIL_ADDRESS.matcher(form.email.trim()).matches()) R.string.error_email else null,
            housing = if (needsHomeInfo(form) && form.housing.isBlank()) R.string.error_choose else null,
            animals = if (needsHomeInfo(form) && form.animals.isBlank()) R.string.error_choose else null,
            consent = !form.consent
        )
    }

    // Вопросы о доме нужны, только если собака будет жить у человека.
    private fun needsHomeInfo(form: RequestForm): Boolean = form.type != RequestType.WALK

    // Имя: от 2 до 50 символов, только буквы, пробел и дефис, начинается с буквы.
    private fun isNameValid(name: String): Boolean {
        val trimmed = name.trim()
        return trimmed.length in 2..50 &&
                trimmed.first().isLetter() &&
                trimmed.all { it.isLetter() || it == ' ' || it == '-' }
    }

    // Российский номер: 11 цифр. С «+» — только +7, без «+» — с 7 или 8.
    // Кроме цифр можно писать пробелы, скобки и дефисы: +7 (999) 123-45-67.
    private fun isPhoneValid(phone: String): Boolean {
        val trimmed = phone.trim()
        if (!trimmed.all { it.isDigit() || it in "+()- " }) return false  // буквы и прочее нельзя
        if (trimmed.lastIndexOf('+') > 0) return false                     // «+» только в начале и один
        val digits = trimmed.filter { it.isDigit() }
        if (digits.length != 11) return false
        return if (trimmed.startsWith("+")) {
            digits.startsWith("7")
        } else {
            digits.startsWith("7") || digits.startsWith("8")
        }
    }
}
