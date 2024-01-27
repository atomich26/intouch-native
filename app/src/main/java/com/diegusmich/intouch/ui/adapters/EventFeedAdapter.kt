package com.diegusmich.intouch.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.ui.activities.EventActivity
import com.diegusmich.intouch.ui.activities.UserActivity
import com.diegusmich.intouch.ui.views.GlideImageView
import com.diegusmich.intouch.ui.views.UserInfoThumbnail
import com.diegusmich.intouch.utils.TimeUtil

class EventFeedAdapter(events : List<Event.FeedPreview>) : MutableAdapterImpl<Event.FeedPreview>(events) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<Event.FeedPreview> {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.event_feed_item, parent, false)
        return EventFeedViewHolder(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder<Event.FeedPreview>, position: Int) {
        holder.bind(data[position])
    }

    class EventFeedViewHolder(itemView : View) : ViewHolder<Event.FeedPreview>(itemView){
        override fun bind(item: Event.FeedPreview) {
           itemView.apply {
               findViewById<TextView>(R.id.eventFeedName).text = item.name
               findViewById<TextView>(R.id.eventFeedCityName).text = item.city
               findViewById<TextView>(R.id.eventFeedDate).text = TimeUtil.toLocaleString(item.startAt, TimeUtil.DAY_OF_YEAR)
               findViewById<GlideImageView>(R.id.eventFeedCover).load(CloudImageProvider.EVENTS.imageRef(item.cover))
               findViewById<UserInfoThumbnail>(R.id.eventUserInfoCard).apply {
                   setUserInfo(item.userInfo)
                   setOnClickListener {
                       with(itemView.context){
                           startActivity(Intent(this@with, UserActivity::class.java).apply {
                               putExtra(UserActivity.USER_ARG, item.userInfo.id)
                           })
                       }
                   }
               }
               setOnClickListener{
                   with(itemView.context){
                       startActivity(Intent(this@with, EventActivity::class.java).apply {
                           putExtra(EventActivity.EVENT_ARG, item.id)
                       })
                   }
               }
           }
        }
    }
}