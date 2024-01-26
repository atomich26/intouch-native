package com.diegusmich.intouch.ui.adapters

import android.content.Intent
import android.text.Html
import android.text.Html.FROM_HTML_MODE_COMPACT
import android.text.Spanned
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.FriendshipRequest
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.ui.activities.UserActivity
import com.diegusmich.intouch.ui.views.GlideImageView
import org.ocpsoft.prettytime.PrettyTime

class FriendshipRequestAdapter(collection: List<FriendshipRequest>) :
    MutableAdapterImpl<FriendshipRequest>(collection) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FriendShipRequestViewholder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.friendship_request_list_item, parent, false)
        return FriendShipRequestViewholder(itemView)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(
        holder: ViewHolder<FriendshipRequest>,
        position: Int
    ) {
        holder.bind(data[position])
    }

    class FriendShipRequestViewholder(itemView: View) :
        ViewHolder<FriendshipRequest>(itemView) {

        //Altro schifo di questo abominio di OS. Inserisci ![CDATA[ nella stringa
        // se la vuoi formattare con gli stili e nella doc ufficiale non c'è SCRITTOOOOO!!!!!!

        override fun bind(item: FriendshipRequest) {
            val escapedUsername: String = TextUtils.htmlEncode(item.actor.username)
            val text: String =
                itemView.context.getString(R.string.friendship_request_message, escapedUsername)
            val styledText: Spanned = Html.fromHtml(text, FROM_HTML_MODE_COMPACT)
            itemView.findViewById<TextView>(R.id.friendshipRequestMessage).text = styledText
            itemView.findViewById<TextView>(R.id.friendshipCreatedAtText).text =
                PrettyTime().format(item.createdAt)
            itemView.findViewById<GlideImageView>(R.id.actorAvatar)
                .load(CloudImageProvider.USERS.imageRef(item.actor.img))
            itemView.setOnClickListener {
                itemView.context.let {
                    it.startActivity(Intent(it, UserActivity::class.java).apply {
                        putExtra(UserActivity.USER_ARG, item.actor.id)
                    })
                }
            }
        }
    }
}