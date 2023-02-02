package hr.foi.air.car2car.MQTT


import hr.foi.air.car2car.Car
import java.util.concurrent.ConcurrentHashMap

interface MqttConnection {
    fun connectToMqtt(cars : ConcurrentHashMap<Int, Car>)
}