package com.diegusmich.intouch.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.ui.activities.UserActivity
import com.diegusmich.intouch.ui.views.GlideImageView

class UsersListAdapter(collection: List<User.Preview>) :
    MutableAdapterImpl<User.Preview>(collection) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserListAdapterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.user_list_item, parent, false)
        return UserListAdapterViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder<User.Preview>, position: Int) {
        holder.bind(data[position])
    }

    class UserListAdapterViewHolder(itemView: View) : ViewHolder<User.Preview>(itemView) {
        override fun bind(item: User.Preview) {
            itemView.findViewById<TextView>(R.id.userListItemNameText).text = item.name
            itemView.findViewById<TextView>(R.id.userListItemUsernameText).text = item.username

            val imgRef = CloudImageProvider.USERS.imageRef(item.img)
            itemView.findViewById<GlideImageView>(R.id.userListItemAvatar).load(imgRef)

            itemView.setOnClickListener {
                itemView.context.startActivity(
                    Intent(
                        itemView.context,
                        UserActivity::class.java
                    ).apply {
                        putExtra(UserActivity.USER_ARG, item.id)
                    })
            }
        }
    }
}