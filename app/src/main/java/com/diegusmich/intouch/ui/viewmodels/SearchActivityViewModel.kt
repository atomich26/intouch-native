package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.EventPreview
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.data.response.SearchUserResponse
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
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

        val sanitized = queryText.replace("@", "")

        Firebase.functions.getHttpsCallable("users-search").call(mapOf("query" to sanitized))
            .addOnSuccessListener {
                updateState(_CONTENT_LOADED, true)
                //_searchUserResults.value = SearchUserResponse().parse(it.data!!)
        }
            .addOnFailureListener{
                val messageId = if (it.cause is UnknownHostException || it.cause is ConnectException)
                    R.string.firebaseNetworkException
                else
                    R.string.firebaseDefaultExceptionMessage

                updateState(_ERROR, messageId)
            }
    }

    fun onSearchByEvent(queryText : String) = viewModelScope.launch {

    }
}