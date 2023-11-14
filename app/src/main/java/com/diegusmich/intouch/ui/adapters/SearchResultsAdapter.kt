package com.diegusmich.intouch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.views.GlideImageView

class SearchResultsAdapter(private val data: MutableList<UserPreview>) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)

        return SearchResultsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class SearchResultsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(userPreview : UserPreview) {
            if(userPreview.img != null){
                val imgRef = CloudImageService.USERS.imageRef(userPreview.img)
                itemView.findViewById<GlideImageView>(R.id.userListItemAvatar).load(imgRef)
            }

            itemView.findViewById<TextView>(R.id.userListItemNameText).text = userPreview.name
            itemView.findViewById<TextView>(R.id.userListItemUsernameText).text = userPreview.username
        }
    }

    fun clear(){
        val size = data.size
        data.clear()
        notifyItemRangeRemoved(0, size);
    }
}