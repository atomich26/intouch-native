package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diegusmich.intouch.data.dto.UserDTO

class UserActivityViewModel : StateViewModel() {

    private val _userDAOProfile = MutableLiveData<UserDTO>()
    val userDAOProfile : LiveData<UserDTO> = _userDAOProfile

}