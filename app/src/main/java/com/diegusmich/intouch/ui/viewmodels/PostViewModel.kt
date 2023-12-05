package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.data.model.Post
import com.diegusmich.intouch.data.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel : StateViewModel(){

    private val _post : MutableLiveData<Post?> = MutableLiveData(null)
    val post : LiveData<Post?> = _post

}