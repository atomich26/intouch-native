package com.diegusmich.intouch.ui.fragments.auth.login

import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.state.StateViewModel
import com.diegusmich.intouch.ui.state.UiState
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

    fun onPerformLogin() = viewModelScope.launch {

       updateState(UiState.LOADING)

        try {
            Firebase.auth.signInWithEmailAndPassword(
                email.inputValue.toString(),
                password.inputValue.toString()
            )
                .addOnSuccessListener {
                    updateState(UiState.LOGGED)
                }
                .addOnFailureListener {
                    _errorMessage = FirebaseExceptionUtil.localize(it)
                    updateState(UiState.ERROR)
                }
        } catch (e: IllegalArgumentException) {
            _errorMessage = R.string.form_blank_fields
            updateState(UiState.ERROR)
        }
    }

    fun onUpdateEmail(emailText: String) {
        _email.update {
            it.copy(inputValue = emailText, isValid = emailText.isNotBlank())
        }
    }

    fun onUpdatePassword(passwordText: String) {
        _password.update {
            it.copy(inputValue = passwordText, isValid = passwordText.isNotBlank())
        }
    }

    fun onSendResetPasswordEmail() = viewModelScope.launch {
        updateState(UiState.LOADING)

        Firebase.auth.sendPasswordResetEmail(email.inputValue.toString())
            .addOnSuccessListener {
                updateState(UiState.RECOVERY_EMAIL_SENT)
            }
            .addOnFailureListener {
                _errorMessage = R.string.invalid_email
                updateState(UiState.ERROR)
            }
    }
}