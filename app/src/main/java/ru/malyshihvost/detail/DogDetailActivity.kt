package ru.malyshihvost.detail

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayoutMediator
import ru.malyshihvost.R
import ru.malyshihvost.data.Dog
import ru.malyshihvost.data.Shelter
import ru.malyshihvost.databinding.ActivityDogDetailBinding
import ru.malyshihvost.request.RequestActivity

// Экран 2 — подробная страница собаки.
class DogDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDogDetailBinding
    private val viewModel: DogDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Сверху тёмное фото, поэтому значки статус-бара здесь светлые (dark-стиль).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Фото заходит под статус-бар, а кнопки сверху и снизу — нет.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.topButtons.updatePadding(top = bars.top)
            binding.bottomBar.updatePadding(bottom = bars.bottom)
            insets
        }

        // Каталог передал нам id собаки через Intent.
        val dogId = intent.getIntExtra(EXTRA_DOG_ID, -1)
        if (!viewModel.load(dogId)) {
            finish()
            return
        }

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener { finish() }
        binding.favoriteButton.setOnClickListener { viewModel.onFavoriteClick() }
        binding.callButton.setOnClickListener { callShelter() }
        binding.telegramButton.setOnClickListener { openTelegram() }
        binding.applyButton.setOnClickListener { openRequestForm() }
    }

    // Открываем форму заявки и передаём ей id этой собаки.
    private fun openRequestForm() {
        val dog = viewModel.dog.value ?: return
        val intent = Intent(this, RequestActivity::class.java)
        intent.putExtra(RequestActivity.EXTRA_DOG_ID, dog.id)
        startActivity(intent)
    }

    private fun observeViewModel() {
        viewModel.dog.observe(this) { dog -> showDog(dog) }

        viewModel.isFavorite.observe(this) { isFavorite ->
            binding.favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )
        }
    }

    private fun showDog(dog: Dog) {
        // Галерея: ViewPager2 листает фото, TabLayout рисует точки под ними.
        binding.photoPager.adapter = PhotoAdapter(dog.photos)
        TabLayoutMediator(binding.photoDots, binding.photoPager) { _, _ -> }.attach()

        binding.name.text = dog.name
        binding.breed.text = dog.breed
        binding.ageValue.text = dog.ageText
        binding.genderValue.text = dog.gender.title
        binding.weightValue.text = dog.weightText
        binding.color.text = getString(R.string.detail_color, dog.color)
        binding.arrival.text = getString(R.string.detail_arrival, dog.arrivalDate)
        binding.story.text = dog.story
        binding.character.text = dog.character

        showBadges(dog)
    }

    // Значки: зелёный ✓ — да, красный ✗ — нет.
    private fun showBadges(dog: Dog) {
        binding.badges.removeAllViews()
        addBadge(getString(R.string.badge_kids), dog.goodWithKids)
        addBadge(getString(R.string.badge_cats), dog.goodWithCats)
        addBadge(getString(R.string.badge_vaccinated), dog.isVaccinated)
        addBadge(getString(R.string.badge_sterilized), dog.isSterilized)
        addBadge(getString(R.string.badge_litter), dog.isLitterTrained)
    }

    private fun addBadge(text: String, isYes: Boolean) {
        val chip = Chip(this)
        chip.text = if (isYes) "✓ $text" else "✗ $text"
        chip.isClickable = false
        chip.setEnsureMinTouchTargetSize(false)  // значок не кнопка — без лишних отступов
        chip.chipStrokeWidth = 0f
        val background = if (isYes) R.color.green_light else R.color.red_light
        val textColor = if (isYes) R.color.green_dark else R.color.red_dark
        chip.chipBackgroundColor = ColorStateList.valueOf(getColor(background))
        chip.setTextColor(getColor(textColor))
        binding.badges.addView(chip)
    }

    // ACTION_DIAL открывает «звонилку» с номером, звонит сам пользователь.
    private fun callShelter() {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Shelter.PHONE}"))
        openSafely(intent)
    }

    // Ссылка t.me откроется в Telegram, а если его нет — в браузере.
    private fun openTelegram() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Shelter.TELEGRAM_URL))
        openSafely(intent)
    }

    // Если на телефоне нет подходящего приложения, показываем сообщение вместо падения.
    private fun openSafely(intent: Intent) {
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, R.string.detail_no_app, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        // Ключ, под которым каталог кладёт id собаки в Intent.
        const val EXTRA_DOG_ID = "dog_id"
    }
}
