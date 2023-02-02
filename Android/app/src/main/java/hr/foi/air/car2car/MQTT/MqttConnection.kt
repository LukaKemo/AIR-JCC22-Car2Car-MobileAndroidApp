package hr.foi.air.car2car.MQTT

import hr.foi.air.car2car.Car

interface MqttConnection {
    fun connectToMqtt(cars : HashMap<Int, Car>)
}