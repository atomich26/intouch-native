package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.repository.PostRepository
import kotlinx.coroutines.launch

class FeedViewModel : StateViewModel() {

    private val _postFeed = MutableLiveData<List<Post.FeedPreview>?>(null)
    val postFeed: LiveData<List<Post.FeedPreview>?> = _postFeed

    fun onLoadMainFeed(isRefreshing: Boolean = false) = viewModelScope.launch {
        if (_postFeed.value != null && !isRefreshing)
            return@launch

        updateState(_LOADING, true)
        try {
            _postFeed.value = PostRepository.feed()
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception) {
            updateState(_ERROR, R.string.firebaseNetworkException)
        }
    }
}