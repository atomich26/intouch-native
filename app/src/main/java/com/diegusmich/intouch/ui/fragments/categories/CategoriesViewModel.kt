package com.diegusmich.intouch.ui.fragments.categories

import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.response.CategoryListResponse
import com.diegusmich.intouch.ui.state.StateViewModel
import com.diegusmich.intouch.ui.state.UiState
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class CategoriesViewModel : StateViewModel() {

    private val _categories = MutableStateFlow<List<Category>?>(null)
    val categories get() = _categories.asStateFlow()

    init{
        loadCategories()
    }

    fun loadCategories() {

        viewModelScope.launch {
            updateState(UiState.LOADING)
            delay(5000)
            updateState(UiState.CONTENT_LOADED)
        }
        /*if (categories.value != null) {
            updateState(UiState.LOADING_COMPLETED)
            return
        }

        updateState(UiState.LOADING)

        Firebase.functions.getHttpsCallable("categories-list").call()
            .addOnSuccessListener { result ->
                _categories.value = CategoryListResponse.parse(result.data!!)
                updateState(UiState.CONTENT_LOADED)
            }
            .addOnFailureListener {
                _errorMessage =
                    if (it.cause is UnknownHostException || it.cause is ConnectException)
                        R.string.firebaseNetworkException
                    else
                        R.string.firebaseDefaultExceptionMessage

                updateState(UiState.ERROR)
            }*/
    }

    override fun onCleared() {
        super.onCleared()

    }
}