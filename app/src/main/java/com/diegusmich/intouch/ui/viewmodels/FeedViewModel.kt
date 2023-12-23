package com.diegusmich.intouch.ui.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

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