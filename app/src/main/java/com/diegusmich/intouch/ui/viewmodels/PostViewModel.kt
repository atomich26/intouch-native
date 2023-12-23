package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diegusmich.intouch.data.domain.Post

class PostViewModel : StateViewModel(){

    private val _post : MutableLiveData<Post?> = MutableLiveData(null)
    val post : LiveData<Post?> = _post

}