package com.diegusmich.intouch.ui.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.repository.EventRepository
import com.diegusmich.intouch.data.repository.PostRepository
import com.diegusmich.intouch.providers.UserLocationProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class FeedViewModel : StateViewModel() {

    private val _postFeed = MutableLiveData<List<Post.FeedPreview>?>(null)
    val postFeed: LiveData<List<Post.FeedPreview>?> = _postFeed

    private val _eventFeed = MutableLiveData<List<Event.FeedPreview>?>(null)
    val eventFeed: LiveData<List<Event.FeedPreview>?> = _eventFeed

    private val _GET_LOCATION_FAILED = MutableLiveData(false)
    val GET_LOCATION_FAILED : LiveData<Boolean> = _GET_LOCATION_FAILED

    private val _locationFailedMessageConsumed = MutableLiveData(false)
    val locationFailedMessageConsumed : LiveData<Boolean> = _locationFailedMessageConsumed

    fun onLoadMainFeed(isRefreshing: Boolean = false) = viewModelScope.launch {
        if (_postFeed.value != null && !isRefreshing)
            return@launch

        updateState(_LOADING, true)
        try {
            _postFeed.value = PostRepository.feed()
            _eventFeed.value = EventRepository.feed(getCurrentLocation().await())
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception) {
            updateState(_ERROR, R.string.firebaseNetworkException)
        }
    }

    fun consumeMessage(){
        _locationFailedMessageConsumed.value = true
    }

    private fun getCurrentLocation() = viewModelScope.async {
        val currentLocation = UserLocationProvider.getCurrentLocation()

        if(currentLocation == UserLocationProvider.defaultLocation)
            updateState(_GET_LOCATION_FAILED, true)

        currentLocation
    }
}