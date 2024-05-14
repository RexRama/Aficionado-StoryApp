package com.rexrama.aficionado.ui.main.map

import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.rexrama.aficionado.R
import com.rexrama.aficionado.data.remote.response.ListStoryItem
import com.rexrama.aficionado.databinding.ActivityMapsBinding
import com.rexrama.aficionado.utils.UserPreference
import com.rexrama.aficionado.utils.Util
import com.rexrama.aficionado.utils.ViewModelFactory
import com.rexrama.aficionado.utils.dataStore
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        setViewModel(pref)
        setUpMap()
        setBackButton()


    }

    private fun setViewModel(pref: UserPreference) {
        val viewModelFactory = ViewModelFactory(pref, this)
        viewModel = ViewModelProvider(this, viewModelFactory)[MapsViewModel::class.java]

        viewModel.getUser().observe(this) {user ->
            val token = "Bearer ${user.token}"
            viewModel.getAllStories(token)
        }

        viewModel.location.observe(this) { stories ->
            storyLocation(stories)
        }
    }

    private fun storyLocation(stories: List<ListStoryItem>) {
        stories.forEach {story ->
            if (story.lat != null && story.lon != null) {
                val latLng = LatLng(story.lat, story.lon)
                val getAddress = getAddressLocation(story.lat, story.lon)
                val markerOptions = MarkerOptions()
                    .position(latLng)
                    .title("Story by " + story.name)
                    .snippet(getAddress)
                mMap.addMarker(
                    markerOptions
                )
            }
        }

        val indonesia = LatLng(-0.7893, 118.9213)
        val zoomLevel = 3.5f
        val positionCamera = CameraPosition.Builder().target(indonesia).zoom(zoomLevel).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(positionCamera))

    }

    @Suppress("DEPRECATION")
    private fun getAddressLocation(lat: Double, lon: Double): String? {
        var addressLocation: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                addressLocation = list[0].getAddressLine(0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return addressLocation
    }


    private fun setUpMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        setMapStyle()
        setMyLocation()
        // Add a marker in Sydney and move the camera

    }

    private fun setMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            Toast.makeText(this, "Location service is not enabled", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMapStyle() {
        try {
            val isSuccess =
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))
            if (!isSuccess) {
                Log.e(TAG, "Parsing style failed!")
                Toast.makeText(this@MapsActivity, "Parsing style failed!", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Style not found!", exception)
            Toast.makeText(this@MapsActivity, "Style not Found!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setBackButton() {
        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Util().toHome(this@MapsActivity)
                finish()
            }
        }
        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}

