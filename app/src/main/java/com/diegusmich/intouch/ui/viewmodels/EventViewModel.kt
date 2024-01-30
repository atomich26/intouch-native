package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.authorization.Policies
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.Friendship
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.repository.EventRepository
import com.diegusmich.intouch.data.repository.FriendshipRepository
import com.diegusmich.intouch.data.repository.UserRepository
import com.diegusmich.intouch.providers.AuthProvider
import com.diegusmich.intouch.utils.ErrorUtil
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class EventViewModel : StateViewModel() {

    private val _event : MutableLiveData<Event.Full?> = MutableLiveData(null)
    val event : LiveData<Event.Full?> = _event

    private val _attendees = MutableLiveData<List<User.Preview>>(mutableListOf())
    val attendees : LiveData<List<User.Preview>> = _attendees

    private val _canEdit = MutableLiveData(false)
    val canEdit : LiveData<Boolean> = _canEdit

    private val _state = MutableLiveData<Event.STATE>(Event.STATE.TERMINATED_NOT_JOINED)
    val state : LiveData<Event.STATE> = _state

    private val _DELETED = MutableLiveData(false)
    val DELETED : LiveData<Boolean> = _DELETED

    fun onLoadEvent(eventId: String?) = viewModelScope.launch {
        if(eventId.isNullOrBlank())
            return@launch updateState(_ERROR, R.string.event_not_exists)

        updateState(_LOADING, true)

        try{
            eventId.let{
                val getEventJob = viewModelScope.async { EventRepository.event(eventId) }
                val getAttendeesJob = viewModelScope.async { EventRepository.attendees(eventId) }

                getAttendeesJob.await().let { attendees ->
                    _attendees.value = attendees
                    getEventJob.await()?.let {event ->
                        _event.value = event
                        _canEdit.value = Policies.canEditEvent(event)
                        val isFriend = FriendshipRepository.isFriend(event.userInfo.id)
                        _state.value = Policies.canEventState(event, attendees, isFriend)
                    }
                }
                updateState(_CONTENT_LOADED, true)
            }
        }catch (e : Exception){
            updateState(_ERROR, R.string.firebaseFirestoreException)
        }
    }

    fun onDeleteEvent() = viewModelScope.launch {
        _event.value?.let {
            updateState(_LOADING, true)
            try{
                EventRepository.deleteEvent(it.id)
                updateState(_DELETED, true)
            }catch (e: FirebaseFunctionsException){
               val messageId = when(e.code){
                   FirebaseFunctionsException.Code.INVALID_ARGUMENT  -> {
                       ErrorUtil.getMessage(e.message.toString())
                   }
                   else ->{
                       R.string.internal_error
                   }
               }
                updateState(_ERROR, messageId)
            }
            catch (e : Exception){
                updateState(_ERROR, R.string.firebaseNetworkException)
            }
        }
    }

    fun onJoinEvent() = viewModelScope.launch{
        if(_event.value?.userInfo?.id == AuthProvider.authUser()?.uid || _LOADING.value == true)
            return@launch

        try{
            updateState(_LOADING, true)
            _state.value?.let { state ->
                if(state == Event.STATE.ACTIVE_AVAILABLE || state == Event.STATE.ACTIVE_JOINED){
                    _event.value?.id?.let{
                        val join = state == Event.STATE.ACTIVE_AVAILABLE
                        val response = EventRepository.join(it, join)

                        if(response.data as Boolean)
                            _state.value = Event.STATE.ACTIVE_JOINED
                        else
                            _state.value = Event.STATE.ACTIVE_AVAILABLE

                        _attendees.value = EventRepository.attendees(it)
                    }
                }
            }
            updateState(_LOADING, false)
        }catch (e: Exception){
            if(e !is FirebaseFunctionsException)
                return@launch updateState(_ERROR, R.string.default_invalid_error)

            val messageId = when(e.code){
                FirebaseFunctionsException.Code.INVALID_ARGUMENT ->{
                    ErrorUtil.getMessage(e.message.toString())
                }
                FirebaseFunctionsException.Code.PERMISSION_DENIED ->{
                    if(e.message.toString() == "TERMINATED_JOINED")
                        _state.value = Event.STATE.TERMINATED_JOINED
                    else if(e.message.toString() == "TERMINATED_NOT_JOINED")
                        _state.value = Event.STATE.TERMINATED_NOT_JOINED
                    ErrorUtil.getMessage(e.message.toString())
                }
                FirebaseFunctionsException.Code.UNAVAILABLE -> {
                    _state.value = Event.STATE.ACTIVE_NOT_AVAILABLE
                    ErrorUtil.getMessage(e.message.toString())
                }
                else -> ErrorUtil.getMessage(e.message.toString())
            }
            updateState(_ERROR, messageId)
        }
    }
}