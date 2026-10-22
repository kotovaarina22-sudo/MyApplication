package ru.malyshihvost.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.malyshihvost.databinding.ItemPhotoBinding

// Адаптер для галереи: каждая «страница» ViewPager2 — одно фото.
class PhotoAdapter(private val photos: List<Int>) :
    RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun getItemCount(): Int = photos.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhotoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.binding.photo.setImageResource(photos[position])
    }

    class PhotoViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)
}
