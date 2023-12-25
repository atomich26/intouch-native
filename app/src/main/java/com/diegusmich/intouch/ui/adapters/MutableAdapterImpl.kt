package com.diegusmich.intouch.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class MutableAdapterImpl<E>(collection: List<E>) :
    RecyclerView.Adapter<MutableAdapterImpl.ViewHolder<E>>(), MutableAdapter<E> {

    protected val data = collection.toMutableList()

    override fun add(items: List<E>) {
        data.addAll(items)
        notifyItemRangeInserted(data.size, items.size)
    }

    override fun replace(items: List<E>) {
        this.clear()
        data.addAll(items)
        notifyItemRangeInserted(0, data.size)
    }

    override fun clear() {
        val size = data.size
        data.clear()
        notifyItemRangeRemoved(0, size)
    }

    abstract class ViewHolder<E>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: E)
    }
}