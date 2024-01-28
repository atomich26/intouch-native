package com.diegusmich.intouch.ui.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.diegusmich.intouch.R
import com.diegusmich.intouch.data.decorators.toLatLng
import com.diegusmich.intouch.databinding.ActivityMapsPickerBinding
import com.diegusmich.intouch.ui.viewmodels.MapsPickerViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MapsPickerActivity : AppCompatActivity(), OnMapReadyCallback {

    private var _binding : ActivityMapsPickerBinding? = null
    private val binding get() = _binding!!

    private val viewModel : MapsPickerViewModel by viewModels()

    private lateinit var googleMap : GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMapsPickerBinding.inflate(layoutInflater)

        binding.appBarLayout.materialToolbar.apply {
            title = getString(R.string.pick_location_title)
            navigationIcon = AppCompatResources.getDrawable(this@MapsPickerActivity, R.drawable.baseline_arrow_back_24)
            setNavigationOnClickListener{
                onBackPressedDispatcher.onBackPressed()
            }
        }
        setContentView(binding.root)

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        observeData()
    }

    private fun observeData(){
        viewModel.selectedLocation.observe(this){
            binding.selectPositionButton.visibility = if(it != null) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getDefaultLocation().toLatLng(), 5f))
        googleMap.setOnMapClickListener {
            addMarkerOnMap(it)
        }

        lifecycleScope.launch {
            viewModel.getCurrentPosition().await().toLatLng().let {
                addMarkerOnMap(it)
                delay(1500)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it,13f));
                googleMap.animateCamera(CameraUpdateFactory.zoomIn());
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, null);
            }
        }
    }

    private fun addMarkerOnMap(latLng: LatLng){
        viewModel.setLocation(latLng)
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(latLng))
    }
}