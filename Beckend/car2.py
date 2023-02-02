import paho.mqtt.client as mqtt
import time

mqttBroker = "mqtt.eclipseprojects.io"
client = mqtt.Client("Car2")
client.connect(mqttBroker)

def on_message(client,userdata,message):
    print("Received message: ", str(message.payload.decode("utf-8")))


client.subscribe("LOCATION/#")
client.on_message=on_message
client.loop_forever()
