package com.diegusmich.intouch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.ArchivedPostPreview
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.views.GlideImageView
import com.diegusmich.intouch.utils.TimeUtil

class ArchivedPostsAdapter(collection: List<ArchivedPostPreview>) : DynamicDataAdapter<ArchivedPostPreview, ArchivedPostsAdapter.ArchivedPostViewHolder>(collection) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchivedPostViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.profile_post_grid_item, parent, false)
        return ArchivedPostViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ArchivedPostViewHolder, position: Int) {
        holder.bindData(data[position])
    }

    class ArchivedPostViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        fun bindData(data : ArchivedPostPreview){
            val imgRef = CloudImageService.POSTS.imageRef(data.thumbnail)
            itemView.findViewById<GlideImageView>(R.id.postPreviewProfileGridItem).load(imgRef)
            itemView.findViewById<TextView>(R.id.datePostProfileGridItem).text = TimeUtil.toLocaleString(data.createdAt)

            itemView.setOnClickListener{
                Toast.makeText(itemView.context, "Post ${data.id}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}