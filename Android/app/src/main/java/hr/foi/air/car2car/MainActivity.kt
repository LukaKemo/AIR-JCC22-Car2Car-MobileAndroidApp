package hr.foi.air.car2car

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import java.nio.charset.StandardCharsets.UTF_8


class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    //creating variable for mapFragment
    private lateinit var mapFragment : SupportMapFragment
    //private lateinit var googleMap : GoogleMap
    //private lateinit var client : MqttAndroidClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupMap()
        println("Test")
        initializationMQTT()
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

        //val cameraPosition = CameraPosition.Builder().target(latLongCar1).zoom(18.0f).build()
        //val cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition)
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

    private fun initializationMQTT() {
    // create an MQTT client
        val host = "e5c6690c23234ff8b2e11e59d3fb82be.s2.eu.hivemq.cloud"
        val username = "JCC_RC_CommunicationFramework"
        val password = "@B1EfbPKD%Pp%kPG"

        // create an MQTT client
        val client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .buildBlocking()

        // connect to HiveMQ Cloud with TLS and username/pw
        client.connectWith()
            .simpleAuth()
            .username(username)
            .password(UTF_8.encode(password))
            .applySimpleAuth()
            .send()
        Log.d("mqtt","Connected successfully to client")

        // subscribe to the topic "my/test/topic"
        client.subscribeWith()
            .topicFilter("LOCATION/#")
            .send()


        // set a callback that is called when a message is received (using the async API style)
        client.toAsync().publishes(MqttGlobalPublishFilter.ALL) { publish ->
            Log.d("MQTT received message", "Received message: ${publish.topic} -> ${UTF_8.decode(publish.payload.get())}")

            // disconnect the client after a message was received

        }
        // publish a message to the topic "my/test/topic"
        client.publishWith()
            .topic("LOCATION")
            .payload(UTF_8.encode("Hello"))
            .send()
    }
}