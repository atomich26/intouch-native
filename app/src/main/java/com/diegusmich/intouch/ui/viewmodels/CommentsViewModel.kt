package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.data.domain.Comment
import kotlinx.coroutines.launch

class CommentsViewModel : StateViewModel() {

    private val _comments = MutableLiveData<List<Comment>>(mutableListOf())
    val comments : LiveData<List<Comment>> = _comments

    fun onLoadComments() = viewModelScope.launch {

    }

    fun onAddNewComment() = viewModelScope.launch {

    }

    fun onRemoveComment() = viewModelScope.launch {

    }
}