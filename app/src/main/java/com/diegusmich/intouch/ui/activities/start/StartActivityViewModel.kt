package com.diegusmich.intouch.ui.activities.start

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.response.CategoryListResponse
import com.diegusmich.intouch.network.NetworkStateObserver
import com.diegusmich.intouch.ui.state.StateViewModel
import com.diegusmich.intouch.service.NetworkService
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.net.ConnectException
import java.net.UnknownHostException

class StartActivityViewModel : StateViewModel() {

    private val _PREFERENCES_SAVED = MutableLiveData(false)
    val PREFERENCES_SAVED : LiveData<Boolean> = _PREFERENCES_SAVED

    private val _categories = MutableLiveData<List<Category>?>(null)
    val categories : LiveData<List<Category>?> = _categories

    private val _checkedCategories = MutableLiveData<List<String>>(listOf())
    val checkedCategories : LiveData<List<String>> = _checkedCategories

    private var retryOnNetworkAvailable = NetworkStateObserver {
        loadCategories()
    }

    init {
        loadCategories()
    }

    private fun loadCategories() {
        if (categories.value != null)
            return

       updateState(_LOADING, true)

        Firebase.functions.getHttpsCallable("categories-list").call()
            .addOnSuccessListener { result ->
                _categories.value = CategoryListResponse.parse(result.data!!)

                updateState(_CONTENT_LOADED, true)

                NetworkService.removeOnNetworkAvailableObserver(retryOnNetworkAvailable)
            }
            .addOnFailureListener {
                if (it.cause is UnknownHostException || it.cause is ConnectException) {
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