package com.diegusmich.intouch.ui.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.data.dto.CategoryDTO
import com.diegusmich.intouch.data.repository.CategoryRepository
import com.google.firebase.firestore.Source
import com.google.firebase.functions.ktx.functions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FeedViewModel : StateViewModel() {
    // TODO: Implement the ViewModel

    fun load() = viewModelScope.launch {
        try {

        }
        catch (e : Exception){
            Log.println(Log.ERROR, "ERR", e.message.toString())
        }
    }
}