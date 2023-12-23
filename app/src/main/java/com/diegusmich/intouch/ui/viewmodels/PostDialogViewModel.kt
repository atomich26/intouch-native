package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.data.domain.Post
import kotlinx.coroutines.launch

class PostDialogViewModel : StateViewModel() {

    private val _post: MutableLiveData<Post> = MutableLiveData(null)
    val post: LiveData<Post> = _post

    fun loadPost(id: String) = viewModelScope.launch {

        updateState(_LOADING, true)


    }
}