package com.diegusmich.intouch.ui.fragments.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.response.FormErrorsResponse
import com.diegusmich.intouch.ui.state.StateViewModel
import com.diegusmich.intouch.ui.views.form.FormInputLayout.FormInputState
import com.diegusmich.intouch.utils.ErrorUtil
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class SignupViewModel : StateViewModel() {

    private val _LOGGED = MutableLiveData(false)
    val LOGGED : LiveData<Boolean> = _LOGGED

    private val _email = MutableLiveData(FormInputState<String>())
    val email : LiveData<FormInputState<String>> = _email

    private val _name = MutableLiveData(FormInputState<String>())
    val name : LiveData<FormInputState<String>> = _name

    private val _username = MutableLiveData(FormInputState<String>())
    val username : LiveData<FormInputState<String>> = _username

    private val _password = MutableLiveData(FormInputState<String>())
    val password : LiveData<FormInputState<String>> = _password

    private val _birthdate = MutableLiveData(FormInputState<Date>())
    val birthdate : LiveData<FormInputState<Date>> = _birthdate

    fun onUpdateEmail(emailText: String) {
        _email.value = FormInputState(inputValue = emailText, isValid = emailText.isNotBlank())
    }

    fun onUpdatePassword(passwordText: String) {
        _password.value = FormInputState(inputValue = passwordText, isValid = passwordText.isNotBlank())
    }

    fun onUpdateName(nameText: String) {
        _name.value = FormInputState(inputValue = nameText, isValid = nameText.isNotBlank())
    }

    fun onUpdateUsername(usernameText: String) {
        _username.value = FormInputState(inputValue = usernameText, isValid = usernameText.isNotBlank())
    }

    fun onUpdateBirthdate(timestamp: Long) {
        _birthdate.value = FormInputState(inputValue = Date(timestamp), isValid = true)
    }

    fun onCreateAccount() = viewModelScope.launch {
        /*
         * DISCLAIMER: Questo non è il modo migliore per effettuare il login,
         * normalmente le API dovrebbero restituire un token d'accesso. La procedura di configurazione
         * su Google Cloud era un po' intricata quindi a scopo dimostrativo abbiamo scelto la via più semplice.
         */
        if (!name.value!!.isValid || !username.value!!.isValid || !email.value!!.isValid || !password.value!!.isValid || !birthdate.value!!.isValid) {
            updateState(_ERROR, R.string.form_blank_fields)
            return@launch
        }

        updateState(_LOADING, true)

        val requestData = mapOf(
            "name" to name.value?.inputValue,
           "username" to username.value?.inputValue,
            "email" to email.value?.inputValue,
            "password" to password.value?.inputValue,
            "birthdate" to birthdate.value?.inputValue?.time
        )
        try {
            Firebase.functions.getHttpsCallable("users-upsert").call(requestData).await()

            Firebase.auth.signInWithEmailAndPassword(requestData["email"].toString(), requestData["password"].toString())
                .addOnSuccessListener {
                    updateState(_LOGGED, true)
                }
                .addOnFailureListener {
                  updateState(_ERROR, FirebaseExceptionUtil.localize(it))
                }

        } catch (e: FirebaseFunctionsException) {
            when (e.code) {
                FirebaseFunctionsException.Code.INTERNAL -> {
                    updateState(_ERROR, R.string.firebaseNetworkException)
                }

                FirebaseFunctionsException.Code.INVALID_ARGUMENT -> {
                    if (e.details != null) {
                        val errors = FormErrorsResponse.parse(e.details!!)

                        if (errors.containsKey("name"))
                            _name.apply {
                                value = value?.copy(error = ErrorUtil.getMessage(errors["name"].toString()))
                            }

                        if (errors.containsKey("username"))
                            _username.apply {
                                value = value?.copy(error = ErrorUtil.getMessage(errors["username"].toString()))
                            }

                        if (errors.containsKey("email"))
                            _email.apply{
                                value = value?.copy(error = ErrorUtil.getMessage(errors["email"].toString()))
                            }

                        if (errors.containsKey("password"))
                            _password.apply {
                                value = value?.copy(error = ErrorUtil.getMessage(errors["password"].toString()))
                            }

                        if (errors.containsKey("birthdate"))
                            _birthdate.apply {
                                value = value?.copy(error = ErrorUtil.getMessage(errors["birthdate"].toString()))
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