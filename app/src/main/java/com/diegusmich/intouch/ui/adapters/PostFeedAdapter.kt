package com.diegusmich.intouch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.RecyclerView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.ui.fragments.PostFragmentDialog
import com.diegusmich.intouch.ui.views.GlideImageView
import com.diegusmich.intouch.ui.views.PostFeedView

class PostFeedAdapter(posts: List<Post.FeedPreview>) :
    MutableAdapterImpl<Post.FeedPreview>(posts){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostFeedViewHolder {
        val itemView = PostFeedView(parent.context)
        return PostFeedViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder<Post.FeedPreview>, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int = data.size

    class PostFeedViewHolder(itemView: View) : ViewHolder<Post.FeedPreview>(itemView) {
        override fun bind(item: Post.FeedPreview) {
            itemView.apply {
                findViewById<TextView>(R.id.postFeedPreviewUsername).text = item.userInfo.username
                findViewById<GlideImageView>(R.id.postFeedPreviewUserAvatar).load(CloudImageProvider.USERS.imageRef(item.userInfo.img))
                (this@apply as PostFeedView).setIsOldState(item.isOld)
                setOnClickListener {
                    (itemView.context as AppCompatActivity).supportFragmentManager.let {
                        val newFragment = PostFragmentDialog().apply {
                            arguments = bundleOf(PostFragmentDialog.POST_ID_ARG to item.id)
                        }
                        it.beginTransaction().apply {
                            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                            add(newFragment, "POST_FRAGMENT_DIALOG")
                            addToBackStack(null)
                            commit()
                        }
                    }
                }
            }
        }
    }
}