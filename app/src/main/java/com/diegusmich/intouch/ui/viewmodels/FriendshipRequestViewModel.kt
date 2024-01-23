package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.data.domain.FriendshipRequest
import com.diegusmich.intouch.data.repository.FriendshipRepository
import com.diegusmich.intouch.network.NetworkStateObserver
import com.diegusmich.intouch.providers.NetworkProvider
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FriendshipRequestViewModel : StateViewModel() {

    private val _friendshipRequest = MutableLiveData<List<FriendshipRequest>>(listOf())
    val friendshipRequests: LiveData<List<FriendshipRequest>> = _friendshipRequest

    private var onLoadFriendshipRequestJob: Job? = null
    private var onFriendshipUpdateListener =
        FriendshipRepository.runOnFriendshipUpdate { _, _ ->
            onLoadFriendshipRequests()
        }
    private var onNetworkAvailable: NetworkStateObserver? = null

    private fun onLoadFriendshipRequests() {
        onLoadFriendshipRequestJob?.cancel()
        onLoadFriendshipRequestJob = viewModelScope.launch {
            try {
                _friendshipRequest.value = FriendshipRepository.getAllFriendshipRequests()
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
        onFriendshipUpdateListener.remove()
    }
}