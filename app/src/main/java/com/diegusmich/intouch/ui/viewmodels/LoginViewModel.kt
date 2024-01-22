package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.providers.AuthProvider
import com.diegusmich.intouch.ui.views.form.FormInputLayout.FormInputState
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.FirebaseException
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class LoginViewModel : StateViewModel() {

    private val _RECOVERY_EMAIL_SENT = MutableLiveData(false)
    val RECOVERY_EMAIL_SENT: LiveData<Boolean> = _RECOVERY_EMAIL_SENT

    private val _LOGGED = MutableLiveData(false)
    val LOGGED: LiveData<Boolean> = _LOGGED

    private val _email = MutableLiveData(FormInputState<String>())
    val email: LiveData<FormInputState<String>> = _email

    private val _password = MutableLiveData(FormInputState<String>())
    val password: LiveData<FormInputState<String>> = _password

    fun onPerformLogin() = viewModelScope.launch {
        updateState(_LOADING, true)

        try {
            AuthProvider.login(
                email.value?.inputValue.toString(),
                password.value?.inputValue.toString()
            )
            updateState(_LOGGED, true)
        } catch (e: Exception) {
            val messageId = when(e){
                is IllegalArgumentException -> R.string.form_blank_fields
                is FirebaseException -> FirebaseExceptionUtil.localize(e)
                else -> R.string.internal_error
            }
            updateState(_ERROR, messageId)
        }
    }

    fun onUpdateEmail(emailText: String) {
        _email.apply {
            value = value?.copy(inputValue = emailText, isValid = emailText.isNotBlank())
        }
    }

    fun onUpdatePassword(passwordText: String) {
        _password.apply {
            value = value?.copy(inputValue = passwordText, isValid = passwordText.isNotBlank())
        }
    }

    fun onSendResetPasswordEmail() = viewModelScope.launch {
        updateState(_LOADING, true)

        try {
            AuthProvider.sendResetPasswordEmail(email.value?.inputValue.toString())
            updateState(_RECOVERY_EMAIL_SENT, true)
        } catch (e: Exception) {
            updateState(_ERROR, R.string.invalid_email)
        }
    }
}