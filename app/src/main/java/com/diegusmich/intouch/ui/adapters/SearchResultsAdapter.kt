package com.diegusmich.intouch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.UserPreview

class SearchResultsAdapter(collection: List<Any>) :
    RecyclerView.Adapter<SearchResultsAdapter.SearchResultsViewHolder>() {

    private val data = collection.toMutableList()

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
        fun bind(item : Any?) {
        /*
            if(userPreview.img != null){
                val imgRef = CloudImageService.USERS.imageRef(userPreview.img)
                itemView.findViewById<GlideImageView>(R.id.userListItemAvatar).load(imgRef)
            }

            itemView.findViewById<TextView>(R.id.userListItemNameText).text = userPreview.name
            itemView.findViewById<TextView>(R.id.userListItemUsernameText).text = userPreview.username
        }
        */
        }
    }

    fun add(items : List<Any>){
        data.addAll(items)
        notifyDataSetChanged()
    }

    fun replace(items : List<Any>){
        this.clear()
        data.addAll(items)
    }

    fun clear(){
        val size = data.size
        data.clear()
        notifyItemRangeRemoved(0, size);
    }

}