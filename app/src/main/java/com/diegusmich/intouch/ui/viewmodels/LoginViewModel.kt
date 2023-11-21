package com.diegusmich.intouch.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.ui.state.StateViewModel
import com.diegusmich.intouch.ui.views.form.FormInputLayout.FormInputState
import com.diegusmich.intouch.utils.FirebaseExceptionUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class LoginViewModel : StateViewModel() {

    private val _RECOVERY_EMAIL_SENT = MutableLiveData(false)
    val RECOVERY_EMAIL_SENT : LiveData<Boolean> = _RECOVERY_EMAIL_SENT

    private val _LOGGED = MutableLiveData(false)
    val LOGGED : LiveData<Boolean> = _LOGGED

    private val _email = MutableLiveData(FormInputState<String>())
    val email : LiveData<FormInputState<String>> = _email

    private val _password = MutableLiveData(FormInputState<String>())
    val password : LiveData<FormInputState<String>> = _password

    fun onPerformLogin() = viewModelScope.launch {
        updateState(_LOADING, true)

        try {
            Firebase.auth.signInWithEmailAndPassword(
                email.value?.inputValue.toString(),
                password.value?.inputValue.toString()
            )
                .addOnSuccessListener {
                    updateState(_LOGGED, true)
                }
                .addOnFailureListener {
                    updateState(_ERROR, FirebaseExceptionUtil.localize(it))
                }
        } catch (e: IllegalArgumentException) {
            updateState(_ERROR, R.string.form_blank_fields)
        }
    }

    fun onUpdateEmail(emailText: String) {
        _email.apply{
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

        Firebase.auth.sendPasswordResetEmail(email.value?.inputValue.toString())
            .addOnSuccessListener {
                updateState(_RECOVERY_EMAIL_SENT, true)
            }
            .addOnFailureListener {
                updateState(_ERROR, R.string.invalid_email )
            }
    }
}