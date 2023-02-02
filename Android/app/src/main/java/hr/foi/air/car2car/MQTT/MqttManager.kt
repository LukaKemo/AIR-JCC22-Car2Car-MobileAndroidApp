package hr.foi.air.car2car.MQTT


import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.MqttGlobalPublishFilter
import hr.foi.air.car2car.Car
import hr.foi.air.car2car.Notifications.NotificationViewModel
import hr.foi.air.car2car.R
import android.content.Context
import androidx.lifecycle.MutableLiveData
import hr.foi.air.car2car.MqttViewModel


import java.nio.charset.StandardCharsets.UTF_8
import java.util.concurrent.ConcurrentHashMap

class MqttConnectionImpl(): MqttConnection {
    private val host = "e5c6690c23234ff8b2e11e59d3fb82be.s2.eu.hivemq.cloud"
    private val username = "JCC_RC_CommunicationFramework"
    private val password = "@B1EfbPKD%Pp%kPG"
    private lateinit var viewModel: MqttViewModel

    override fun connectToMqtt(cars: ConcurrentHashMap<Int, Car>) {
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
            .topicFilter("#")
            .send()

        client.toAsync().publishes(MqttGlobalPublishFilter.ALL) { publish ->
            Log.d(
                "MQTT received message",
                "Received message: ${publish.topic} -> ${UTF_8.decode(publish.payload.get())}"
            )

            viewModel = MqttViewModel.getInstance()
            val mqttData = viewModel.data
            val topicParts = publish.topic.toString().split("/")
            val msg = UTF_8.decode(publish.payload.get())

            when {
                topicParts[0] == "LOCATION" && topicParts[1].toIntOrNull() != null -> {
                    val carInfo = msg.split(',')
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
                    val currentNotifications = mqttData.value ?: ArrayList()
                    currentNotifications.add(
                        NotificationViewModel(
                            R.drawable.notifications_icon,
                            "LOCATION:$msg"
                        )
                    )
                    mqttData.postValue(currentNotifications)
                }
                topicParts[0] == "NOTIFICATION" -> {
                    val currentNotifications = mqttData.value ?: ArrayList()
                    currentNotifications.add(
                        NotificationViewModel(
                            R.drawable.notifications_icon,
                            "NOTIFICATION:$msg"
                        )
                    )
                    mqttData.postValue(currentNotifications)
                }
                topicParts[0] == "DANGER" -> {
                    val currentNotifications = mqttData.value ?: ArrayList()
                    currentNotifications.add(
                        NotificationViewModel(
                            R.drawable.notifications_icon,
                            "DANGER:$msg"
                        )
                    )
                    mqttData.postValue(currentNotifications)
                }
            }

            Log.d("DATA", mqttData.toString())
        }

    }

    fun sendConnectedMessage() {
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

            client.publishWith()
                .topic("NOTIFICATION/ADMIN")
                .payload(UTF_8.encode("Admin monitor connected!"))
                .send()
        }
    }