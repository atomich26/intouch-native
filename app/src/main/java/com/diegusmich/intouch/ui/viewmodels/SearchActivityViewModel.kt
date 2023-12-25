package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.repository.EventRepository
import com.diegusmich.intouch.data.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class SearchActivityViewModel : StateViewModel() {

    private val _searchUserResults : MutableLiveData<List<User.Preview>> = MutableLiveData(
        mutableListOf()
    )
    val searchUserResult : LiveData<List<User.Preview>> = _searchUserResults

    private val _searchEventResults : MutableLiveData<List<Event.Preview>> = MutableLiveData(
        mutableListOf()
    )
    val searchEventResult : LiveData<List<Event.Preview>> = _searchEventResults

    fun onRunSearch(queryText : String, searchType: SearchType) = viewModelScope.launch {
        updateState(_LOADING, true)

        val sanitized = queryText.trim().let {
            if(searchType is SearchType.USERS)
                queryText.replace("@", "")
            else it
        }

        if(sanitized.isBlank())
            return@launch updateState(_ERROR, R.string.search_empty_text)

        try{
            when(searchType){
                is SearchType.USERS ->{
                    _searchUserResults.value = UserRepository.search(sanitized)
                }
                is SearchType.EVENTS -> {
                    _searchEventResults.value = EventRepository.search(sanitized)
                }
            }
            updateState(_CONTENT_LOADED, true)
        }catch (e : Exception){
            val messageId = if (e.cause is UnknownHostException || e.cause is ConnectException || e is FirebaseNetworkException)
                R.string.firebaseNetworkException
            else
                R.string.firebaseFirestoreException

            updateState(_ERROR, messageId)
        }
    }

    sealed interface SearchType{
        data object USERS : SearchType
        data object EVENTS : SearchType
    }
}