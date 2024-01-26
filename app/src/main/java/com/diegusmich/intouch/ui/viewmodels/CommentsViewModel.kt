package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Comment
import com.diegusmich.intouch.data.repository.PostRepository
import com.diegusmich.intouch.ui.views.form.FormInputLayout
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import kotlinx.coroutines.launch

class CommentsViewModel : StateViewModel() {


    private val _COMMENT_ADDED = MutableLiveData(false)
    val COMMENT_ADDED : LiveData<Boolean> = _COMMENT_ADDED

    private val _comments = MutableLiveData<List<Comment>>(mutableListOf())
    val comments : LiveData<List<Comment>> = _comments

    private val _commentText = MutableLiveData(FormInputLayout.FormInputState<String>(null))
    val commentText : LiveData<FormInputLayout.FormInputState<String>> = _commentText

    private var postId : String? = null

    fun setPostId(value : String){
        postId = value
    }

    fun onLoadComments() = viewModelScope.launch {
        updateState(_LOADING, true)
        try{
            postId?.let{
                _comments.value = PostRepository.getPostComments(it)
                updateState(_CONTENT_LOADED, true)
            }
        }catch (e : Exception){
            updateState(_ERROR, R.string.firebaseNetworkException)
        }
    }

    fun onUpdateCommentText(input: String){
        _commentText.value = _commentText.value?.copy(inputValue = input, isValid = input.isNotBlank() && input.length < 250)
    }

    fun onAddNewComment() = viewModelScope.launch {
        commentText.value?.let {
            if(!it.isValid)
                return@launch

            try {
                postId?.let{ postId ->
                    updateState(_LOADING, true)
                    PostRepository.addComment(postId, commentText.value?.inputValue.toString())
                    updateState(_COMMENT_ADDED, true)
                    onUpdateCommentText("")
                    onLoadComments()
                }
            }catch (e : Exception){
                updateState(_ERROR, R.string.firebaseNetworkException)
            }
        }
    }
}