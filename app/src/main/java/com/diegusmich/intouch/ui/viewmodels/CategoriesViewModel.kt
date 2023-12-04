package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.repository.CategoryRepository
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class CategoriesViewModel : StateViewModel() {

    private val _categories = MutableLiveData<List<Category>?>(null)
    val categories: LiveData<List<Category>?> = _categories

    fun loadCategories() = viewModelScope.launch {
        if (categories.value != null) {
            updateState(_LOADING, false)
            return@launch
        }

        updateState(_LOADING, true)

        try {
            _categories.value = CategoryRepository.getAll()
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception) {
            val messageId =
                if (e.cause is UnknownHostException || e.cause is ConnectException)
                    R.string.firebaseNetworkException
                else
                    R.string.firebaseDefaultExceptionMessage

            updateState(_ERROR, messageId)
        }
    }
}