package com.diegusmich.intouch.ui.activities.search

import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.EventPreview
import com.diegusmich.intouch.data.model.UserPreview
import com.diegusmich.intouch.data.response.SearchUserResponse
import com.diegusmich.intouch.ui.state.StateViewModel
import com.diegusmich.intouch.ui.state.UiState
import com.diegusmich.intouch.utils.ErrorUtil
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class SearchActivityViewModel : StateViewModel() {

    private val _searchUserResults : MutableStateFlow<List<UserPreview>?> = MutableStateFlow(null)
    val searchUserResult = _searchUserResults.asStateFlow()

    private val _searchEventResults : MutableStateFlow<List<EventPreview>?> = MutableStateFlow(null)
    val searchEventResult = _searchEventResults.asStateFlow()

    fun onSearchByUsername(queryText : String) = viewModelScope.launch {
        updateState(UiState.LOADING)

        val sanitized = queryText.replace("@", "")
        Firebase.functions.getHttpsCallable("users-search").call(mapOf("query" to sanitized))
            .addOnSuccessListener {
                updateState(UiState.CONTENT_LOADED)
                _searchUserResults.value = SearchUserResponse().parse(it.data!!)
        }
            .addOnFailureListener{
                _errorMessage = if (it.cause is UnknownHostException || it.cause is ConnectException)
                    R.string.firebaseNetworkException
                else
                    R.string.firebaseDefaultExceptionMessage

                updateState(UiState.ERROR)
            }
    }

    fun onSearchByEvent(queryText : String){

    }
}