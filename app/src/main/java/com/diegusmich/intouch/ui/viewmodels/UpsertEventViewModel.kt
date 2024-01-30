package com.diegusmich.intouch.ui.viewmodels

import android.content.Context
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.Category
import com.diegusmich.intouch.data.domain.Event
import com.diegusmich.intouch.data.repository.CategoryRepository
import com.diegusmich.intouch.data.repository.EventRepository
import com.diegusmich.intouch.data.response.FormErrorsCallableResponse
import com.diegusmich.intouch.network.NetworkStateObserver
import com.diegusmich.intouch.providers.CloudImageProvider
import com.diegusmich.intouch.providers.NetworkProvider
import com.diegusmich.intouch.providers.UserLocationProvider
import com.diegusmich.intouch.ui.views.form.FormInputLayout
import com.diegusmich.intouch.utils.ErrorUtil
import com.diegusmich.intouch.utils.FileUtil
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.FirebaseException
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.Calendar
import java.util.Date
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

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

    private val _EVENT_CREATED = MutableLiveData(false)
    val EVENT_CREATED: LiveData<Boolean> = _EVENT_CREATED

    private val _EVENT_NOT_EXISTS = MutableLiveData(false)
    val EVENT_NOT_EXISTS: LiveData<Boolean> = _EVENT_NOT_EXISTS

    private val _LOCATION_ADDED = MutableLiveData(false)
    val LOCATION_ADDED: LiveData<Boolean> = _LOCATION_ADDED

    private val _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean> = _editMode

    private var _eventId: String? = null

    private var _newEventId: String? = null
    val newEventId get() = _newEventId

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
            FormInputLayout.FormInputState<Int>(
                inputName = AVAILABLE_FIELD_FORM
            )
        )
    val available: LiveData<FormInputLayout.FormInputState<Int>> = _available

    private val _coverRef =
        MutableLiveData(FormInputLayout.FormInputState<StorageReference>(inputName = COVER_FIELD_FORM))
    val cover: LiveData<FormInputLayout.FormInputState<StorageReference>> = _coverRef

    private val _coverFile = MutableLiveData<File?>(null)
    val coverFile: LiveData<File?> = _coverFile

    private var _currentCoverImage = MutableLiveData<StorageReference?>(null)
    val currentCoverImage: LiveData<StorageReference?> = _currentCoverImage

    private val _category =
        MutableLiveData(
            FormInputLayout.FormInputState<Int>(
                inputName = CATEGORY_FIELD_FORM
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
            inputValue = Date()
        )
    )
    val startAt: LiveData<FormInputLayout.FormInputState<Date>> = _startAt

    private val _endAt = MutableLiveData(
        FormInputLayout.FormInputState<Date>(
            inputName = END_FIELD_FORM
        )
    )

    val endAt: LiveData<FormInputLayout.FormInputState<Date>> = _endAt

    private val _geo =
        MutableLiveData(FormInputLayout.FormInputState<Location>(inputName = GEO_FIELD_FORM))
    val geo: LiveData<FormInputLayout.FormInputState<Location>> = _geo

    private val _categories = MutableLiveData<List<Category>?>(null)
    val categories: LiveData<List<Category>?> = _categories

    private var eventCurrentData: Event.Full? = null
    private var requestFormData = mutableMapOf<String, Any>()

    private var onNetworkAvailableJob = NetworkStateObserver {
        onLoadData()
    }

    fun setEditMode(eventId: String?) {
        _eventId = eventId
        onLoadData()
    }

    private fun onLoadData(): Job = viewModelScope.launch {
        updateState(_LOADING, true)

        try {
            if (_categories.value.isNullOrEmpty()) {
                _categories.value = CategoryRepository.getAll()
                onUpdateCategory(0)
            }

            if(editMode.value == false){
                val getCurrentPos = viewModelScope.async {
                    UserLocationProvider.getCurrentLocation()
                }
                onUpdateLocation(getCurrentPos.await(), false)
            }

            _eventId?.let {
                eventCurrentData = EventRepository.event(_eventId!!)
                if (eventCurrentData == null) {
                    updateState(_EVENT_NOT_EXISTS, true)
                } else
                    eventCurrentData?.let {
                        _editMode.value = true
                        onUpdateName(it.name)
                        onUpdateDescription(it.description)
                        onUpdateCity(it.city)
                        onUpdateAddress(it.address)
                        onUpdateLocation(it.geo)
                        onUpdateRestricted(it.restricted)
                        setCurrentCoverImage(it.cover)
                        onUpdateDate(_startAt, timestamp = it.startAt.time, currentDate = it.startAt)
                        it.endAt?.let{ it0 ->
                            onUpdateDate(_endAt, timestamp = it0.time, currentDate = it0)
                        }
                        onUpdateCategory(categories.value!!.indexOfFirst { cat -> cat.id == it.categoryInfo.id })

                    }
            }
            updateState(_CONTENT_LOADED, true)
            NetworkProvider.removeOnNetworkAvailableObserver(onNetworkAvailableJob)

        } catch (e: Exception) {
            NetworkProvider.addOnNetworkAvailableObserver(onNetworkAvailableJob)
            updateState(_ERROR, R.string.firebaseNetworkException)
        }
    }

    fun onUpdateStartAt(timestamp: Long? = null, minutes: Int? = null, hours: Int? = null){
        onUpdateDate(_startAt, timestamp, minutes, hours)
    }

    fun onUpdateEndAt(timestamp: Long? = null, minutes: Int? = null, hours: Int? = null){
        onUpdateDate(_endAt, timestamp, minutes, hours)
    }

    private fun onUpdateDate(input: MutableLiveData<FormInputLayout.FormInputState<Date>>, timestamp: Long? = null, newMinutes: Int? = null, newHours: Int? = null, currentDate : Date? = null) {
        input.apply {
            val newDate = Calendar.getInstance().apply {
                time = Date(timestamp ?: value?.inputValue?.time?: Date().time)
                set(Calendar.MINUTE, newMinutes ?: value?.inputValue?.minutes ?: Date().minutes)
                set(Calendar.HOUR, newHours?: value?.inputValue?.hours ?: Date().hours)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            value = value?.copy(inputValue = newDate.time, error = null)

            if (newDate.time != currentDate)
                requestFormData[value?.inputName!!] = value?.inputValue?.time!!
            else
                requestFormData.remove(value?.inputName)
        }
    }

    fun onUpdateCoverImage(ctx: Context, image: File) = viewModelScope.launch {
        try {
            _coverFile.value = FileUtil.compressImage(ctx, image, 512_000)
            val fileRef = CloudImageProvider.EVENTS.newFileRef()
            _coverRef.apply {
                requestFormData[value?.inputName!!] = fileRef.name
                value = value?.copy(inputValue = fileRef, error = null)
            }
        } catch (e: Exception) {
            updateState(_ERROR, R.string.compressed_image_failed)
        }
    }

    private fun setCurrentCoverImage(path: String) {
        _currentCoverImage.value = CloudImageProvider.EVENTS.imageRef(path)
    }

    fun onUpdateCategory(pos: Int) {
        _category.apply {
            val selected = _categories.value?.get(pos)
            if (selected?.id != eventCurrentData?.categoryInfo?.id)
                requestFormData[value?.inputName!!] = selected?.id ?: ""
            else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = pos, error = null)
        }
    }

    fun onUpdateLocation(location: Location, showMessage: Boolean = true) {
        _geo.apply {
            if (location != eventCurrentData?.geo) {
                requestFormData[value?.inputName!!] = listOf(location.latitude, location.longitude)
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = location, error = null)

            if (_editMode.value == false && showMessage)
                updateState(_LOCATION_ADDED, true)
        }
    }

    fun onUpdateAvailable(newValue: String) {
        _available.apply {
            val parsed = try{ newValue.toInt() } catch (e: Exception){ null }

            if(parsed != eventCurrentData?.available)
                requestFormData[value?.inputName!!] = parsed ?: 0
            else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = parsed, error = null)
        }
    }

    fun onUpdateRestricted(restrictedValue: Boolean) {
        _restricted.apply {
            if (restrictedValue != eventCurrentData?.restricted) {
                requestFormData[value?.inputName!!] = restrictedValue
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = restrictedValue, error = null)
        }
    }

    fun onUpdateName(nameText: String) {
        _name.apply {
            if (nameText != eventCurrentData?.name) {
                requestFormData[value?.inputName!!] = nameText
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = nameText, error = null)
        }
    }

    fun onUpdateDescription(descriptionText: String) {
        _description.apply {
            if (descriptionText != eventCurrentData?.description) {
                requestFormData[value?.inputName!!] = descriptionText
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = descriptionText, error = null)
        }
    }

    fun onUpdateCity(cityText: String) {
        _city.apply {
            if (cityText != eventCurrentData?.city) {
                requestFormData[value?.inputName!!] = cityText
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = cityText, error = null)
        }
    }

    fun onUpdateAddress(addressText: String) {
        _address.apply {
            if (addressText != eventCurrentData?.address) {
                requestFormData[value?.inputName!!] = addressText
            } else
                requestFormData.remove(value?.inputName)

            value = value?.copy(inputValue = addressText, error = null)
        }
    }

    fun onUpsertEvent() = viewModelScope.launch {

        if (requestFormData.isEmpty() && editMode.value!!)
            return@launch updateState(_ERROR, R.string.event_already_updated)

        if (editMode.value == false && _coverFile.value == null)
            return@launch updateState(_ERROR, R.string.event_cover_empty)

        updateState(_LOADING, true)
        try {
            if (editMode.value == true)
                requestFormData["eventId"] = _eventId.toString()

            val upsertJob = viewModelScope.async {
                EventRepository.upsert(requestFormData)
            }

            _newEventId = upsertJob.await().data.toString()

            val uploadImagejob = viewModelScope.async {
                _coverFile.value?.let {
                    CloudImageProvider.EVENTS.uploadImage(it, _coverRef.value?.inputValue!!)
                }
            }
            requestFormData.clear()
            uploadImagejob.await()

            if (editMode.value == true)
                updateState(_EDITED, true)
            else
                updateState(_EVENT_CREATED, true)

        } catch (e: FirebaseFunctionsException) {
            when (e.code) {
                FirebaseFunctionsException.Code.INTERNAL -> {
                    val messageId =
                        if (e.cause is UnknownHostException || e.cause is ConnectException)
                            R.string.firebaseNetworkException
                        else
                            R.string.internal_error

                    updateState(_ERROR, messageId)
                }

                FirebaseFunctionsException.Code.INVALID_ARGUMENT -> {

                    if (e.details != null) {
                        val errors = FormErrorsCallableResponse(e.details).errors

                        if (errors.containsKey("name"))
                            _name.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["name"].toString()))
                            }

                        if (errors.containsKey("description"))
                            _description.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["description"].toString()))
                            }

                        if (errors.containsKey("city"))
                            _city.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["city"].toString()))
                            }

                        if (errors.containsKey("address"))
                            _address.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["address"].toString()))
                            }

                        if (errors.containsKey("startAt"))
                            _startAt.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["startAt"].toString()))
                            }

                        if (errors.containsKey("endAt"))
                            _endAt.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["endAt"].toString()))
                            }

                        if (errors.containsKey("categoryId"))
                            _category.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["categoryId"].toString()))
                            }

                        if (errors.containsKey("available"))
                            _available.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["available"].toString()))
                            }
                    }
                    updateState(_ERROR, ErrorUtil.getMessage(e.message.toString()))
                }

                else -> updateState(_ERROR, ErrorUtil.getMessage(e.message.toString()))
            }
        } catch (e: StorageException) {
            updateState(_ERROR, R.string.storageImageException)
        } catch (e: FirebaseException) {
            updateState(_ERROR, FirebaseExceptionUtil.localize(e))
        }
    }
}