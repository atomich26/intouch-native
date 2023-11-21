package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.response.CategoryListResponse
import com.diegusmich.intouch.ui.state.StateViewModel
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import java.net.ConnectException
import java.net.UnknownHostException

class CategoriesViewModel : StateViewModel() {

    private val _categories = MutableLiveData<List<Category>?>(null)
    val categories : LiveData<List<Category>?> = _categories

    init{
        loadCategories()
    }

    fun loadCategories() {
        if (categories.value != null) {
            return updateState(_LOADING, false)
        }

       updateState(_LOADING, true)

        Firebase.functions.getHttpsCallable("categories-list").call()
            .addOnSuccessListener { result ->
                _categories.value = CategoryListResponse.parse(result.data!!)
                updateState(_CONTENT_LOADED, true)
            }
            .addOnFailureListener {
                val messageId =
                    if (it.cause is UnknownHostException || it.cause is ConnectException)
                        R.string.firebaseNetworkException
                    else
                        R.string.firebaseDefaultExceptionMessage

                updateState(_ERROR, messageId )
            }
    }
}