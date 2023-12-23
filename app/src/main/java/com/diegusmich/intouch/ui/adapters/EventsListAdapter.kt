package com.diegusmich.intouch.ui.adapters

import android.view.View
import android.view.ViewGroup
import com.diegusmich.intouch.data.domain.Event

class EventsListAdapter(collection: List<Event.Preview>) : DynamicDataAdapter<Event.Preview, EventsListAdapter.EventListViewHolder>(collection){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: EventListViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    class EventListViewHolder(itemView: View) : ViewHolder<Event.Preview>(itemView) {
        override fun bind(item: Event.Preview) {

        }
    }
}