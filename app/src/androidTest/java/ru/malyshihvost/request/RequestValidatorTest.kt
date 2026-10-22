package ru.malyshihvost.request

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

// Автотесты проверки формы заявки. Запускаются на эмуляторе или телефоне,
// потому что проверка e-mail использует android.util.Patterns.
@RunWith(AndroidJUnit4::class)
class RequestValidatorTest {

    // Правильно заполненная форма. В тестах меняем в ней одно поле через copy().
    private val validForm = RequestForm(
        type = RequestType.ADOPT,
        walkDate = "",
        name = "Мария",
        phone = "+7 999 123-45-67",
        email = "maria@example.com",
        housing = "Квартира",
        animals = "Нет",
        comment = "",
        consent = true
    )

    private fun errorsFor(form: RequestForm) = RequestValidator.validate(form)

    @Test
    fun validFormHasNoErrors() {
        assertFalse(errorsFor(validForm).hasErrors)
    }

    // ---------- Имя ----------

    @Test
    fun goodNames() {
        for (name in listOf("Мария", "Ян", "Анна-Мария", "Mary Jane", "  Олег  ")) {
            assertNull("Имя «$name» должно подходить", errorsFor(validForm.copy(name = name)).name)
        }
    }

    @Test
    fun badNames() {
        for (name in listOf("", "   ", "А", "12", "--", "Мария1", "Маша!", "-Маша")) {
            assertNotNull("Имя «$name» не должно подходить", errorsFor(validForm.copy(name = name)).name)
        }
    }

    // ---------- Телефон ----------

    @Test
    fun goodPhones() {
        val phones = listOf(
            "+79991234567", "89991234567", "79991234567",
            "+7 (999) 123-45-67", "8 999 123 45 67", " +7 999 123-45-67 "
        )
        for (phone in phones) {
            assertNull("Телефон «$phone» должен подходить", errorsFor(validForm.copy(phone = phone)).phone)
        }
    }

    @Test
    fun badPhones() {
        val phones = listOf(
            "", "9991234567", "+7999123456", "+799912345678",
            "+89991234567", "+7 999 abc 123-45-67", "7+9991234567", "++79991234567",
            "59991234567"
        )
        for (phone in phones) {
            assertNotNull("Телефон «$phone» не должен подходить", errorsFor(validForm.copy(phone = phone)).phone)
        }
    }

    // ---------- E-mail ----------

    @Test
    fun goodEmails() {
        for (email in listOf("maria@example.com", "a@b.ru", "maria.ivanova+dog@mail.ru", " maria@mail.ru ")) {
            assertNull("E-mail «$email» должен подходить", errorsFor(validForm.copy(email = email)).email)
        }
    }

    @Test
    fun badEmails() {
        for (email in listOf("", "maria", "maria@", "@mail.ru", "maria@mail", "maria mail@mail.ru")) {
            assertNotNull("E-mail «$email» не должен подходить", errorsFor(validForm.copy(email = email)).email)
        }
    }

    // ---------- Дата прогулки ----------

    @Test
    fun walkNeedsDate() {
        val walk = validForm.copy(type = RequestType.WALK)
        assertNotNull(errorsFor(walk.copy(walkDate = "")).walkDate)
        assertNull(errorsFor(walk.copy(walkDate = "15.10.2026")).walkDate)
    }

    @Test
    fun adoptDoesNotNeedDate() {
        assertNull(errorsFor(validForm.copy(walkDate = "")).walkDate)
    }

    // ---------- Дом и животные ----------

    @Test
    fun adoptAndFosterNeedHomeInfo() {
        for (type in listOf(RequestType.ADOPT, RequestType.FOSTER)) {
            val errors = errorsFor(validForm.copy(type = type, housing = "", animals = ""))
            assertNotNull(errors.housing)
            assertNotNull(errors.animals)
        }
    }

    @Test
    fun walkDoesNotNeedHomeInfo() {
        val form = validForm.copy(type = RequestType.WALK, walkDate = "15.10.2026", housing = "", animals = "")
        assertFalse(errorsFor(form).hasErrors)
    }

    // ---------- Согласие и комментарий ----------

    @Test
    fun consentIsRequired() {
        assertTrue(errorsFor(validForm.copy(consent = false)).consent)
    }

    @Test
    fun commentIsOptional() {
        assertFalse(errorsFor(validForm.copy(comment = "")).hasErrors)
    }

    // Пустая форма — ошибки во всех обязательных полях.
    @Test
    fun emptyFormShowsAllErrors() {
        val empty = RequestForm(RequestType.ADOPT, "", "", "", "", "", "", "", false)
        val errors = errorsFor(empty)
        val fieldsWithErrors = listOf(errors.name, errors.phone, errors.email, errors.housing, errors.animals)
        assertEquals(5, fieldsWithErrors.count { it != null })
        assertTrue(errors.consent)
    }
}
