package ru.malyshihvost.data

import android.content.Context
import ru.malyshihvost.R

// Репозиторий — единственное место, откуда экраны берут данные.
// Список собак хранится прямо в коде, а избранное — в SharedPreferences,
// чтобы оно не пропадало после закрытия приложения.
class DogRepository(context: Context) {

    private val prefs = context.getSharedPreferences("favorites", Context.MODE_PRIVATE)

    fun getDogs(): List<Dog> = dogs

    fun getDog(id: Int): Dog? = dogs.find { it.id == id }

    fun getFavoriteIds(): Set<Int> {
        val saved = prefs.getStringSet(KEY_FAVORITES, emptySet()) ?: emptySet()
        return saved.map { it.toInt() }.toSet()
    }

    fun toggleFavorite(id: Int) {
        val favorites = getFavoriteIds().toMutableSet()
        if (id in favorites) favorites.remove(id) else favorites.add(id)
        prefs.edit()
            .putStringSet(KEY_FAVORITES, favorites.map { it.toString() }.toSet())
            .apply()
    }

    companion object {
        private const val KEY_FAVORITES = "favorite_ids"

        private val dogs = listOf(
            Dog(
                id = 1,
                name = "Булочка",
                ageMonths = 24,
                gender = Gender.FEMALE,
                weightKg = 2.1,
                breed = "Метис чихуахуа",
                color = "Рыжий",
                story = "Булочку нашли зимой у дверей пекарни — отсюда и имя. Она грелась у вентиляции и очень боялась людей, но быстро поняла, что в приюте её любят.",
                character = "Ласковая и спокойная. Обожает спать на мягкой подушке и сидеть на ручках.",
                arrivalDate = "12.01.2026",
                photos = listOf(R.drawable.dog1_1, R.drawable.dog1_2, R.drawable.dog1_3),
                goodWithKids = true,
                goodWithCats = false,
                isActive = false,
                isVaccinated = true,
                isSterilized = true,
                isLitterTrained = true
            ),
            Dog(
                id = 2,
                name = "Уголёк",
                ageMonths = 36,
                gender = Gender.MALE,
                weightKg = 3.5,
                breed = "Той-пудель",
                color = "Чёрный",
                story = "Уголёк жил у пожилой хозяйки, которая переехала к детям и не смогла взять его с собой. Он воспитанный и знает много команд.",
                character = "Умный и игривый. Любит учить новые трюки и гулять в парке.",
                arrivalDate = "03.04.2026",
                photos = listOf(R.drawable.dog2_1, R.drawable.dog2_2, R.drawable.dog2_3),
                goodWithKids = true,
                goodWithCats = true,
                isActive = true,
                isVaccinated = true,
                isSterilized = true,
                isLitterTrained = false
            ),
            Dog(
                id = 3,
                name = "Пончик",
                ageMonths = 7,
                gender = Gender.MALE,
                weightKg = 1.8,
                breed = "Померанский шпиц",
                color = "Кремовый",
                story = "Пончика отдали в приют прежние хозяева — у ребёнка оказалась аллергия. Он совсем малыш и ждёт семью, с которой вырастет.",
                character = "Весёлый пушистый непоседа. Со всеми дружит и любит играть с мячиком.",
                arrivalDate = "20.08.2026",
                photos = listOf(R.drawable.dog3_1, R.drawable.dog3_2, R.drawable.dog3_3),
                goodWithKids = true,
                goodWithCats = true,
                isActive = true,
                isVaccinated = true,
                isSterilized = false,
                isLitterTrained = true
            ),
            Dog(
                id = 4,
                name = "Ириска",
                ageMonths = 60,
                gender = Gender.FEMALE,
                weightKg = 2.7,
                breed = "Йоркширский терьер",
                color = "Золотисто-чёрный",
                story = "Ириску нашли на дачном участке осенью. Хозяев так и не нашли. Сейчас она полностью здорова и готова к новому дому.",
                character = "Нежная, немного стеснительная. Пугается громких звуков, поэтому ей подойдёт тихий дом.",
                arrivalDate = "15.10.2025",
                photos = listOf(R.drawable.dog4_1, R.drawable.dog4_2, R.drawable.dog4_3),
                goodWithKids = false,
                goodWithCats = true,
                isActive = false,
                isVaccinated = true,
                isSterilized = true,
                isLitterTrained = true
            ),
            Dog(
                id = 5,
                name = "Жужа",
                ageMonths = 108,
                gender = Gender.FEMALE,
                weightKg = 7.5,
                breed = "Мопс",
                color = "Палевый",
                story = "Жужа всю жизнь прожила в семье, но хозяйка тяжело заболела. Жужа очень скучает по дому и мечтает о диване и заботливых руках.",
                character = "Спокойная, добродушная, немного ленивая. Любит вкусно поесть и похрапеть рядом с человеком.",
                arrivalDate = "01.06.2026",
                photos = listOf(R.drawable.dog5_1, R.drawable.dog5_2, R.drawable.dog5_3),
                goodWithKids = true,
                goodWithCats = true,
                isActive = false,
                isVaccinated = true,
                isSterilized = true,
                isLitterTrained = false
            ),
            Dog(
                id = 6,
                name = "Снежок",
                ageMonths = 4,
                gender = Gender.MALE,
                weightKg = 1.5,
                breed = "Метис мальтезе",
                color = "Белый",
                story = "Снежка вместе с братьями подбросили в коробке к воротам приюта. Братья уже нашли дом, а он пока ждёт своих людей.",
                character = "Любопытный и смелый малыш. Всё время хочет играть и исследовать.",
                arrivalDate = "05.09.2026",
                photos = listOf(R.drawable.dog6_1, R.drawable.dog6_2, R.drawable.dog6_3),
                goodWithKids = true,
                goodWithCats = false,
                isActive = true,
                isVaccinated = false,
                isSterilized = false,
                isLitterTrained = true
            ),
            Dog(
                id = 7,
                name = "Бантик",
                ageMonths = 26,
                gender = Gender.MALE,
                weightKg = 3.2,
                breed = "Папийон",
                color = "Бело-рыжий",
                story = "Бантика забрали от недобросовестного заводчика. Он быстро пришёл в себя и теперь самый энергичный житель приюта.",
                character = "Очень активный и сообразительный. Обожает бегать и играть в догонялки.",
                arrivalDate = "18.02.2026",
                photos = listOf(R.drawable.dog7_1, R.drawable.dog7_2, R.drawable.dog7_3),
                goodWithKids = true,
                goodWithCats = false,
                isActive = true,
                isVaccinated = true,
                isSterilized = true,
                isLitterTrained = false
            ),
            Dog(
                id = 8,
                name = "Тося",
                ageMonths = 100,
                gender = Gender.FEMALE,
                weightKg = 6.4,
                breed = "Ши-тцу",
                color = "Бело-палевый",
                story = "Тосю привезли волонтёры из деревни, где она жила на улице. Теперь у неё снова блестящая шёрстка и отличное настроение.",
                character = "Мудрая и уравновешенная. Любит неспешные прогулки и когда её расчёсывают.",
                arrivalDate = "27.07.2026",
                photos = listOf(R.drawable.dog8_1, R.drawable.dog8_2, R.drawable.dog8_3),
                goodWithKids = true,
                goodWithCats = true,
                isActive = false,
                isVaccinated = true,
                isSterilized = true,
                isLitterTrained = false
            )
        )
    }
}
