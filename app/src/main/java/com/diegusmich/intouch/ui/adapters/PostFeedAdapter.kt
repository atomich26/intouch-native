package com.diegusmich.intouch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Post

class PostFeedAdapter(private val posts : List<Post.FeedPreview>) : RecyclerView.Adapter<PostFeedAdapter.PostFeedViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostFeedViewHolder {
        val itemView =  LayoutInflater.from(parent.context).inflate(R.layout.post_feed_preview, parent, false)
        return PostFeedViewHolder(itemView)
    }

    override fun getItemCount(): Int = posts.size

    override fun onBindViewHolder(holder: PostFeedViewHolder, position: Int) {

    }

    class PostFeedViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
}