package hr.foi.air.car2car.MQTT

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import hr.foi.air.car2car.Car

import java.nio.charset.StandardCharsets.UTF_8

class MqttConnectionImpl(private val cars : HashMap<Int, Car>): MqttConnection {
    private val host = "e5c6690c23234ff8b2e11e59d3fb82be.s2.eu.hivemq.cloud"
    private val username = "JCC_RC_CommunicationFramework"
    private val password = "@B1EfbPKD%Pp%kPG"


    override fun connectToMqtt(cars: HashMap<Int,Car>) {
        val client = MqttClient.builder()
            .useMqttVersion5()
            .serverHost(host)
            .serverPort(8883)
            .sslWithDefaultConfig()
            .buildBlocking()

        client.connectWith()
            .simpleAuth()
            .username(username)
            .password(UTF_8.encode(password))
            .applySimpleAuth()
            .send()
        Log.d("mqtt", "Connected successfully to client")

        client.subscribeWith()
            .topicFilter("LOCATION/#")
            .send()

        client.toAsync().publishes(MqttGlobalPublishFilter.ALL) { publish ->
            Log.d(
                "MQTT received message",
                "Received message: ${publish.topic} -> ${UTF_8.decode(publish.payload.get())}"
            )

            val topicParts = publish.topic.toString().split("/")
            if (topicParts[0] == "LOCATION" && topicParts[1].toIntOrNull() != null) {
                val carInfo = UTF_8.decode(publish.payload.get()).split(',')
                val id = topicParts[1].toInt()
                val lat = carInfo[1].toDouble()
                val lng = carInfo[2].toDouble()
                if (!cars.containsKey(id)) {
                    cars[id] = Car(id, lat, lng)
                    Log.d("CARS", "Created new car $carInfo")
                } else {
                    cars[id]?.updateLocation(LatLng(lat, lng))
                    Log.d("CARS", "Updated car $id current position:$lat,$lng")
                }
            }
        }

        client.publishWith()
            .topic("Notification/ADMIN")
            .payload(UTF_8.encode("Admin monitor connected!"))
            .send()
    }
}