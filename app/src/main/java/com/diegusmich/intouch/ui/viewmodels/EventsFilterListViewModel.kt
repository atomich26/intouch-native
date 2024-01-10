package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diegusmich.intouch.data.domain.Event
import kotlinx.coroutines.Job

abstract class EventsFilterListViewModel : StateViewModel() {
    protected val _events : MutableLiveData<List<Event.Preview>?> = MutableLiveData(null)
    val events : LiveData<List<Event.Preview>?> = _events

    abstract fun onLoadEvents(id: String?, isRefreshing: Boolean = false) : Job
}