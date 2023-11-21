package com.diegusmich.intouch.ui.state

import com.diegusmich.intouch.ui.state.StateViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileViewModel : StateViewModel() {

    fun onLogout() = Firebase.auth.signOut()

}