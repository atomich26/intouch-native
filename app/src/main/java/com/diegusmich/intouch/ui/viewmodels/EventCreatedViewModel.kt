package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventCreatedViewModel : StateViewModel() {

    private val _events: MutableLiveData<List<Event.Preview>> = MutableLiveData(mutableListOf())
    val events: LiveData<List<Event.Preview>> = _events

    fun onLoadCreatedEvents(userId: String?, isRefreshing: Boolean = false) = viewModelScope.launch {
        if (userId.isNullOrBlank())
            return@launch updateState(_ERROR, R.string.firebaseAuthInvalidUserException)

        updateState(_LOADING, true)

        try {
            _events.value = EventRepository.createdBy(userId)
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception) {
            val messageId =
                if (isRefreshing)
                    R.string.unable_to_update_error
                else
                    R.string.firebaseFirestoreException
            updateState(_ERROR, messageId)
        }
    }
}