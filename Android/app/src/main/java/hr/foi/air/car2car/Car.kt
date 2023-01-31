package hr.foi.air.car2car

import com.google.android.gms.maps.model.LatLng

class Car(val id: Int, var location: LatLng)
{
    constructor(id: Int, lat: Double, lng: Double) : this(id, LatLng(lat, lng))


    fun updateLocation(newLocation: LatLng) {
        location = newLocation
    }
}