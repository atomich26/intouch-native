package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.repository.PostRepository
import com.diegusmich.intouch.data.repository.UserRepository
import com.diegusmich.intouch.service.NetworkService
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class ProfileViewModel : StateViewModel() {

    private val _userProfile: MutableLiveData<User.Profile?> = MutableLiveData(null)
    val userProfile: LiveData<User.Profile?> = _userProfile

    private val _archivedPosts: MutableLiveData<List<Post.ArchivePreview>> = MutableLiveData(mutableListOf())
    val archivedPosts: LiveData<List<Post.ArchivePreview>> = _archivedPosts

    fun onLogout() = Firebase.auth.signOut()

    fun onLoadData(userId: String?, isRefreshing: Boolean = false) = viewModelScope.launch{
        if (userId == null)
            return@launch updateState(_ERROR, R.string.firebaseAuthInvalidUserException)

        updateState(_LOADING, true)

        try {
            _userProfile.value = UserRepository.userProfile(userId)
            _archivedPosts.value = PostRepository.archived(userId)

            updateState(_CONTENT_LOADED, true)
        }
        catch (e: FirebaseFirestoreException) {
            val messageId =
                if (isRefreshing)
                    R.string.unable_to_update_error
                else
                    R.string.firebaseFirestoreException
            updateState(_ERROR, messageId)
        }
    }

    private fun onLoadUserProfile(userId: String) = viewModelScope.launch {

    }

    private fun onLoadArchivedPosts(userId: String) = viewModelScope.launch {

    }

    fun onLoadAuthData(isRefreshing: Boolean = false) = onLoadData(Firebase.auth.currentUser?.uid!!, isRefreshing)
}