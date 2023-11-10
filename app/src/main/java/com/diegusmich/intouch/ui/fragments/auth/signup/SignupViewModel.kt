package com.diegusmich.intouch.ui.fragments.auth.signup

import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.response.FormErrorsResponse
import com.diegusmich.intouch.ui.state.StateViewModel
import com.diegusmich.intouch.ui.state.UiState
import com.diegusmich.intouch.ui.views.form.FormInputLayout.FormInputState
import com.diegusmich.intouch.utils.ErrorUtil
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.FirebaseFunctionsException
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

class SignupViewModel : StateViewModel() {

    private val _email = MutableStateFlow(FormInputState<String>())
    val email get() = _email.asStateFlow()

    private val _name = MutableStateFlow(FormInputState<String>())
    val name get() = _name.asStateFlow()

    private val _username = MutableStateFlow(FormInputState<String>())
    val username get() = _username.asStateFlow()

    private val _password = MutableStateFlow(FormInputState<String>())
    val password = _password.asStateFlow()

    private val _birthdate = MutableStateFlow(FormInputState<Date>())
    val birthdate get() = _birthdate.asStateFlow()


    fun onUpdateEmail(emailText: String) {
        _email.update {
            FormInputState(inputValue = emailText, isValid = emailText.isNotBlank())
        }
    }

    fun onUpdatePassword(passwordText: String) {
        _password.update {
            FormInputState(inputValue = passwordText, isValid = passwordText.isNotBlank())
        }
    }

    fun onUpdateName(nameText: String) {
        _name.update {
            FormInputState(inputValue = nameText, isValid = nameText.isNotBlank())
        }
    }

    fun onUpdateUsername(usernameText: String) {
        _username.update {
            FormInputState(inputValue = usernameText, isValid = usernameText.isNotBlank())
        }
    }

    fun onUpdateBirthdate(timestamp: Long) {
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
            updateState(UiState.ERROR)
            return@launch
        }
        updateState(UiState.LOADING)

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
                    updateState(UiState.LOGGED)
                }
                .addOnFailureListener {
                    _errorMessage = FirebaseExceptionUtil.localize(it)
                    updateState(UiState.ERROR)
                }

        } catch (e: FirebaseFunctionsException) {
            when (e.code) {
                FirebaseFunctionsException.Code.INTERNAL -> {
                    _errorMessage = R.string.firebaseNetworkException
                    updateState(UiState.ERROR)
                }

                FirebaseFunctionsException.Code.INVALID_ARGUMENT -> {
                    if (e.details != null) {
                        val errors = FormErrorsResponse.parse(e.details!!)

                        if (errors.containsKey("name"))
                            _name.update { it.copy(error = ErrorUtil.getMessage(errors["name"].toString())) }

                        if (errors.containsKey("username"))
                            _username.update { it.copy(error = ErrorUtil.getMessage(errors["username"].toString())) }

                        if (errors.containsKey("email"))
                            _email.update { it.copy(error = ErrorUtil.getMessage(errors["email"].toString())) }

                        if (errors.containsKey("password"))
                            _password.update { it.copy(error = ErrorUtil.getMessage(errors["password"].toString())) }

                        if (errors.containsKey("birthdate"))
                            _birthdate.update { it.copy(error = ErrorUtil.getMessage(errors["birthdate"].toString())) }

                        updateState(UiState.FORM_VALIDATION_ERROR)
                    }
                }
                else -> {
                    _errorMessage = R.string.firebaseDefaultExceptionMessage
                    updateState(UiState.ERROR)
                }
            }
        }
    }
}