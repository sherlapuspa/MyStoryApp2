package com.dicoding.mystoryapp.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.dicoding.mystoryapp.R
import com.dicoding.mystoryapp.databinding.ActivitySelectLocationBinding
import com.dicoding.mystoryapp.helper.LocationConverter
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class SelectLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivitySelectLocationBinding
    private lateinit var fusedLocation: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySelectLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setButtonState(binding.btnCurrentLocation, false)
        setButtonState(binding.btnSelectLocation, false)
        setActions()

        val fragmentMap =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        fragmentMap.getMapAsync(this)

        fusedLocation = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true

        getCurrentLocation()

        mMap.setOnMapClickListener {
            selectedLocation = it
            val markerOpt = MarkerOptions()
            markerOpt.position(it)

            markerOpt.title(LocationConverter.getStringAddressDetail(it, this))
            mMap.clear()
            val location = CameraUpdateFactory.newLatLngZoom(
                it, 15f
            )
            mMap.animateCamera(location)
            mMap.addMarker(markerOpt)
            setButtonState(binding.btnSelectLocation, true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                getCurrentLocation()
            }

            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                getCurrentLocation()
            }

            else -> {
            }
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this, permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getCurrentLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) && checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            fusedLocation.lastLocation.addOnSuccessListener { loc: Location? ->
                if (loc != null) {
                    currentLocationLatLng = LatLng(
                        loc.latitude, loc.longitude
                    )
                    setButtonState(binding.btnCurrentLocation, true)
                    mMap.isMyLocationEnabled = true
                    showStartMarker(loc)
                } else {
                    setButtonState(binding.btnCurrentLocation, false)
                    Toast.makeText(
                        this@SelectLocationActivity,
                        resources.getString(R.string.locationNotFound),
                        Toast.LENGTH_SHORT
                    ).show()
                    mMap.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(defaultLocation(), ZOOM_DEFAULT)
                    )
                    mMap.isMyLocationEnabled = false
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun setButtonState(button: Button, isEnabled: Boolean) {
        if (isEnabled) {
            button.setBackgroundResource(R.drawable.rounded_black_bg)
            button.setTextColor(ContextCompat.getColor(this, R.color.white))

            button.isEnabled = true
        } else {
            button.setBackgroundResource(R.drawable.rounded_gray_bg)
            button.setTextColor(ContextCompat.getColor(this, R.color.wordColor))

            button.isEnabled = false
        }
    }

    private fun setActions() {
        binding.btnCurrentLocation.setOnClickListener {
            showAlertDialog(currentLocationLatLng)
        }

        binding.btnSelectLocation.setOnClickListener {
            showAlertDialog(selectedLocation)
        }
    }

    private fun showAlertDialog(latlng: LatLng?) {
        val address = LocationConverter.getStringAddressDetail(latlng, this)
        val builder = AlertDialog.Builder(this)
        val alert = builder.create()
        builder.setTitle(resources.getString(R.string.useThisLocation)).setMessage(address)
            .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                returnLocationResult(address, latlng)
            }.setNegativeButton(resources.getString(R.string.no)) { _, _ ->
                alert.cancel()
            }.show()
    }

    private fun returnLocationResult(address: String, latlng: LatLng?) {
        val resultIntent = Intent()
        resultIntent.putExtra("address", address)
        resultIntent.putExtra("lat", latlng?.latitude)
        resultIntent.putExtra("lng", latlng?.longitude)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    private fun defaultLocation() = LatLng(-6.2088, 106.8456)

    private fun showStartMarker(location: Location) {
        val startLocation = LatLng(location.latitude, location.longitude)
        mMap.addMarker(
            MarkerOptions().position(startLocation).title(getString(R.string.start_point))
        )
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLocation, ZOOM_DEFAULT))
    }

    companion object {
        var currentLocationLatLng: LatLng? = null
        var selectedLocation: LatLng? = null
        const val ZOOM_DEFAULT = 15.0f
    }
}