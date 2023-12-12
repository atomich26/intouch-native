package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.repository.CategoryRepository
import com.diegusmich.intouch.network.NetworkStateObserver
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class CategoriesViewModel : StateViewModel() {

    private val _categories = MutableLiveData<List<Category>>(mutableListOf())
    val categories: LiveData<List<Category>> = _categories


    private var retryOnNetworkAvailable = NetworkStateObserver {
        loadCategories()
    }

    fun loadCategories() = viewModelScope.launch {
        if (categories.value?.isEmpty() == true) {
            updateState(_LOADING, false)
            return@launch
        }

        updateState(_LOADING, true)

        try {
            _categories.value = CategoryRepository.getAll()
            updateState(_CONTENT_LOADED, _categories.value!!.isNotEmpty())
        } catch (e: Exception) {
            val messageId =
                if (e is FirebaseNetworkException)
                    R.string.firebaseNetworkException
                    else
                    R.string.firebaseDefaultExceptionMessage

            updateState(_ERROR, messageId)
        }
    }
}