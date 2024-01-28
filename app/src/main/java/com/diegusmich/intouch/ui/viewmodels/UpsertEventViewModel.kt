package com.diegusmich.intouch.ui.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Category
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.repository.CategoryRepository
import com.diegusmich.intouch.data.repository.EventRepository
import com.diegusmich.intouch.network.NetworkStateObserver
import com.diegusmich.intouch.providers.NetworkProvider
import com.diegusmich.intouch.ui.views.form.FormInputLayout
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date

private const val NAME_FIELD_FORM: String = "name"
private const val DESCRIPTION_FIELD_FORM: String = "description"
private const val CITY_FIELD_FORM: String = "city"
private const val ADDRESS_FIELD_FORM: String = "address"
private const val GEO_FIELD_FORM: String = "geo"
private const val CATEGORY_FIELD_FORM: String = "categoryId"
private const val START_FIELD_FORM: String = "startAt"
private const val END_FIELD_FORM: String = "endAt"
private const val COVER_FIELD_FORM: String = "cover"
private const val RESTRICTED_FIELD_FORM: String = "restricted"
private const val AVAILABLE_FIELD_FORM: String = "available"

class UpsertEventViewModel : StateViewModel() {

    private val _EDITED = MutableLiveData(false)
    val EDITED: LiveData<Boolean> = _EDITED

    private val _EVENT_NOT_EXISTS = MutableLiveData(false)
    val EVENT_NOT_EXISTS: LiveData<Boolean> = _EVENT_NOT_EXISTS

    private val _LOCATION_ADDED = MutableLiveData(false)
    val LOCATION_ADDED: LiveData<Boolean> = _LOCATION_ADDED

    private val _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean> = _editMode

    private var _eventId: String? = null

    private val _name =
        MutableLiveData(FormInputLayout.FormInputState<String>(inputName = NAME_FIELD_FORM))
    val name: LiveData<FormInputLayout.FormInputState<String>> = _name

    private val _description =
        MutableLiveData(FormInputLayout.FormInputState<String>(inputName = DESCRIPTION_FIELD_FORM))
    val description: LiveData<FormInputLayout.FormInputState<String>> = _description

    private val _city =
        MutableLiveData(FormInputLayout.FormInputState<String>(inputName = CITY_FIELD_FORM))
    val city: LiveData<FormInputLayout.FormInputState<String>> = _city

    private val _address =
        MutableLiveData(FormInputLayout.FormInputState<String>(inputName = ADDRESS_FIELD_FORM))
    val address: LiveData<FormInputLayout.FormInputState<String>> = _address

    private val _available =
        MutableLiveData(
            FormInputLayout.FormInputState(
                inputName = AVAILABLE_FIELD_FORM,
                inputValue = 0
            )
        )
    val available: LiveData<FormInputLayout.FormInputState<Int>> = _available

    private val _cover =
        MutableLiveData(FormInputLayout.FormInputState<File>(inputName = COVER_FIELD_FORM))
    val cover: LiveData<FormInputLayout.FormInputState<File>> = _cover

    private val _category =
        MutableLiveData(
            FormInputLayout.FormInputState(
                inputName = CATEGORY_FIELD_FORM,
                inputValue = 0
            )
        )
    val category: LiveData<FormInputLayout.FormInputState<Int>> = _category

    private val _restricted = MutableLiveData(
        FormInputLayout.FormInputState(
            inputName = RESTRICTED_FIELD_FORM,
            inputValue = false
        )
    )
    val restricted: LiveData<FormInputLayout.FormInputState<Boolean>> = _restricted

    private val _startAt = MutableLiveData(
        FormInputLayout.FormInputState(
            inputName = START_FIELD_FORM,
            inputValue = Date().time
        )
    )
    val startAt: LiveData<FormInputLayout.FormInputState<Long>> = _startAt

    private val _endAt = MutableLiveData(
        FormInputLayout.FormInputState(
            inputName = END_FIELD_FORM,
            inputValue = Date().time
        )
    )
    val endAt: LiveData<FormInputLayout.FormInputState<Long>> = _endAt

    private val _geo =
        MutableLiveData(FormInputLayout.FormInputState<Location>(inputName = GEO_FIELD_FORM))
    val geo: LiveData<FormInputLayout.FormInputState<Location>> = _geo

    private var categories : List<Category>? = null
    private var eventCurrentData: Event.Full? = null
    private var requestFormData = mutableMapOf<String, Any?>()

    private var onNetworkAvailableJob = NetworkStateObserver {
        onLoadData()
    }

    fun setEditMode(eventId : String?){
        _eventId = eventId
        onLoadData()
    }

    fun onLoadData(): Job = viewModelScope.launch {
        updateState(_LOADING, true)

         try{
             val getCategoriesJob = async{
                 CategoryRepository.getAll()
             }
             val getCurrentEventData = async{
                 _eventId?.let{
                     EventRepository.event(_eventId!!)
                 }
             }

             categories = getCategoriesJob.await()
             eventCurrentData = getCurrentEventData.await()

             if(eventCurrentData == null){
                 return@launch updateState(_EVENT_NOT_EXISTS, true)
             }
             eventCurrentData?.let {
                 onUpdateName(it.name)
             }
             categories = CategoryRepository.getAll()
             updateState(_CONTENT_LOADED, true)
             NetworkProvider.removeOnNetworkAvailableObserver(onNetworkAvailableJob)

         } catch (e: Exception){
             NetworkProvider.addOnNetworkAvailableObserver(onNetworkAvailableJob)
             updateState(_ERROR, R.string.firebaseNetworkException)
         }
    }

    fun onUpdateCoverImage(image: File) {
        _cover.apply {
            if(image.name == eventCurrentData?.cover){
                requestFormData[value?.inputName!!] = image.name
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = image, error = null)
        }
    }

    fun onUpdateLocation(location: Location) {
        _geo.apply {
            if (location != eventCurrentData?.geo) {
                requestFormData[value?.inputName!!] = arrayOf(location.latitude, location.longitude)
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = location, error = null)
        }
    }

    fun onUpdateRestricted(restrictedValue: Boolean) {
        _restricted.apply {
            if (restrictedValue != eventCurrentData?.restricted) {
                requestFormData[value?.inputName!!] = restrictedValue
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = restrictedValue , error = null)
        }
    }

    fun onUpdateName(nameText: String) {
        _name.apply {
            if (nameText != eventCurrentData?.name) {
                requestFormData[value?.inputName!!] = nameText
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = nameText , error = null)
        }
    }

    fun onUpdateDescription(descriptionText: String) {
        _description.apply {
            if (descriptionText != eventCurrentData?.description) {
                requestFormData[value?.inputName!!] = description
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = descriptionText , error = null)
        }
    }

    fun onUpdateCity(cityText: String) {
        _city.apply {
            if (cityText != eventCurrentData?.description) {
                requestFormData[value?.inputName!!] = description
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = cityText , error = null)
        }
    }

    fun onUpdateAddress(addressText: String) {
        _address.apply {
            if (addressText != eventCurrentData?.description) {
                requestFormData[value?.inputName!!] = description
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = addressText , error = null)
        }
    }

    private fun upsertEvent() = viewModelScope.launch {

    }
}