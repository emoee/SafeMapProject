import pandas as pd
from math import sin, cos, sqrt, atan2, radians

df = pd.read_csv('csv/merged_safe.csv')

def haversine(lat1, lon1, lat2, lon2):
    R = 6371  # radius of the earth in kilometers
    dlat = radians(lat2 - lat1)
    dlon = radians(lon2 - lon1)
    a = sin(dlat/2)**2 + cos(radians(lat1)) * cos(radians(lat2)) * sin(dlon/2)**2
    c = 2 * atan2(sqrt(a), sqrt(1-a))
    d = R * c
    return d

def find_closest(lat, lon, num_rows=3):
    distances = []
    for _, row in df.iterrows():
        distance = haversine(row['latitude'], row['longitude'], lat, lon)
        distances.append(distance)

    df['distance'] = distances
    closest_rows = df.sort_values('distance').head(num_rows)

    result = {}
    j = 0
    for i, row in enumerate(closest_rows.itertuples()):
        j = j+1
        result["[{0}]{1}".format(j,row.type)] = (row.latitude, row.longitude)

    return result

#result = find_closest(37.514026,127.028346)
#print(result)