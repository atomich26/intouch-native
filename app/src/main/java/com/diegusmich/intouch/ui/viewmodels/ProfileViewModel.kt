package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Category
import com.diegusmich.intouch.data.domain.Friendship
import com.diegusmich.intouch.data.domain.Post
import com.diegusmich.intouch.data.repository.PostRepository
import com.diegusmich.intouch.data.repository.UserRepository
import com.diegusmich.intouch.providers.AuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ProfileViewModel : StateViewModel() {

    private val _id: MutableLiveData<String?> = MutableLiveData(null)
    val id: LiveData<String?> = _id

    private val _isAuth: MutableLiveData<Boolean> = MutableLiveData(false)
    val isAuth: LiveData<Boolean> = _isAuth

    private val _username: MutableLiveData<String?> = MutableLiveData(null)
    val username: LiveData<String?> = _username

    private val _name: MutableLiveData<String?> = MutableLiveData(null)
    val name: LiveData<String?> = _name

    private val _biography: MutableLiveData<String?> = MutableLiveData(null)
    val biography: LiveData<String?> = _biography

    private val _image: MutableLiveData<String?> = MutableLiveData(null)
    val image: LiveData<String?> = _image

    private val _friends: MutableLiveData<Int> = MutableLiveData(0)
    val friends: LiveData<Int> = _friends

    private val _eventsCreated: MutableLiveData<Int> = MutableLiveData(0)
    val eventsCreated: LiveData<Int> = _eventsCreated

    private val _eventsJoined: MutableLiveData<Int> = MutableLiveData(0)
    val eventsJoined: LiveData<Int> = _eventsJoined

    private val _friendship: MutableLiveData<Friendship?> = MutableLiveData(null)
    val friendship: LiveData<Friendship?> = _friendship

    private val _preferences: MutableLiveData<List<Category>> = MutableLiveData(mutableListOf())
    val preferences: LiveData<List<Category>> = _preferences

    private val _archivedPosts: MutableLiveData<List<Post.ArchivePreview>> =
        MutableLiveData(mutableListOf())
    val archivedPosts: LiveData<List<Post.ArchivePreview>> = _archivedPosts

    private var onUpdateUserListener : ListenerRegistration? = null
    private var onUpdateArchivePostListener : ListenerRegistration? = null

    //Il flag non è necessario se la libreria è fatta bene. A quanto pare Firebase non lo è.
    fun onLogout() = AuthProvider.logout()

    fun onLoadUserData(userId: String?, isRefreshing: Boolean = false) : Job = viewModelScope.launch {
        if (userId == null)
            return@launch updateState(_ERROR, R.string.firebaseAuthInvalidUserException)

        updateState(_LOADING, true)

        try {
            UserRepository.userProfile(userId)?.let {
                _id.value = it.id
                _isAuth.value = it.isAuth
                _username.value = it.username
                _name.value = it.name
                _biography.value = it.biography
                _image.value = it.img
                _friends.value = it.friends
                _eventsCreated.value = it.created
                _eventsJoined.value = it.joined
                _friendship.value = it.friendship
                _preferences.value = it.preferences
            }

            if(onUpdateUserListener == null && isAuth.value!!)
                onUpdateUserListener = UserRepository.runOnDocumentUpdate(userId){
                    onLoadUserData(userId, true)
                }
            _archivedPosts.value = PostRepository.archived(userId)
            updateState(_CONTENT_LOADED, true)
        } catch (e: FirebaseFirestoreException) {
            val messageId =
                if (isRefreshing)
                    R.string.unable_to_update_error
                else
                    R.string.firebaseNetworkException
            updateState(_ERROR, messageId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        onUpdateUserListener?.remove()
    }
}