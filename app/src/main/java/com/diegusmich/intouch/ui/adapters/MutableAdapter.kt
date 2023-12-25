package com.diegusmich.intouch.ui.adapters

interface MutableAdapter<T> {
    fun add(items: List<T>)
    fun replace(items: List<T>)
    fun clear()
}