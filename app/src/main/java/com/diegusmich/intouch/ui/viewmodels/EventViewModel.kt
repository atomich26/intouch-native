package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel : StateViewModel() {

    private val _event : MutableLiveData<Event.Full?> = MutableLiveData(null)
    val event : LiveData<Event.Full?> = _event

    fun onLoadEvent(eventId: String?) = viewModelScope.launch {
        if(eventId.isNullOrBlank())
            return@launch updateState(_ERROR, R.string.event_not_exists)

        updateState(_LOADING, true)

        try{
            _event.value = EventRepository.event(eventId)
            updateState(_CONTENT_LOADED, true)
        }catch (e : Exception){
            updateState(_ERROR, R.string.firebaseFirestoreException)
        }
    }
}