package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.authorization.Policies
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventViewModel : StateViewModel() {

    private val _event : MutableLiveData<Event.Full?> = MutableLiveData(null)
    val event : LiveData<Event.Full?> = _event

    private val _canEdit = MutableLiveData(false)
    val canEdit : LiveData<Boolean> = _canEdit

    fun onLoadEvent(eventId: String?) = viewModelScope.launch {
        if(eventId.isNullOrBlank())
            return@launch updateState(_ERROR, R.string.event_not_exists)

        updateState(_LOADING, true)

        try{
            EventRepository.event(eventId)?.let {
                _event.value = it
                _canEdit.value = Policies.canEditEvent(it)
            }

            updateState(_CONTENT_LOADED, true)

        }catch (e : Exception){
            updateState(_ERROR, R.string.firebaseFirestoreException)
        }
    }
}