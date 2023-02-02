import time
import paho.mqtt.client as paho
from paho import mqtt
import time
import math
import numpy as np
from sklearn.cluster import DBSCAN

cars = {}
previousClusters = {}
timestamps = {}
lastClusterindex=0
def on_connect(client, userdata, flags, rc, properties=None):
    print("CONNACK received with code %s." % rc)


def on_publish(client, userdata, mid, properties=None):
    print("mid: " + str(mid))


def on_subscribe(client, userdata, mid, granted_qos, properties=None):
    print("Subscribed: " + str(mid) + " " + str(granted_qos))


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
    global lastClusterindex
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
            
            rcluster_radius = round(cluster_radius, 2)
            rcluster_center = [round(coordinate, 2) for coordinate in cluster_center]
            if (previousClusters):
                for cluster in previousClusters:
                    for i in range (0,lastClusterindex+1):
                        if i in previousClusters:
                            if previousClusters[i]["cars"] == car_ids:
                                elapsed_time = time.time() - previousClusters[i]["timestamp"] 
                                if elapsed_time < 600:
                                    pass
                                else:
                                    previousClusters[i]["timestamp"]=time.time()
                                    message = "Traffic jam detected with " + str(len(cluster_points)) + " cars on a radius of " + str(rcluster_radius) + " with the center at " + str(rcluster_center)
                                    client.publish("NOTIFICATION",message)
            else:
                lastClusterindex += 1
                previousClusters[lastClusterindex] = {}
                previousClusters[lastClusterindex]["cars"]=car_ids
                previousClusters[lastClusterindex]["timestamp"]=time.time()
                message = "Traffic jam detected with " + str(len(cluster_points)) + " cars on a radius of " + str(rcluster_radius) + " with the center at " + str(rcluster_center)
                client.publish("NOTIFICATION",message)           
                
            
            print("Traffic jam detected with",len(cluster_points),"cars on a radius of",cluster_radius," with the center at ",cluster_center)
            print("Car IDs in a traffic jam: "+', '.join(car_ids))
        else:
            print("Number of cars in cluster", i,"is", len(cluster_points))
            print("Radius is : ",cluster_radius)

    

client = paho.Client(client_id="", userdata=None, protocol=paho.MQTTv5)
client.on_connect = on_connect


client.tls_set(tls_version=mqtt.client.ssl.PROTOCOL_TLS)

client.username_pw_set("JCC_RC_CommunicationFramework", "@B1EfbPKD%Pp%kPG")
client.connect("e5c6690c23234ff8b2e11e59d3fb82be.s2.eu.hivemq.cloud", 8883)


client.on_message = on_message

client.subscribe("LOCATION/#", qos=1)

client.loop_forever()
    
def main():
    pass
    
if __name__=="__main__":
    main()