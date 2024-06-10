package com.dicoding.mystoryapp.ui

import android.content.ContentValues.TAG
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.UserPreferencesManager
import com.dicoding.mystoryapp.db.DetailPostStory
import com.dicoding.mystoryapp.databinding.ActivityMapsBinding
import com.dicoding.mystoryapp.helper.LocationConverter
import com.dicoding.mystoryapp.viewmodel.AuthSplashVM
import com.dicoding.mystoryapp.viewmodel.ListStoryVM
import com.dicoding.mystoryapp.viewmodel.ListVMFactory
import com.dicoding.mystoryapp.viewmodel.UserVMFactory
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private val boundBuilder = LatLngBounds.Builder()
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapsViewModel: ListStoryVM by lazy {
        ViewModelProvider(this, ListVMFactory(this))[ListStoryVM::class.java]
    }
    private val userpref by lazy {
        UserPreferencesManager.getInstance(dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragmentMap =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        fragmentMap.getMapAsync(this)

        val authSplashVM =
            ViewModelProvider(this, UserVMFactory(userpref))[AuthSplashVM::class.java]
        authSplashVM.getToken().observe(this) {
            mapsViewModel.getStories(it)
        }

        mapsViewModel.userStories.observe(this) {
            if (it != null && ::mMap.isInitialized) {
                setMarker(it)
            }
        }

        mapsViewModel.isLocating.observe(this) {
            if (it != "Stories fetched successfully") Toast.makeText(this, it, Toast.LENGTH_SHORT)
                .show()
        }

        mapsViewModel.isLoad.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoad: Boolean) {
        binding.progressBarMap.visibility = if (isLoad) View.VISIBLE else View.GONE
    }

    private fun setMarker(detail: List<DetailPostStory>) {
        if (!::mMap.isInitialized) {
            return
        }
        lateinit var locationZoom: LatLng
        detail.forEach {
            if (it.lat != null && it.lon != null) {
                val latLng = LatLng(it.lat, it.lon)
                val address = LocationConverter.getStringAddressDetail(latLng, this)
                val marker = mMap.addMarker(
                    MarkerOptions().position(latLng).title(it.name).snippet(address)
                )
                boundBuilder.include(latLng)
                marker?.tag = it

                locationZoom = latLng
            }
        }

        mMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                locationZoom, 5f
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true

        mapsViewModel.userStories.value?.let {
            setMarker(it)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private fun setMapStyle(mapsType: String) {
        if (mapsType == "standard") {
            try {
                val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.maps_standard
                    )
                )
                if (!success) {
                    Log.e(TAG, getString(R.string.style_parsing_failed))
                }
            } catch (exception: Resources.NotFoundException) {
                Log.e(TAG, getString(R.string.cant_find_style), exception)
            }
        } else {
            try {
                val success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.map_style
                    )
                )
                if (!success) {
                    Log.e(TAG, getString(R.string.style_parsing_failed))
                }
            } catch (exception: Resources.NotFoundException) {
                Log.e(TAG, getString(R.string.cant_find_style), exception)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.standardMaps -> {
                setMapStyle("standard")
                true
            }

            R.id.aubergineMaps -> {
                setMapStyle("aubergine")
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}