package com.diegusmich.intouch.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.EventPreview
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.service.CloudImageService
import com.diegusmich.intouch.ui.activities.UserActivity
import com.diegusmich.intouch.ui.views.GlideImageView

class SearchResultsAdapter(collection: List<Any>) :
    DynamicDataAdapter<Any, SearchResultsAdapter.SearchResultsViewHolder>(collection) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return SearchResultsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
       return when(data[position]){
           is EventPreview -> R.layout.event_preview_small
           is UserPreview -> R.layout.user_list_item
           else -> 0
       }
    }

    override fun onBindViewHolder(holder: SearchResultsViewHolder, position: Int) {
        holder.bind(data[position])
    }

    class SearchResultsViewHolder(itemView: View) : ViewHolder<Any>(itemView) {
        override fun bind(item : Any) {
            if(item is UserPreview){
                val imgRef = CloudImageService.USERS.imageRef(item.img)
                itemView.findViewById<GlideImageView>(R.id.userListItemAvatar).load(imgRef)
                itemView.findViewById<TextView>(R.id.userListItemNameText).text = item.name
                itemView.findViewById<TextView>(R.id.userListItemUsernameText).text = item.username
                itemView.setOnClickListener{
                    itemView.context.startActivity(Intent(itemView.context, UserActivity::class.java).apply {
                        putExtra(UserActivity.USER_ARG, item.id)
                    })
                }
            }else if(item is EventPreview){

            }
        }
    }
}