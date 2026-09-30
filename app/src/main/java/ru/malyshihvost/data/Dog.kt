package ru.malyshihvost.data

// Пол собаки. У каждого значения есть подпись для экрана.
enum class Gender(val title: String) {
    MALE("Мальчик"),
    FEMALE("Девочка")
}

// Возрастная группа — нужна для фильтров в каталоге.
enum class AgeGroup {
    PUPPY,  // до 1 года
    ADULT,  // от 1 до 7 лет
    SENIOR  // 8 лет и старше
}

// Модель данных: вся информация об одной собаке.
data class Dog(
    val id: Int,
    val name: String,
    val ageMonths: Int,
    val gender: Gender,
    val weightKg: Double,
    val breed: String,
    val color: String,
    val story: String,
    val character: String,
    val arrivalDate: String,
    val photos: List<Int>,        // id картинок из res/drawable-nodpi
    val goodWithKids: Boolean,
    val goodWithCats: Boolean,
    val isActive: Boolean,        // нужны долгие активные прогулки
    val isVaccinated: Boolean,
    val isSterilized: Boolean,
    val isLitterTrained: Boolean  // приучена к пелёнке/лотку
) {

    // Возрастная группа считается из возраста в месяцах.
    val ageGroup: AgeGroup
        get() = when {
            ageMonths < 12 -> AgeGroup.PUPPY
            ageMonths < 96 -> AgeGroup.ADULT
            else -> AgeGroup.SENIOR
        }

    // Возраст текстом: «7 месяцев», «2 года», «5 лет».
    val ageText: String
        get() = if (ageMonths < 12) {
            "$ageMonths ${plural(ageMonths, "месяц", "месяца", "месяцев")}"
        } else {
            val years = ageMonths / 12
            "$years ${plural(years, "год", "года", "лет")}"
        }

    // Вес текстом с запятой: «2,1 кг».
    val weightText: String
        get() = weightKg.toString().replace('.', ',') + " кг"

    // Главное фото — первое в списке.
    val mainPhoto: Int
        get() = photos.first()
}

// Выбирает правильное окончание слова для числа: 1 год, 2 года, 5 лет.
private fun plural(number: Int, one: String, few: String, many: String): String {
    val lastTwo = number % 100
    val last = number % 10
    return when {
        lastTwo in 11..14 -> many
        last == 1 -> one
        last in 2..4 -> few
        else -> many
    }
}
