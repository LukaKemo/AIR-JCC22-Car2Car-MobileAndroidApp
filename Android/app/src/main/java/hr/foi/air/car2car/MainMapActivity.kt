package hr.foi.air.car2car

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.ViewModelProvider

import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomnavigation.BottomNavigationView
import fragments.MainMapFragment
import fragments.NotificationsFragment
import fragments.SettingsFragment
import hr.foi.air.car2car.MQTT.MqttConnectionImpl
import hr.foi.air.car2car.Notifications.NotificationActivity
import hr.foi.air.car2car.Notifications.NotificationViewModel

class MainMapActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var appMap : GoogleMap
    var cars = HashMap<Int, Car>()

    private var markers = mutableListOf<Marker>()
    lateinit var mRefreshThread: RefreshThread
    private lateinit var viewModel: MqttViewModel
    private var mqttConnection: MqttConnectionImpl? = null
    private val mapManager = MapHandler()
    private val mqttData = MutableLiveData<ArrayList<NotificationViewModel>>()
    private val mainMapFragment = MainMapFragment()
    private val settingsFragment = SettingsFragment()
    private val notificationsFragment = NotificationsFragment()
    private lateinit var bottomNav : BottomNavigationView
    private val viewModelStoreOwner = MainViewModelStoreOwner()


    //private lateinit var googleMap : GoogleMap
    //private lateinit var client : MqttAndroidClient
    var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_map)

        replaceFragment(mainMapFragment)
        bottomNav = findViewById(R.id.bottomNav)

        bottomNav.setOnItemSelectedListener {
            when(it.itemId){
                R.id.mainMap -> replaceFragment(mainMapFragment)
                R.id.notifications -> replaceFragment(notificationsFragment)
                R.id.settings -> replaceFragment(settingsFragment)
            }
            true
        }


        val button: Button = findViewById(R.id.round_button_notifications)
        button.setOnClickListener {
            val intent = Intent(this, NotificationActivity::class.java)
            startActivity(intent)
        }

        viewModel = MqttViewModel.getInstance()
        Log.d("TAG", "Hash code Main: " + viewModel.hashCode());
        viewModel.data.observe(this, Observer {
            Log.d("DATA", "Data changed main: ${it.toString()}")
        })
        val newConnection = MqttConnectionImpl()
        newConnection.connectToMqtt(cars)
        mqttConnection = newConnection
        mapManager.setupMap(this)

    }

    override fun onDestroy() {
        super.onDestroy()
        mRefreshThread.interrupt()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mapManager.onMapReady(googleMap)
    }

    private fun refreshMarkers(cars: HashMap<Int, Car>) {
        appMap = mapManager.getMap()
        markers.forEach { marker ->
            marker.remove()
        }
        markers.clear()

        val markerView = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater)
            .inflate(R.layout.marker_layout, null)
        val cardView = markerView.findViewById<CardView>(R.id.markerCardView)

        cars.values.forEach { car ->
            val latLong = car.location
            val bitmap = Bitmap.createScaledBitmap(viewToBitmap(cardView)!!, cardView.width, cardView.height, false)
            val markerIcon = BitmapDescriptorFactory.fromBitmap(bitmap)
            val marker = appMap.addMarker(MarkerOptions().position(latLong).icon(markerIcon))
            if (marker != null) {
                markers.add(marker)
            }
        }
    }

    //bottom navBar
    private  fun replaceFragment(fragment: Fragment){
        if (fragment != null) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.commitNow()
        }
    }

    private fun viewToBitmap(view : View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }


    inner class RefreshThread : Thread() {
        private val mHandler = Handler()
        private var mapIsBeingScrolled = false
        override fun run() {
            while (!isInterrupted) {
                if (!mapIsBeingScrolled){
                    mHandler.post { refreshMarkers(cars) }
                    try {
                        sleep(500)
                    } catch (e: InterruptedException) {
                        interrupt()
                    }
                }
            }


        }
        fun setMapIsBeingScrolled(mapIsBeingScrolled: Boolean) {
            this.mapIsBeingScrolled = mapIsBeingScrolled
        }
        fun getMapState(): Boolean {
            return this.mapIsBeingScrolled
        }
    }
}