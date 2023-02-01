package hr.foi.air.car2car.MQTT

import androidx.lifecycle.MutableLiveData
import hr.foi.air.car2car.Car
import hr.foi.air.car2car.Notifications.NotificationViewModel

interface MqttConnection {
    fun connectToMqtt(cars : HashMap<Int, Car>)
}