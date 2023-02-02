import time
import paho.mqtt.client as paho
from paho import mqtt
import time
import math
import numpy as np
from sklearn.cluster import DBSCAN
#TODO : upon shutting down car ,remove it from dictionary
cars = {}

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
    topic_parts = msg.topic.split("/")
    payload = msg.payload
    parts = payload.split(b',')
    
    lat = parts[1].decode()
    lon = parts[2].decode()
    
    if topic_parts[0] == "LOCATION" and topic_parts[1].isnumeric():
        cars[topic_parts[1]]=[lat,lon]
        
    checkRadius(cars)
    
def checkRadius(cars):
    cars = {k: list(map(float, v)) for k, v in cars.items()}
    coordinates = []
    coordinates = np.array(list(map(lambda x: list(map(float, x)), cars.values())))
    

    # define the DBSCAN parameters
    eps = 500  # maximum distance between two points in a cluster
    min_samples = 3  # minimum number of points required to form a dense region

    # run DBSCAN on the points
    db = DBSCAN(eps=eps, min_samples=min_samples).fit(coordinates)

    # number of cars required to be considered as traffic jam
    num_cars_threshold = 3
    # maximum radius for a cluster to be considered as traffic jam
    maximum_radius = 6

    # check if there is at least one cluster that contains more than or equal to the specified number of cars
    # and has a radius less than the specified value
    for i in set(db.labels_):
        if i == -1:
            continue
        cluster_points = coordinates[db.labels_ == i]
        cluster_center = np.mean(cluster_points, axis=0)
        cluster_radius = np.mean(np.linalg.norm(cluster_points - cluster_center, axis=1)) * 10000
        if len(cluster_points) >= num_cars_threshold and cluster_radius < maximum_radius:
            # get the car IDs of the cars in the cluster
            car_ids = [car_id for car_id, coords in cars.items() if coords in cluster_points]
            message = "Traffic jam detected with " + str(len(cluster_points)) + " cars on a radius of " + str(cluster_radius) + " with the center at " + str(cluster_center)
            client.publish("NOTIFICATION",message)
            print("Traffic jam detected with",len(cluster_points),"cars on a radius of",cluster_radius," with the center at ",cluster_center)
            print("Car IDs in a traffic jam: "+', '.join(car_ids))
        else:
            print("Number of cars in cluster", i,"is", len(cluster_points))
            print("Radius is : ",cluster_radius)

    

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
#client.on_subscribe = on_subscribe
client.on_message = on_message
#client.on_publish = on_publish

# subscribe to all topics of encyclopedia by using the wildcard "#"
client.subscribe("LOCATION/#", qos=1)

# a single publish, this can also be done in loops, etc.
#client.publish("LOCATION", payload="Location", qos=1)


client.loop_forever()
    
def main():
    pass
    
if __name__=="__main__":
    main()