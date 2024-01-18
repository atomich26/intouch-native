package com.diegusmich.intouch.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.views.GlideImageView
import com.google.android.material.carousel.MaskableFrameLayout
import com.google.firebase.storage.StorageReference

class PostImagesCarouselAdapter(images: List<StorageReference>) : MutableAdapterImpl<StorageReference>(images) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder<StorageReference> {
        val imageView = LayoutInflater.from(parent.context).inflate(R.layout.post_image_carousel_item, parent, false)
        return PostImagesCarouselViewHolder(imageView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder<StorageReference>, position: Int) {
        holder.bind(data[position])
    }

    class PostImagesCarouselViewHolder(itemView : View) : ViewHolder<StorageReference>(itemView) {
        override fun bind(item: StorageReference) {
            itemView.findViewById<GlideImageView>(R.id.carouselItemImageView).load(item)
        }
    }
}