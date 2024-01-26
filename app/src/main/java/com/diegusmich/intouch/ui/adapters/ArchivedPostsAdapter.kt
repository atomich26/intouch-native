package com.diegusmich.intouch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentTransaction
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.ui.fragments.PostFragmentDialog
import com.diegusmich.intouch.ui.fragments.ProfileFragment
import com.diegusmich.intouch.ui.views.GlideImageView
import com.diegusmich.intouch.utils.TimeUtil

class ArchivedPostsAdapter(collection: List<Post.ArchivePreview>) :
    MutableAdapterImpl<Post.ArchivePreview>(collection) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchivedPostViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_post_grid_item, parent, false)
        return ArchivedPostViewHolder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder<Post.ArchivePreview>, position: Int) {
        holder.bind(data[position])
    }

    class ArchivedPostViewHolder(itemView: View) : ViewHolder<Post.ArchivePreview>(itemView) {
        override fun bind(item: Post.ArchivePreview) {
            val imgRef = CloudImageProvider.POSTS.imageRef(item.thumbnail)
            itemView.findViewById<GlideImageView>(R.id.postPreviewProfileGridItem).load(imgRef)
            itemView.findViewById<TextView>(R.id.datePostProfileGridItem).text =
                TimeUtil.toLocaleString(item.createdAt, TimeUtil.DAY_OF_YEAR)

            itemView.setOnClickListener { _ ->
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