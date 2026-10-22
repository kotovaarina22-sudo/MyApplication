package ru.malyshihvost.request

import android.app.DatePickerDialog
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
import ru.malyshihvost.R
import ru.malyshihvost.catalog.CatalogActivity
import ru.malyshihvost.databinding.ActivityRequestBinding
import java.util.Calendar

// Экран 3 — форма заявки.
class RequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRequestBinding
    private val viewModel: RequestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
        binding = ActivityRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отступы от системных панелей. Снизу учитываем ещё и клавиатуру (ime),
        // чтобы она не закрывала поля формы.
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val keyboard = insets.getInsets(WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, maxOf(bars.bottom, keyboard.bottom))
            insets
        }

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

        // Для «Прийти погулять» показываем дату и прячем вопросы о доме.
        binding.typeGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val isWalk = checkedId == R.id.typeWalk
                binding.dateLayout.isVisible = isWalk
                binding.homeSection.isVisible = !isWalk
            }
        }
        binding.dateInput.setOnClickListener { showDatePicker() }

        // Начали исправлять поле — убираем с него красную ошибку.
        binding.nameInput.doAfterTextChanged { binding.nameLayout.error = null }
        binding.phoneInput.doAfterTextChanged { binding.phoneLayout.error = null }
        binding.emailInput.doAfterTextChanged { binding.emailLayout.error = null }
        binding.housingInput.doAfterTextChanged { binding.housingLayout.error = null }
        binding.animalsInput.doAfterTextChanged { binding.animalsLayout.error = null }
        binding.consentCheck.setOnCheckedChangeListener { _, _ -> binding.consentCheck.isErrorShown = false }

        binding.submitButton.setOnClickListener { viewModel.submit(readForm()) }
        binding.backToCatalogButton.setOnClickListener { openCatalog() }
    }

    private fun observeViewModel() {
        viewModel.dog.observe(this) { dog ->
            binding.dogPhoto.setImageResource(dog.mainPhoto)
            binding.dogName.text = dog.name
            binding.dogBreed.text = dog.breed
        }

        viewModel.errors.observe(this) { errors ->
            binding.dateLayout.error = errors.walkDate?.let { getString(it) }
            binding.nameLayout.error = errors.name?.let { getString(it) }
            binding.phoneLayout.error = errors.phone?.let { getString(it) }
            binding.emailLayout.error = errors.email?.let { getString(it) }
            binding.housingLayout.error = errors.housing?.let { getString(it) }
            binding.animalsLayout.error = errors.animals?.let { getString(it) }
            binding.consentCheck.isErrorShown = errors.consent
            if (errors.hasErrors) {
                // Прокручиваем наверх, чтобы человек увидел ошибки.
                binding.formScroll.smoothScrollTo(0, 0)
            }
        }

        viewModel.sentForm.observe(this) { form ->
            if (form != null) showSuccess(form)
        }
    }

    // Собираем всё, что введено в форму, в один объект.
    private fun readForm(): RequestForm {
        val type = when (binding.typeGroup.checkedButtonId) {
            R.id.typeFoster -> RequestType.FOSTER
            R.id.typeWalk -> RequestType.WALK
            else -> RequestType.ADOPT
        }
        return RequestForm(
            type = type,
            walkDate = binding.dateInput.text.toString(),
            name = binding.nameInput.text.toString(),
            phone = binding.phoneInput.text.toString(),
            email = binding.emailInput.text.toString(),
            housing = binding.housingInput.text.toString(),
            animals = binding.animalsInput.text.toString(),
            comment = binding.commentInput.text.toString(),
            consent = binding.consentCheck.isChecked
        )
    }

    // Календарь для выбора даты прогулки. Прошедшие дни выбрать нельзя.
    private fun showDatePicker() {
        val today = Calendar.getInstance()
        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                // Месяцы в Calendar считаются с 0, поэтому +1.
                val text = String.format("%02d.%02d.%d", day, month + 1, year)
                binding.dateInput.setText(text)
                binding.dateLayout.error = null
            },
            today.get(Calendar.YEAR),
            today.get(Calendar.MONTH),
            today.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.minDate = today.timeInMillis
        dialog.show()
    }

    // Прячем форму и показываем «Спасибо».
    private fun showSuccess(form: RequestForm) {
        binding.formScroll.isVisible = false
        binding.successView.isVisible = true
        binding.successTitle.text = getString(R.string.success_title, form.name.trim())
        binding.successText.text = getString(
            R.string.success_text,
            form.type.title,
            viewModel.dog.value?.name,
            form.phone.trim().replace(' ', '\u00A0')  // неразрывные пробелы — номер не переносится
        )
    }

    // Возвращаемся в каталог и закрываем экраны собаки и заявки.
    private fun openCatalog() {
        val intent = Intent(this, CatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_DOG_ID = "dog_id"
    }
}
