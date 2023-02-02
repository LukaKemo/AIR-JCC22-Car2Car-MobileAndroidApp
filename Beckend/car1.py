import paho.mqtt.client as mqtt
import time
import math

mqttBroker = "mqtt.eclipseprojects.io"
client = mqtt.Client("Car1")
client.connect(mqttBroker)





def main():
   # message ="Hello second car"
   # client.publish("SIGNAL",message)
   # print("Published a message:" + message + "\nTo a topic: SIGNAL")
    simulate_car_movement(46.307882,16.358041,46.298256,16.353410,100)
    send_current_location(2,46.308151,16.358597)


def send_current_location(carID,lat,lon):
    pass
        
       

def simulate_car_movement(lat1, lon1, lat2, lon2, speed):
    carID = '1'
    R = 6371 # radius of Earth in kilometers
    
    dLat = math.radians(lat2-lat1)
    dLon = math.radians(lon2-lon1)
    lat1 = math.radians(lat1)
    lat2 = math.radians(lat2)

    a = math.sin(dLat/2) * math.sin(dLat/2) + math.sin(dLon/2) * math.sin(dLon/2) * math.cos(lat1) * math.cos(lat2) 
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a)) 
    distance = R * c * 1000 # distance in meters

    duration = distance / speed # duration in seconds
    message = f"2,46.308151,16.358597"
    client.publish("LOCATION",message)

    start_time = time.time()
    current_time = start_time
    while current_time - start_time <= duration:
        elapsed_time = current_time - start_time
        fraction_of_trip = elapsed_time / duration
        current_lat = lat1 + (lat2 - lat1) * fraction_of_trip
        current_lon = lon1 + (lon2 - lon1) * fraction_of_trip
        print(f'Current location: {current_lat}, {current_lon}')
        message = f"{carID},{current_lat},{current_lon}"
        client.publish("LOCATION",message)
        time.sleep(0.5)
        current_time = time.time()
        
        
if __name__=="__main__":
    main()