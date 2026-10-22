package ru.malyshihvost.catalog

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.malyshihvost.R
import ru.malyshihvost.data.AgeGroup
import ru.malyshihvost.data.Gender
import ru.malyshihvost.databinding.ActivityCatalogBinding
import ru.malyshihvost.databinding.DialogQuizBinding
import ru.malyshihvost.detail.DogDetailActivity

// Экран 1 — каталог собак.
class CatalogActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCatalogBinding
    private val viewModel: CatalogViewModel by viewModels()

    private val adapter = DogAdapter(
        onDogClick = { dog ->
            // Открываем страницу собаки и передаём ей id через Intent.
            val intent = Intent(this, DogDetailActivity::class.java)
            intent.putExtra(DogDetailActivity.EXTRA_DOG_ID, dog.id)
            startActivity(intent)
        },
        onFavoriteClick = { dog -> viewModel.onFavoriteClick(dog) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Рисуем под системными панелями, иконки на них — тёмные (у нас светлая тема).
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        binding = ActivityCatalogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отступы, чтобы контент не залезал под статус-бар и панель навигации.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }

        binding.dogList.adapter = adapter

        setupListeners()
        observeViewModel()
    }

    // Возвращаясь с экрана собаки, обновляем сердечки — их могли поменять там.
    override fun onResume() {
        super.onResume()
        viewModel.refresh()
    }

    private fun setupListeners() {
        binding.searchInput.doAfterTextChanged { text ->
            viewModel.onSearchChanged(text.toString())
        }

        binding.chipMale.setOnClickListener { viewModel.onGenderClick(Gender.MALE) }
        binding.chipFemale.setOnClickListener { viewModel.onGenderClick(Gender.FEMALE) }
        binding.chipPuppy.setOnClickListener { viewModel.onAgeClick(AgeGroup.PUPPY) }
        binding.chipAdult.setOnClickListener { viewModel.onAgeClick(AgeGroup.ADULT) }
        binding.chipSenior.setOnClickListener { viewModel.onAgeClick(AgeGroup.SENIOR) }
        binding.chipSmall.setOnClickListener { viewModel.onSmallOnlyClick() }
        binding.chipFavorites.setOnClickListener { viewModel.onFavoritesOnlyClick() }

        binding.quizButton.setOnClickListener { showQuiz() }
        binding.quizResetButton.setOnClickListener { viewModel.onQuizReset() }
    }

    private fun observeViewModel() {
        // Список собак изменился — показываем его.
        viewModel.cards.observe(this) { cards ->
            adapter.submitList(cards)
            binding.emptyText.isVisible = cards.isEmpty()
        }

        // Фильтры изменились — отмечаем нужные чипы и плашку анкеты.
        viewModel.filter.observe(this) { filter ->
            binding.chipMale.isChecked = filter.gender == Gender.MALE
            binding.chipFemale.isChecked = filter.gender == Gender.FEMALE
            binding.chipPuppy.isChecked = filter.ageGroup == AgeGroup.PUPPY
            binding.chipAdult.isChecked = filter.ageGroup == AgeGroup.ADULT
            binding.chipSenior.isChecked = filter.ageGroup == AgeGroup.SENIOR
            binding.chipSmall.isChecked = filter.smallOnly
            binding.chipFavorites.isChecked = filter.favoritesOnly
            binding.quizBanner.isVisible = filter.quiz != null
        }
    }

    // Анкета «Подобрать собаку» во всплывающей снизу панели.
    private fun showQuiz() {
        val dialog = BottomSheetDialog(this)
        val quizBinding = DialogQuizBinding.inflate(layoutInflater)
        dialog.setContentView(quizBinding.root)

        quizBinding.showButton.setOnClickListener {
            val ageGroup = when (quizBinding.ageGroup.checkedRadioButtonId) {
                R.id.agePuppy -> AgeGroup.PUPPY
                R.id.ageAdult -> AgeGroup.ADULT
                else -> null
            }
            val answers = QuizAnswers(
                hasKids = quizBinding.kidsYes.isChecked,
                hasCats = quizBinding.catsYes.isChecked,
                littleTime = quizBinding.timeLittle.isChecked,
                ageGroup = ageGroup
            )
            viewModel.onQuizFinished(answers)
            dialog.dismiss()
        }

        dialog.show()
    }
}
