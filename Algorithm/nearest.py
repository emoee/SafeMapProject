import math
import json

def distance(lat1, lon1, lat2, lon2):
    R = 6371  # 지구의 반지름 (단위: km)
    dlat = math.radians(lat2 - lat1)
    dlon = math.radians(lon2 - lon1)
    a = math.sin(dlat/2) * math.sin(dlat/2) + math.cos(math.radians(lat1)) \
        * math.cos(math.radians(lat2)) * math.sin(dlon/2) * math.sin(dlon/2)
    c = 2 * math.atan2(math.sqrt(a), math.sqrt(1-a))
    return R * c  # 두 지점 간의 거리 (단위: km)

def find_nearest_node(latitude, longitude, nodes):
    min_distance = float('inf')
    nearest_node_id = None
    
    for node_id, node_location in nodes.items():
        node_lat, node_lon = node_location
        node_distance = distance(latitude, longitude, node_lat, node_lon)
        
        if node_distance < min_distance:
            min_distance = node_distance
            nearest_node_id = node_id
    
    return nearest_node_id

with open('node.json', 'r') as f:
    nodes = json.load(f)
    
latitude = 37.499915
longitude = 127.024740

nearest_node_id = find_nearest_node(latitude, longitude, nodes)
# print(f'가장 근접한 노드 : {nearest_node_id}')
