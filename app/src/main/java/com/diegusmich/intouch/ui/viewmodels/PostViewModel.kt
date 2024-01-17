package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel : StateViewModel() {

    private val _post: MutableLiveData<Post.Full?> = MutableLiveData(null)
    val post: LiveData<Post.Full?> = _post

    fun onloadPost(id: String?, isRefreshing: Boolean = false) = viewModelScope.launch {
        if (id.isNullOrBlank())
            return@launch updateState(_ERROR, R.string.post_not_exists)

        updateState(_LOADING, true)

        try {
            _post.value = PostRepository.getPost(id)
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception) {
            val messageId =
                if (isRefreshing) R.string.unable_to_update_error else R.string.firebaseNetworkException
            updateState(_ERROR, messageId)
        }
    }
}