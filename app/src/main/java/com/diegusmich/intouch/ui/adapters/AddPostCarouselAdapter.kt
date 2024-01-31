package com.diegusmich.intouch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.views.GlideImageView
import java.io.File

class AddPostCarouselAdapter(images: List<File>) : MutableAdapterImpl<File>(images) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddPostCarouselViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.post_image_carousel_item, parent, false)
        return AddPostCarouselViewHolder(imageView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder<File>, position: Int) {
        holder.bind(data[position])
    }

    class AddPostCarouselViewHolder(itemView: View) : ViewHolder<File>(itemView) {
        override fun bind(item: File) {
            itemView.findViewById<GlideImageView>(R.id.carouselItemImageView).load(item)
        }
    }
}