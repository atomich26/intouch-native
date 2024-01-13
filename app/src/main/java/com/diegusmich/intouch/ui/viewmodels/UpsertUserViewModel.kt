package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.domain.User
import com.diegusmich.intouch.data.repository.UserRepository
import com.diegusmich.intouch.data.response.FormErrorsCallableResponse
import com.diegusmich.intouch.ui.views.form.FormInputLayout
import com.diegusmich.intouch.ui.views.form.FormInputLayout.FormInputState
import com.diegusmich.intouch.utils.ErrorUtil
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.Date

private const val EMAIL_FIELD_FORM: String = "email"
private const val NAME_FIELD_FORM: String = "name"
private const val USERNAME_FIELD_FORM: String = "username"
private const val BIOGRAPHY_FIELD_FORM: String = "biography"
private const val DISTANCE_LOCATION_FIELD_FORM: String = "distanceRange"
private const val PASSWORD_FIELD_FORM: String = "password"
private const val BIRTHDATE_FIELD_FORM: String = "birthdate"

class UpsertUserViewModel : StateViewModel() {

    private val _LOGGED = MutableLiveData(false)
    val LOGGED: LiveData<Boolean> = _LOGGED

    private val _RECOVERY_EMAIL_SENT = MutableLiveData(false)
    val RECOVERY_EMAIL_SENT: LiveData<Boolean> = _RECOVERY_EMAIL_SENT

    private val _EDITED = MutableLiveData(false)
    val EDITED: LiveData<Boolean> = _EDITED

    private val _editMode = MutableLiveData(false)
    val editMode: LiveData<Boolean> = _editMode

    private val _name = MutableLiveData(FormInputState<String>(inputName = NAME_FIELD_FORM))
    val name: LiveData<FormInputState<String>> = _name

    private val _email = MutableLiveData(FormInputState<String>(inputName = EMAIL_FIELD_FORM))
    val email : LiveData<FormInputState<String>> = _email

    private val _username = MutableLiveData(FormInputState<String>(inputName = USERNAME_FIELD_FORM))
    val username: LiveData<FormInputState<String>> = _username

    private val _biography =
        MutableLiveData(FormInputState<String>(inputName = BIOGRAPHY_FIELD_FORM))
    val biography: LiveData<FormInputState<String>> = _biography

    private val _distance =
        MutableLiveData(FormInputState(inputName = DISTANCE_LOCATION_FIELD_FORM, inputValue = 5))
    val distance: LiveData<FormInputState<Int>> = _distance

    private val _password = MutableLiveData(FormInputState<String>(inputName = PASSWORD_FIELD_FORM))
    val password: LiveData<FormInputState<String>> = _password

    private val _birthdate = MutableLiveData(FormInputState<Long>(inputName = BIRTHDATE_FIELD_FORM))
    val birthdate: LiveData<FormInputState<Long>> = _birthdate

    private var userCurrentData : User.Profile? = null
    private var requestFormData = mutableMapOf<String, Any?>()

    fun onLoadUserCurrentData() = viewModelScope.launch {
        updateState(_LOADING, true)

        try{
            userCurrentData = UserRepository.userProfile(Firebase.auth.currentUser?.uid!!)
            userCurrentData?.let {
                onUpdateName(it.name)
                onUpdateUsername(it.username)
                onUpdateBiography(it.biography)
                onUpdateBirthdate(it.birthdate.time)
                onUpdateDistanceRange(it.distanceRange.toFloat())
            }
            updateState(_CONTENT_LOADED, true)
        } catch (e: Exception){
            updateState(_ERROR, R.string.firebaseNetworkException)
        }
    }

    fun onActiveEditMode(state: Boolean) {
        _editMode.value = state
    }

    fun onUpdateEmail(emailText: String) {
       updateFormInput(_email, emailText, emailText.isNotBlank(), null)
    }

    fun onUpdatePassword(passwordText: String) {
        updateFormInput(_password, passwordText, passwordText.isNotBlank(), null)
    }

    private fun <T> updateFormInput(input: MutableLiveData<FormInputState<T>>, newValue: T, rule: Boolean, currentValue: T?){
        input.apply {
            value = value?.copy(inputValue = newValue, isValid = rule)
        }

        if(newValue?.equals(currentValue) == false){
            input.value?.inputName?.let {
                requestFormData.put(it, newValue)
            }
        }else
            requestFormData.remove(input.value?.inputName)
    }

    fun onUpdateBiography(biographyText: String) {
        updateFormInput(_biography, biographyText, biographyText.isNotBlank(), userCurrentData?.biography)
    }

    fun onUpdateDistanceRange(distanceRange: Float) {
        updateFormInput(_distance, distanceRange.toInt(), true, userCurrentData?.distanceRange)
    }

    fun onUpdateName(nameText: String) {
        updateFormInput(_name, nameText, nameText.isNotBlank(), userCurrentData?.name)
    }

    fun onUpdateUsername(usernameText: String) {
        updateFormInput(_username, usernameText, usernameText.isNotBlank(), userCurrentData?.username)
    }

    fun onUpdateBirthdate(timestamp: Long) {
        updateFormInput(_birthdate, timestamp, true, userCurrentData?.birthdate?.time)
        return
    }

    fun onSendResetPasswordEmail() = viewModelScope.launch {
        updateState(_LOADING, true)

        Firebase.auth.sendPasswordResetEmail(Firebase.auth.currentUser?.email ?: "")
            .addOnSuccessListener {
                updateState(_RECOVERY_EMAIL_SENT, true)
            }
            .addOnFailureListener {
                updateState(_ERROR, R.string.firebaseNetworkException)
            }
    }

    fun onUpsertProfile() = viewModelScope.launch {

        if(requestFormData.isEmpty() && editMode.value!!)
            return@launch updateState(_ERROR, R.string.values_already_updated)

        updateState(_LOADING, true)

        try {
            Firebase.functions.getHttpsCallable("users-upsert").call(requestFormData).await()

            if (editMode.value == false) {
                Firebase.auth.signInWithEmailAndPassword(
                    requestFormData[EMAIL_FIELD_FORM].toString(),
                    requestFormData[PASSWORD_FIELD_FORM].toString()
                )
                    .addOnSuccessListener {
                        updateState(_LOGGED, true)
                    }
                    .addOnFailureListener {
                        updateState(_ERROR, FirebaseExceptionUtil.localize(it))
                    }
            } else{
                onLoadUserCurrentData()
                updateState(_EDITED, true)
            }
            requestFormData.clear()
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

                        if (errors.containsKey("username"))
                            _username.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["username"].toString()))
                            }

                        if (errors.containsKey("email"))
                            _email.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["email"].toString()))
                            }

                        if (errors.containsKey("password"))
                            _password.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["password"].toString()))
                            }

                        if (errors.containsKey("birthdate"))
                            _birthdate.apply {
                                value =
                                    value?.copy(error = ErrorUtil.getMessage(errors["birthdate"].toString()))
                            }
                        updateState(_LOADING, false)
                    }
                }

                else -> {
                    updateState(_ERROR, R.string.firebaseDefaultExceptionMessage)
                }
            }
        }
    }
}