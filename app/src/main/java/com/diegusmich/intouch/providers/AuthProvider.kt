package com.diegusmich.intouch.providers

import com.diegusmich.intouch.data.repository.UserRepository
import com.diegusmich.intouch.exceptions.InitFCMTokenFailedException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object AuthProvider {
    private val auth = Firebase.auth

    fun authUser() = auth.currentUser

    suspend fun logout() = withContext(Dispatchers.IO) {
        NotificationProvider.deleteTokenforAuthUser()
        auth.signOut()
    }

    suspend fun login(email: String, password: String) = withContext(Dispatchers.IO) {
        auth.signInWithEmailAndPassword(email, password).await()
        assignFCMToken()
    }

    suspend fun sendResetPasswordEmail(email: String) = withContext(Dispatchers.IO) {
        auth.sendPasswordResetEmail(email).await()
    }

    suspend fun signUp(data: Map<String, Any?>) = withContext(Dispatchers.IO) {
        UserRepository.upsertUser(data)
        login(data["email"].toString(), data["password"].toString())
    }

    private suspend fun assignFCMToken() = withContext(Dispatchers.IO) {
        try {
            val token = NotificationProvider.getToken()
            val userTokenRef =
                Firebase.firestore.collection("messaging").document(authUser()?.uid!!)

            Firebase.firestore.runTransaction {
                it.set(userTokenRef, mapOf("token" to token))
            }.addOnCompleteListener {
                if (!it.isSuccessful)
                    auth.signOut()
            }.await()
        } catch (e: Exception) {
            throw InitFCMTokenFailedException()
        }
    }
}