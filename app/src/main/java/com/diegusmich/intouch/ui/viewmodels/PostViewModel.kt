package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.authorization.Policies
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel : StateViewModel() {

    private val _post: MutableLiveData<Post.Full?> = MutableLiveData(null)
    val post: LiveData<Post.Full?> = _post

    private val _POST_DELETED = MutableLiveData(false)
    val POST_DELETED : LiveData<Boolean> = _POST_DELETED

    fun onloadPost(id: String?, isRefreshing: Boolean = false) = viewModelScope.launch {
        if (id.isNullOrBlank())
            return@launch updateState(_ERROR, R.string.post_not_exists)

        if(post.value == null || isRefreshing){
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

    fun onDeletePost() = viewModelScope.launch {
        if(_post.value == null || Policies.canDeletePost(_post.value!!))
            return@launch

        updateState(_LOADING, true)
        try{
            _post.value?.let{
                PostRepository.deletePost(it.id)
                updateState(_POST_DELETED, true)
            }
        }catch (e : Exception){
            updateState(_ERROR, R.string.firebaseNetworkException)
        }
    }
}