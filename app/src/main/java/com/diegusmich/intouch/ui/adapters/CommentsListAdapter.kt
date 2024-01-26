package com.diegusmich.intouch.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Comment
import com.diegusmich.intouch.ui.activities.UserActivity
import com.diegusmich.intouch.ui.views.UserInfoThumbnail
import org.ocpsoft.prettytime.PrettyTime

class CommentsListAdapter(collection: List<Comment>) :
    MutableAdapterImpl<Comment>(collection) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.comment_card_layout, parent, false)
        return CommentsViewHolder(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder<Comment>, position: Int) {
        holder.bind(data[position])
    }

    class CommentsViewHolder(itemView: View) : ViewHolder<Comment>(itemView) {
        override fun bind(item: Comment) {
            itemView.findViewById<UserInfoThumbnail>(R.id.commentUserInfo).apply {
                setUserInfo(item.userInfo)
                setOnClickListener {
                    itemView.context.let{
                        it.startActivity(Intent(it, UserActivity::class.java).apply {
                            putExtra(UserActivity.USER_ARG, item.userInfo.id)
                        })
                    }
                }
            }
            itemView.findViewById<TextView>(R.id.commentText).text = item.content
            itemView.findViewById<TextView>(R.id.commentCreatedDate).text = PrettyTime().format(item.createdAt)
        }
    }
}