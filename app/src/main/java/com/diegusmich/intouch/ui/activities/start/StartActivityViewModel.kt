package com.diegusmich.intouch.ui.activities.start

import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.response.CategoryListResponse
import com.diegusmich.intouch.network.NetworkStateObserver
import com.diegusmich.intouch.ui.state.StateViewModel
import com.diegusmich.intouch.ui.state.UiState
import com.diegusmich.intouch.utils.NetworkUtil
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class StartActivityViewModel : StateViewModel() {

    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories get() = _categories.asStateFlow()

    private val _checkedCategories = MutableStateFlow<List<String>>(listOf())
    val checkedCategories get() = _checkedCategories.asStateFlow()

    private var retryOnNetworkAvailable = NetworkStateObserver{
        loadCategories()
    }

    init{
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            if (categories.value != null)
                return@launch

            updateState(UiState.LOADING)

            Firebase.functions.getHttpsCallable("categories-list").call()
                .addOnSuccessListener { result ->
                    _categories.value = CategoryListResponse.parse(result.data!!)
                    updateState(UiState.CONTENT_LOADED)

                    NetworkUtil.removeOnNetworkAvailableObserver(retryOnNetworkAvailable)
                }
                .addOnFailureListener {
                    if(it.cause is UnknownHostException || it.cause is ConnectException){
                        _errorMessage = R.string.firebaseNetworkException
                        retryOnNetworkAvailable = NetworkStateObserver {
                            loadCategories()
                        }
                        NetworkUtil.addOnNetworkAvailableObserver(this@StartActivityViewModel.retryOnNetworkAvailable)
                    }
                    else
                        _errorMessage =   R.string.firebaseDefaultExceptionMessage

                    updateState(UiState.ERROR)
                }
        }
    }

    fun onUpdateCheckedCategories(checkedIds : List<String>){
        _checkedCategories.update { checkedIds }
    }

    fun saveCategories(){
        viewModelScope.launch {
            updateState(UiState.LOADING)

            Firebase.functions.getHttpsCallable("users-preferences")
                .call(mapOf("preferences" to checkedCategories.value))
                .addOnSuccessListener {
                    updateState(UiState.PREFERENCES_SAVED)
                }
                .addOnFailureListener{
                    _errorMessage = if(it.cause is UnknownHostException || it.cause is ConnectException)
                        R.string.firebaseNetworkException
                     else
                        R.string.firebaseDefaultExceptionMessage

                    updateState(UiState.ERROR)
                }
        }
    }

    override fun onCleared() {
        super.onCleared()
        NetworkUtil.removeOnNetworkAvailableObserver(retryOnNetworkAvailable)
    }
}