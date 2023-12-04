package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.repository.CategoryRepository
import com.diegusmich.intouch.network.NetworkStateObserver
import com.diegusmich.intouch.service.NetworkService
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class StartActivityViewModel : StateViewModel() {

    private val _PREFERENCES_SAVED = MutableLiveData(false)
    val PREFERENCES_SAVED: LiveData<Boolean> = _PREFERENCES_SAVED

    private val _categories = MutableLiveData<List<Category>?>(null)
    val categories: LiveData<List<Category>?> = _categories

    private val _checkedCategories = MutableLiveData<List<String>>(listOf())
    val checkedCategories: LiveData<List<String>> = _checkedCategories

    private var retryOnNetworkAvailable = NetworkStateObserver {
        loadCategories()
    }

    init {
        loadCategories()
    }

    private fun loadCategories(): Job = viewModelScope.launch {
        if (categories.value != null)
            return@launch

        updateState(_LOADING, true)

        try {
            _categories.value = CategoryRepository.getAll()
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception) {
            if (e.cause is UnknownHostException || e.cause is ConnectException) {
                updateState(_ERROR, R.string.firebaseNetworkException)

                retryOnNetworkAvailable = NetworkStateObserver {
                    loadCategories()
                }

                NetworkService.addOnNetworkAvailableObserver(this@StartActivityViewModel.retryOnNetworkAvailable)
            } else
                updateState(_ERROR, R.string.firebaseDefaultExceptionMessage)
        }
    }

    fun onUpdateCheckedCategories(checkedIds: List<String>) {
        _checkedCategories.value = checkedIds
    }

    fun saveCategories() {
        updateState(_LOADING, true)

        if (checkedCategories.value?.size == categories.value?.size) {
            return updateState(_PREFERENCES_SAVED, true)
        }

        Firebase.functions.getHttpsCallable("users-preferences")
            .call(mapOf("preferences" to checkedCategories.value))
            .addOnSuccessListener {
                updateState(_PREFERENCES_SAVED, true)
            }
            .addOnFailureListener {
                val messageId =
                    if (it.cause is UnknownHostException || it.cause is ConnectException)
                        R.string.firebaseNetworkException
                    else
                        R.string.firebaseDefaultExceptionMessage

                updateState(_ERROR, messageId)
            }
    }

    override fun onCleared() {
        super.onCleared()
        NetworkService.removeOnNetworkAvailableObserver(retryOnNetworkAvailable)
    }
}