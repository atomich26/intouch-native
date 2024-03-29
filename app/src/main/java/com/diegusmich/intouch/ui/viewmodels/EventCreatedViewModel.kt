package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.repository.EventRepository
import kotlinx.coroutines.launch

class EventCreatedViewModel : EventsFilterListViewModel() {

    override fun onLoadEvents(id: String?, isRefreshing: Boolean) = viewModelScope.launch {
        if (id.isNullOrBlank())
            return@launch updateState(_ERROR, R.string.firebaseAuthInvalidUserException)

        if(!isRefreshing && _events.value?.isEmpty() == true)
            return@launch updateState(_LOADING, false)

        updateState(_LOADING, true)

        try {
            _events.value = EventRepository.createdBy(id)
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception) {
            val messageId =
                if (isRefreshing)
                    R.string.unable_to_update_error
                else
                    R.string.firebaseNetworkException
            updateState(_ERROR, messageId)
        }
    }
}