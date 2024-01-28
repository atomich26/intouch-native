package com.diegusmich.intouch.ui.viewmodels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.diegusmich.intouch.providers.UserLocationProvider
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async

class MapsPickerViewModel : StateViewModel() {

    private val _selectedLocation = MutableLiveData<Location?>(null)
    val selectedLocation: LiveData<Location?> = _selectedLocation

    fun getCurrentPosition() = viewModelScope.async {
        UserLocationProvider.getCurrentLocation()
    }

    fun getDefaultLocation() = UserLocationProvider.defaultLocation

    fun setLocation(location: Location){
        _selectedLocation.value = location
    }

    fun setLocation(location: LatLng) {
        _selectedLocation.value = Location("").apply {
            latitude = location.latitude
            longitude = location.longitude
        }
    }
}