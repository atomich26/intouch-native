package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.EventPreview
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.data.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class SearchActivityViewModel : StateViewModel() {

    private val _searchUserResults : MutableLiveData<List<UserPreview>?> = MutableLiveData(null)
    val searchUserResult : LiveData<List<UserPreview>?> = _searchUserResults

    private val _searchEventResults : MutableLiveData<List<EventPreview>?> = MutableLiveData(null)
    val searchEventResult : LiveData<List<EventPreview>?> = _searchEventResults

    fun onSearchByUsername(queryText : String) = viewModelScope.launch {
        updateState(_LOADING, true)

        try{
            _searchUserResults.value = UserRepository.searchUser(queryText)
            updateState(_CONTENT_LOADED, true)
        }catch (e : Exception){
            val messageId = if (e.cause is UnknownHostException || e.cause is ConnectException || e is FirebaseNetworkException)
                R.string.firebaseNetworkException
            else
                R.string.firebaseDefaultExceptionMessage

            updateState(_ERROR, messageId)
        }
    }

    fun onSearchByEvent(queryText : String) = viewModelScope.launch {

    }
}