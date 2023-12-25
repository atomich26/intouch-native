package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.repository.EventRepository
import com.diegusmich.intouch.data.repository.UserRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchActivityViewModel : StateViewModel() {

    private val _searchUserResults : MutableLiveData<List<User.Preview>?> = MutableLiveData(null)
    val searchUserResult : LiveData<List<User.Preview>?> = _searchUserResults

    private val _searchEventResults : MutableLiveData<List<Event.Preview>?> = MutableLiveData(null)
    val searchEventResult : LiveData<List<Event.Preview>?> = _searchEventResults

    private var searchingJob : Job? = null

    fun onRunSearch(queryText : String, searchType: SearchType) {
        searchingJob?.cancel()
        searchingJob = viewModelScope.launch {
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
                if(e is CancellationException)
                    return@launch

                updateState(_ERROR, R.string.firebaseFirestoreException)
            }
        }.apply {
            invokeOnCompletion {
                if(it != null)
                    updateState(_CONTENT_LOADED, false)
            }
        }
    }

    fun onCancelSearch(){
        if(searchingJob?.isActive == true)
            searchingJob?.cancel()
    }

    sealed interface SearchType{
        data object USERS : SearchType
        data object EVENTS : SearchType
    }
}