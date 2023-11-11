package com.diegusmich.intouch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.views.GlideImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoriesGridAdapter(private val data: List<Category>) :
    RecyclerView.Adapter<CategoriesGridAdapter.CategoryGridViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryGridViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.category_grid_item, parent, false)

        return CategoryGridViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: CategoryGridViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class CategoryGridViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(category: Category) {
            CoroutineScope(Dispatchers.Main).launch {
                val imgRef = CloudImageService.CATEGORIES.imageRef(category.cover!!)
                itemView.findViewById<TextView>(R.id.categoryNameGridItem).text = category.name
                itemView.findViewById<GlideImageView>(R.id.categoryImageGridItem).load(imgRef)
            }
        }
    }
}