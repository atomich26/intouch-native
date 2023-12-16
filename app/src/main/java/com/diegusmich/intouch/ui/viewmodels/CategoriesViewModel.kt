package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.model.Category
import com.diegusmich.intouch.data.repository.CategoryRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CategoriesViewModel : StateViewModel() {

    private val _categories = MutableLiveData<List<Category>>(mutableListOf())
    val categories: LiveData<List<Category>> = _categories

    fun onLoadCategories(): Job = viewModelScope.launch {

        if (categories.value?.isNotEmpty() == true)
            return@launch updateState(_LOADING, false)

        updateState(_LOADING, true)

        with(CategoryRepository.getAll()) {
            if (this.isEmpty()){
                updateState(_ERROR, R.string.unable_to_update_error)
            }
            else {
                _categories.value = this
                updateState(_CONTENT_LOADED, true)
            }
        }
    }
}