package com.diegusmich.intouch.utils

import com.diegusmich.intouch.R
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthEmailException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

object FirebaseExceptionUtil {
    fun localize(e : Exception) : Int {
        return when (e) {
            is FirebaseAuthUserCollisionException -> R.string.firebaseAuthCollision
            is FirebaseAuthInvalidCredentialsException -> R.string.firebaseAuthInvalidCredentials
            is FirebaseAuthInvalidUserException -> R.string.firebaseAuthInvalidUserException
            is FirebaseAuthEmailException -> R.string.firebaseAuthEmailException
            is FirebaseTooManyRequestsException -> R.string.firebaseTooManyRequestsException
            is FirebaseNetworkException -> R.string.firebaseNetworkException
            else -> R.string.firebaseDefaultExceptionMessage
        }
    }
}