package com.diegusmich.intouch.ui.activities

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
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

    private lateinit var supportMapFragment : SupportMapFragment

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

        intent.extras?.getParcelable<Location>(LOCATION_ARG)?.let{
            viewModel.setLocation(it)
        }

        binding.selectPositionButton.setOnClickListener {
            val resultData = Intent().apply {
                putExtra(LOCATION_RESULT_KEY, viewModel.selectedLocation.value!!)
            }
            setResult(Activity.RESULT_OK, resultData)
            finish()
        }

        supportMapFragment = supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

        Toast.makeText(this, getString(R.string.map_picker_message), Toast.LENGTH_LONG).show()

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
        googleMap.apply {
            uiSettings.setAllGesturesEnabled(false)
            moveCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getDefaultLocation().toLatLng(), 5f))
            setOnMapClickListener {
                addMarkerOnMap(it)
            }
        }

        lifecycleScope.launch {
            val selected = viewModel.selectedLocation.value ?: viewModel.getCurrentPosition().await()
            selected.toLatLng().let {
                addMarkerOnMap(it)
                delay(1000)
                googleMap.apply {
                    moveCamera(CameraUpdateFactory.newLatLngZoom(it,13f))
                    animateCamera(CameraUpdateFactory.zoomIn())
                    animateCamera(CameraUpdateFactory.zoomTo(15f), 2000, object: GoogleMap.CancelableCallback{
                        override fun onCancel() {
                           return
                        }

                        override fun onFinish() {
                            uiSettings.setAllGesturesEnabled(true)
                        }

                    })
                }
            }
        }
    }

    private fun addMarkerOnMap(latLng: LatLng){
        viewModel.setLocation(latLng)
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(latLng))
    }

    companion object{
        const val LOCATION_RESULT_KEY = "locationResult"
        const val LOCATION_ARG = "locationArg"
    }
}