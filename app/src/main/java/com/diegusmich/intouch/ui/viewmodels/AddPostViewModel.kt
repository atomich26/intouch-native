package com.diegusmich.intouch.ui.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.repository.PostRepository
import com.diegusmich.intouch.ui.views.form.FormInputLayout
import com.diegusmich.intouch.utils.FileUtil
import kotlinx.coroutines.launch
import java.io.File

private val DESCRIPTION_FORM_FIELD = "description"

class AddPostViewModel : StateViewModel() {

    private val _POST_ADDED = MutableLiveData(false)
    val POST_ADDED : LiveData<Boolean> = _POST_ADDED

    private var _eventId : String? = null

    private val _images = MutableLiveData<List<File>>(listOf())
    val images : LiveData<List<File>> = _images

    private val _description = MutableLiveData(FormInputLayout.FormInputState<String>(inputName = DESCRIPTION_FORM_FIELD))
    val description : LiveData<FormInputLayout.FormInputState<String>> = _description

    fun onLoadImages(list : List<File>){
        _images.apply {
            value = list
        }
    }

    fun setEventId(eventId : String?){
        _eventId = eventId
    }

    fun onUpdateDescription(descriptionText : String){
        _description.apply {
            value = value?.copy(inputValue = descriptionText)
        }
    }

    fun onAddPost(ctx: Context) = viewModelScope.launch {
       try{
           _eventId?.let{
               updateState(_LOADING, true)
               val compressed = _images.value!!.map { file -> FileUtil.compressImage(ctx, file, 512_000) }
               PostRepository.createPost(it, _description.value?.inputValue, compressed)
               updateState(_POST_ADDED, true)
           }
       }catch (e : Exception){
           updateState(_ERROR, R.string.firebaseNetworkException)
       }
    }
}