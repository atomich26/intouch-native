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
import com.diegusmich.intouch.ui.views.GlideImageView
import com.diegusmich.intouch.utils.TimeUtil

class EventsListAdapter(collection: List<Event.Preview>) :
    MutableAdapterImpl<Event.Preview>(collection) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListViewHolder {
        return LayoutInflater.from(parent.context).inflate(R.layout.event_list_item, parent, false)
            .let {
                EventListViewHolder(it)
            }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder<Event.Preview>, position: Int) {
        holder.bind(data[position])
    }

    class EventListViewHolder(itemView: View) : ViewHolder<Event.Preview>(itemView) {
        override fun bind(item: Event.Preview) {
            itemView.findViewById<GlideImageView>(R.id.eventListItemThumbnail).apply {
                load(CloudImageProvider.EVENTS.imageRef(item.cover))
            }
            itemView.findViewById<TextView>(R.id.eventListItemCity).text = item.city
            itemView.findViewById<TextView>(R.id.eventListItemName).text = item.name
            itemView.findViewById<TextView>(R.id.eventListItemDate).text =
                TimeUtil.toLocaleString(item.startAt, TimeUtil.DAY_OF_YEAR)

            itemView.setOnClickListener {
                itemView.context.startActivity(
                    Intent(
                        itemView.context,
                        EventActivity::class.java
                    ).apply {
                        putExtra(EventActivity.EVENT_ARG, item.id)
                    })
            }
        }
    }
}