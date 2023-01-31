package hr.foi.air.car2car

import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng

class MapHandler() : OnMapReadyCallback {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var appMap: GoogleMap
    private lateinit var mainActivity: MainMapActivity


    override fun onMapReady(googleMap: GoogleMap) {
        appMap = googleMap
        val firstCar = mainActivity.cars.values.firstOrNull()
        if (firstCar != null) {
            val latLongFirstCar = firstCar.location
            appMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLongFirstCar, 16f))
        } else {
            val initialLocation = LatLng(46.308148, 16.358248)
            appMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 16f))
        }
        mainActivity.mRefreshThread = mainActivity.RefreshThread()
        appMap.setOnCameraIdleListener {
            if (mainActivity.mRefreshThread.getMapState()) {
                mainActivity.mRefreshThread.setMapIsBeingScrolled(false)
                Log.d("SCROLL", "MAP STOPPED BEING SCROLLED")
            }
            appMap.setOnCameraMoveStartedListener {
                mainActivity.mRefreshThread.setMapIsBeingScrolled(true)
                Log.d("SCROLL", "MAP IS BEING SCROLLED")
            }
        }
        mainActivity.mRefreshThread.start()

    }

    fun setupMap(activity: MainMapActivity) {
        mainActivity = activity
        mapFragment =
            mainActivity.supportFragmentManager.findFragmentById(R.id.mainMap) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun getMap(): GoogleMap {
        return this.appMap
    }
}