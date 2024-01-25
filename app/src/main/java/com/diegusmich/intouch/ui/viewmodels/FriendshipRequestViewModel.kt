package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.data.domain.FriendshipRequest
import com.diegusmich.intouch.data.repository.FriendshipRepository
import com.diegusmich.intouch.network.NetworkStateObserver
import com.diegusmich.intouch.providers.AuthProvider
import com.diegusmich.intouch.providers.NetworkProvider
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FriendshipRequestViewModel : StateViewModel() {

    private val _friendshipRequest = MutableLiveData<List<FriendshipRequest>>(listOf())
    val friendshipRequests: LiveData<List<FriendshipRequest>> = _friendshipRequest

    private var onLoadFriendshipRequestJob: Job? = null
    private var onFriendshipUpdateListener: ListenerRegistration? = null

    private var onNetworkAvailable: NetworkStateObserver? = null

    fun listenForFriendshipRequestsUpdate() {
        AuthProvider.authUser()?.uid?.let {
            onFriendshipUpdateListener =
                FriendshipRepository.runOnFriendshipRequestsUpdate { snapshots, _ ->
                    if(snapshots?.size() != _friendshipRequest.value?.size) {
                        if (snapshots?.isEmpty == true)
                            _friendshipRequest.value = mutableListOf()
                        else
                            onLoadFriendshipRequests()
                    }
                }
        }
    }

    private fun onLoadFriendshipRequests() {
        onLoadFriendshipRequestJob?.cancel()
        onLoadFriendshipRequestJob = viewModelScope.launch {
            try {
                updateState(_LOADING, true)
                _friendshipRequest.value = FriendshipRepository.getAllFriendshipWithAuthUser()
                updateState(_CONTENT_LOADED, true)
                onNetworkAvailable?.let {
                    NetworkProvider.removeOnNetworkAvailableObserver(it)
                }
            } catch (e: Exception) {
                updateState(_ERROR, FirebaseExceptionUtil.localize(e))
                onNetworkAvailable = NetworkStateObserver {
                    onLoadFriendshipRequests()
                }
                NetworkProvider.addOnNetworkAvailableObserver(onNetworkAvailable!!)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        onFriendshipUpdateListener?.remove()
    }
}