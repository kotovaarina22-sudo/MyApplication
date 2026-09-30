package ru.malyshihvost.catalog

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.malyshihvost.R
import ru.malyshihvost.data.Dog
import ru.malyshihvost.databinding.ItemDogBinding

// Карточка в каталоге: собака + отмечена ли она сердечком.
data class DogCard(val dog: Dog, val isFavorite: Boolean)

// Адаптер связывает список карточек с RecyclerView.
// Через лямбды он сообщает экрану о нажатиях.
class DogAdapter(
    private val onDogClick: (Dog) -> Unit,
    private val onFavoriteClick: (Dog) -> Unit
) : RecyclerView.Adapter<DogAdapter.DogViewHolder>() {

    private var items: List<DogCard> = emptyList()

    fun submitList(newItems: List<DogCard>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val binding = ItemDogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class DogViewHolder(private val binding: ItemDogBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(card: DogCard) {
            val dog = card.dog
            binding.photo.setImageResource(dog.mainPhoto)
            binding.name.text = dog.name
            binding.info.text = "${dog.ageText} · ${dog.gender.title}"
            binding.weight.text = dog.weightText

            binding.favoriteButton.setImageResource(
                if (card.isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )

            binding.root.setOnClickListener { onDogClick(dog) }
            binding.favoriteButton.setOnClickListener { onFavoriteClick(dog) }
        }
    }
}
