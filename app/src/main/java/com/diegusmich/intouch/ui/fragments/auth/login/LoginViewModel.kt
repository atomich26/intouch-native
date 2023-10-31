package com.diegusmich.intouch.ui.fragments.auth.login

import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.viewmodel.StateViewModel
import com.diegusmich.intouch.ui.viewmodel.UiEvent
import com.diegusmich.intouch.ui.views.form.FormInputLayout.FormInputState
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : StateViewModel() {

    private var _email = MutableStateFlow(FormInputState<String>())
    val email get() = _email.value

    private var _password = MutableStateFlow(FormInputState<String>())
    val password get() = _password.value

    private var _errorMessage: Int? = null
    val errorMessage get() = _errorMessage

    fun performLogin() = viewModelScope.launch {
        _uiEvent.update { UiEvent.LOADING }

        try {
            Firebase.auth.signInWithEmailAndPassword(
                email.inputValue.toString(),
                password.inputValue.toString()
            )
                .addOnSuccessListener {
                    _uiEvent.update { UiEvent.LOGGED }
                }
                .addOnFailureListener {
                    _errorMessage = FirebaseExceptionUtil.localize(it)
                    _uiEvent.update { UiEvent.ERROR }
                }
        } catch (e: IllegalArgumentException) {
            _errorMessage = R.string.form_blank_fields
            _uiEvent.update { UiEvent.ERROR }
        }
    }

    fun updateEmail(emailText: String) {
        _email.update {
            it.copy(inputValue = emailText, isValid = emailText.isNotBlank())
        }
    }

    fun updatePassword(passwordText: String) {
        _password.update {
            it.copy(inputValue = passwordText, isValid = passwordText.isNotBlank())
        }
    }

    fun sendResetPasswordEmail() = viewModelScope.launch {
        _uiEvent.update { UiEvent.LOADING }

        Firebase.auth.sendPasswordResetEmail(email.inputValue.toString())
            .addOnSuccessListener {
                _uiEvent.update { UiEvent.RECOVERY_EMAIL_SENT }
            }
            .addOnFailureListener {
                _errorMessage = R.string.invalid_email
                _uiEvent.update { UiEvent.ERROR }
            }
    }
}