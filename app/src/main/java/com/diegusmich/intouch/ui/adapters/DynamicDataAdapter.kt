package com.diegusmich.intouch.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class DynamicDataAdapter<E, T : DynamicDataAdapter.ViewHolder<E>>(collection: List<E>) : RecyclerView.Adapter<T>() {

    protected val data = collection.toMutableList()

    fun add(items : List<E>){
        data.addAll(items)
        notifyItemRangeInserted(data.size, items.size)
    }

    fun replace(items : List<E>){
        this.clear()
        data.addAll(items)
        notifyItemRangeInserted(0, data.size)
    }

    fun clear(){
        val size = data.size
        data.clear()
        notifyItemRangeRemoved(0, size)
    }

    abstract class ViewHolder<E>(view: View) : RecyclerView.ViewHolder(view){
        abstract fun bind(item : E)
    }
}