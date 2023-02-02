import time
import paho.mqtt.client as paho
from paho import mqtt
import time
import math

def simulate_car_movement(lat1, lon1, lat2, lon2, speed):
    R = 6371 # radius of Earth in kilometers

    dLat = math.radians(lat2-lat1)
    dLon = math.radians(lon2-lon1)
    lat1 = math.radians(lat1)
    lat2 = math.radians(lat2)

    a = math.sin(dLat/2) * math.sin(dLat/2) + math.sin(dLon/2) * math.sin(dLon/2) * math.cos(lat1) * math.cos(lat2) 
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a)) 
    distance = R * c * 1000 # distance in meters

    duration = distance / speed # duration in seconds

    start_time = time.time()
    current_time = start_time
    while current_time - start_time <= duration:
        elapsed_time = current_time - start_time
        fraction_of_trip = elapsed_time / duration
        current_lat = lat1 + (lat2 - lat1) * fraction_of_trip
        current_lon = lon1 + (lon2 - lon1) * fraction_of_trip
        print(f'Current location: {current_lat}, {current_lon}')
        time.sleep(0.5)
        current_time = time.time()

# setting callbacks for different events to see if it works, print the message etc.
def on_connect(client, userdata, flags, rc, properties=None):
    print("CONNACK received with code %s." % rc)

# with this callback you can see if your publish was successful
def on_publish(client, userdata, mid, properties=None):
    print("mid: " + str(mid))

# print which topic was subscribed to
def on_subscribe(client, userdata, mid, granted_qos, properties=None):
    print("Subscribed: " + str(mid) + " " + str(granted_qos))

# print message, useful for checking if it was successful
def on_message(client, userdata, msg):
    print(msg.topic + " " + str(msg.qos) + " " + str(msg.payload))

# using MQTT version 5 here, for 3.1.1: MQTTv311, 3.1: MQTTv31
# userdata is user defined data of any type, updated by user_data_set()
# client_id is the given name of the client
client = paho.Client(client_id="", userdata=None, protocol=paho.MQTTv5)
client.on_connect = on_connect

# enable TLS for secure connection
client.tls_set(tls_version=mqtt.client.ssl.PROTOCOL_TLS)
# set username and password
client.username_pw_set("JCC_RC_CommunicationFramework", "@B1EfbPKD%Pp%kPG")
# connect to HiveMQ Cloud on port 8883 (default for MQTT)
client.connect("e5c6690c23234ff8b2e11e59d3fb82be.s2.eu.hivemq.cloud", 8883)

# setting callbacks, use separate functions like above for better visibility
client.on_subscribe = on_subscribe
client.on_message = on_message
client.on_publish = on_publish

# subscribe to all topics of encyclopedia by using the wildcard "#"
client.subscribe("encyclopedia/#", qos=1)

# a single publish, this can also be done in loops, etc.
client.publish("encyclopedia/temperature", payload="hot", qos=1)

# loop_forever for simplicity, here you need to stop the loop manually
# you can also use loop_start and loop_stop
client.loop_forever()