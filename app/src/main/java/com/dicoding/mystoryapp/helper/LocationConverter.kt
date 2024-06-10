package com.dicoding.mystoryapp.helper

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.dicoding.mystoryapp.R
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.lang.StringBuilder

class LocationConverter {
    companion object {
        fun toLatlng(lat: Double?, lng: Double?): LatLng? {
            println("trial")
            return if (lat != null && lng != null) {
                LatLng(lat, lng)

            } else null
        }

        fun getStringAddressDetail(
            latlng: LatLng?, context: Context
        ): String {
            var addressDetailed = context.getString(R.string.no_location_found)

            try {
                if (latlng != null) {
                    val address: Address?
                    val geoCoder = Geocoder(context)
                    val list: List<Address> = geoCoder.getFromLocationName(
                        "${latlng.latitude},${latlng.longitude}", 1
                    ) as List<Address>
                    address = if (list.isNotEmpty()) list[0] else null

                    if (address != null) {
                        val local = address.locality
                        val area = address.adminArea
                        val country = address.countryName

                        addressDetailed = address.getAddressLine(0)
                            ?: if (local != null && area != null && country != null) {
                                StringBuilder(local).append(", $area").append(", $country")
                                    .toString()
                            } else if (area != null && country != null) {
                                StringBuilder(area).append(", $country").toString()
                            } else country ?: context.getString(R.string.unknown_location)
                    }

                }
            } catch (e: Exception) {
                Log.d("ERROR", "ERROR: $e")
            }
            return addressDetailed
        }
    }
}