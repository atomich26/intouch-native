package com.diegusmich.intouch.ui.fragments.auth.signup

import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.parser.FormErrorsParser
import com.diegusmich.intouch.ui.viewmodel.StateViewModel
import com.diegusmich.intouch.ui.viewmodel.UiEvent
import com.diegusmich.intouch.ui.views.form.FormInputLayout.FormInputState
import com.diegusmich.intouch.utils.ErrorUtil
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class SignupViewModel : StateViewModel() {

    private val _email = MutableStateFlow(FormInputState<String>())
    val email get() = _email

    private val _name = MutableStateFlow(FormInputState<String>())
    val name get() = _name

    private val _username = MutableStateFlow(FormInputState<String>())
    val username get() = _username

    private val _password = MutableStateFlow(FormInputState<String>())
    val password = _password

    private var _birthdate = MutableStateFlow(FormInputState<Date>())
    val birthdate get() = _birthdate

    private var _errorMessage: Int? = null
    val errorMessage get() = _errorMessage

    fun updateEmail(emailText: String) {
        _email.update {
            FormInputState(inputValue = emailText, isValid = emailText.isNotBlank())
        }
    }

    fun updatePassword(passwordText: String) {
        _password.update {
            FormInputState(inputValue = passwordText, isValid = passwordText.isNotBlank())
        }
    }

    fun updateName(nameText: String) {
        _name.update {
            FormInputState(inputValue = nameText, isValid = nameText.isNotBlank())
        }
    }

    fun updateUsername(usernameText: String) {
        _username.update {
            FormInputState(inputValue = usernameText, isValid = usernameText.isNotBlank())
        }
    }

    fun updateBirthdate(timestamp: Long) {
        _birthdate.update {
            FormInputState(inputValue = Date(timestamp), isValid = true)
        }
    }


    fun createAccount() = viewModelScope.launch {
        /*
         * DISCLAIMER: Questo non è il modo migliore per effettuare il login,
         * normalmente le API dovrebbero restituire un token d'accesso. La procedura di configurazione
         * su Google Cloud era un po' intricata quindi a scopo dimostrativo abbiamo scelto la via più semplice.
         */
        if (!name.value.isValid || !username.value.isValid || !email.value.isValid || !password.value.isValid || !birthdate.value.isValid) {

            _errorMessage = R.string.form_blank_fields
            _uiEvent.update { UiEvent.ERROR }
            return@launch
        }

        _uiEvent.update { UiEvent.LOADING }

        val requestData = mapOf(
            "name" to name.value.inputValue,
            "username" to username.value.inputValue,
            "email" to email.value.inputValue,
            "password" to password.value.inputValue,
            "birthdate" to birthdate.value.inputValue?.time
        )

        try {
            Firebase.functions.getHttpsCallable("users-upsert").call(requestData).await()

            Firebase.auth.signInWithEmailAndPassword(requestData["email"].toString(), requestData["password"].toString())
                .addOnSuccessListener {
                    _uiEvent.update { UiEvent.LOGGED }
                }
                .addOnFailureListener {
                    _errorMessage = FirebaseExceptionUtil.localize(it)
                    _uiEvent.update { UiEvent.ERROR }
                }

        } catch (e: FirebaseFunctionsException) {
            when (e.code) {
                FirebaseFunctionsException.Code.INTERNAL -> {
                    _errorMessage =
                        R.string.firebaseNetworkException
                    _uiEvent.update { UiEvent.ERROR }
                }

                FirebaseFunctionsException.Code.INVALID_ARGUMENT -> {

                    if (e.details != null) {
                        val tempErrors = FormErrorsParser.parse(e.details!!)

                        if (tempErrors.containsKey("name"))
                            _name.update { it.copy(error = ErrorUtil.getMessage(tempErrors["name"].toString())) }

                        if (tempErrors.containsKey("username"))
                            _username.update { it.copy(error = ErrorUtil.getMessage(tempErrors["username"].toString())) }

                        if (tempErrors.containsKey("email"))
                            _email.update { it.copy(error = ErrorUtil.getMessage(tempErrors["email"].toString())) }

                        if (tempErrors.containsKey("password"))
                            _password.update { it.copy(error = ErrorUtil.getMessage(tempErrors["password"].toString())) }

                        if (tempErrors.containsKey("birthdate"))
                            _birthdate.update { it.copy(error = ErrorUtil.getMessage(tempErrors["birthdate"].toString())) }

                        _uiEvent.update { UiEvent.FORM_VALIDATION_ERROR }
                    }
                }

                else -> {
                    _errorMessage = R.string.firebaseDefaultExceptionMessage
                    _uiEvent.update { UiEvent.ERROR }
                }
            }
        }
    }
}