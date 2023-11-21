package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diegusmich.intouch.data.model.User
import com.diegusmich.intouch.ui.state.StateViewModel

class UserActivityViewModel : StateViewModel() {

    private val _userProfile = MutableLiveData<User>()
    val userProfile : LiveData<User> = _userProfile
}
