package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Category
import com.diegusmich.intouch.data.repository.CategoryRepository
import com.diegusmich.intouch.data.repository.UserRepository
import com.diegusmich.intouch.network.NetworkStateObserver
import com.diegusmich.intouch.providers.NetworkProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.UnknownHostException

class EditPreferencesActivityViewModel : StateViewModel() {

    private val _PREFERENCES_SAVED = MutableLiveData(false)
    val PREFERENCES_SAVED: LiveData<Boolean> = _PREFERENCES_SAVED

    private val _EDITED = MutableLiveData(false)
    val EDITED : LiveData<Boolean> = _EDITED

    private val _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean> = _editMode

    private val _categories = MutableLiveData<List<Category>>(mutableListOf())
    val categories: LiveData<List<Category>> = _categories

    private val _currentCheckedCategories = MutableLiveData<List<String>>(mutableListOf())

    private val _checkedCategories = MutableLiveData<List<String>>(listOf())
    val checkedCategories: LiveData<List<String>> = _checkedCategories

    private var retryOnNetworkAvailable = NetworkStateObserver {
        onLoadCategories()
    }

    init {
        onLoadCategories()
    }

    fun onSetEditMode(state: Boolean) {
        _editMode.value = state
    }

    private fun onLoadCategories(): Job = viewModelScope.launch {
        if (categories.value?.isEmpty() == false)
            return@launch

        updateState(_LOADING, true)

        try {
            with(CategoryRepository.getAll()) {
                _checkedCategories.value = CategoryRepository.getAuthUserCategories().map {
                    it.id
                }
                _currentCheckedCategories.value = _checkedCategories.value

                if (this.isEmpty()) {
                    updateState(_ERROR, R.string.unable_to_update_error)
                    NetworkProvider.addOnNetworkAvailableObserver(this@EditPreferencesActivityViewModel.retryOnNetworkAvailable)
                } else {
                    _categories.value = this
                    NetworkProvider.removeOnNetworkAvailableObserver(retryOnNetworkAvailable)
                    updateState(_CONTENT_LOADED, true)
                }
            }
        } catch (e: Exception) {
            updateState(_ERROR, R.string.firebaseNetworkException)
        }
    }

    fun onUpdateCheckedCategories(checkedIds: List<String>) {
        _checkedCategories.value = checkedIds
        _EDITED.value = _checkedCategories.value != _currentCheckedCategories.value && _checkedCategories.value?.isNotEmpty() == true
    }

    fun onSaveCategories(all: Boolean = false) = viewModelScope.launch {

        if(_EDITED.value == false && !all)
            return@launch

        updateState(_LOADING, true)

        try {
            val data = if(all)
                _categories.value?.map { it.id }
            else
                checkedCategories.value

            UserRepository.saveAuthUserPreferences(data!!)
            updateState(_PREFERENCES_SAVED, true)
        }catch (e : Exception){
            val messageId =
                if (e.cause is UnknownHostException || e.cause is ConnectException)
                    R.string.firebaseNetworkException
                else
                    R.string.firebaseDefaultExceptionMessage

            updateState(_ERROR, messageId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        NetworkProvider.removeOnNetworkAvailableObserver(retryOnNetworkAvailable)
    }
}