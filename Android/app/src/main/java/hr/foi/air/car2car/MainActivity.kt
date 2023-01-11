package hr.foi.air.car2car

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    //creating variable for mapFragment
    private lateinit var mapFragment : SupportMapFragment
    private lateinit var googleMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupMap()
    }

    private fun setupMap(){
        mapFragment = supportFragmentManager.findFragmentById(R.id.mainMap) as SupportMapFragment
        mapFragment !!.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val latLongCar1 = LatLng(46.308007, 16.358387)
        val latLongCar2 = LatLng(46.307881, 16.358226)

        val markerView = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.marker_layout, null)
        val cardView = markerView.findViewById<CardView>(R.id.markerCardView)

        val bitmapCar1 = Bitmap.createScaledBitmap(viewToBitmap(cardView)!!, cardView.width, cardView.height, false)
        val markerIconCar1 = BitmapDescriptorFactory.fromBitmap(bitmapCar1)
        googleMap.addMarker(MarkerOptions().position(latLongCar1).icon(markerIconCar1))

        val bitmapCar2 = Bitmap.createScaledBitmap(viewToBitmap(cardView)!!, cardView.width, cardView.height, false)
        val markerIconCar2 = BitmapDescriptorFactory.fromBitmap(bitmapCar2)
        googleMap.addMarker(MarkerOptions().position(latLongCar2).icon(markerIconCar2))

        val cameraPosition = CameraPosition.Builder().target(latLongCar1).zoom(18.0f).build()
        val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
        //googleMap.moveCamera(cameraUpdate)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLongCar1, 19f))
    }

    private fun viewToBitmap(view : View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
}