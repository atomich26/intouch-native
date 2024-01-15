package com.diegusmich.intouch.providers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase

object AuthProvider {
    private val auth = Firebase.auth

    fun authUser() = auth.currentUser

    fun logout(callback: (() -> Unit)? = null){
        auth.signOut()
        callback?.invoke()
    }

    fun login(email: String, password: String): Task<AuthResult> = auth.signInWithEmailAndPassword(email, password)

    fun sendResetPasswordEmail(email: String) = auth.sendPasswordResetEmail(email)

    fun upsertAccount(data: Map<String, Any?>) = Firebase.functions.getHttpsCallable("user-upsert").call(data)

}