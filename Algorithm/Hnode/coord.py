from geopy.distance import geodesic

coord1 = [37.502606, 127.025115]
coord2 = [37.499389, 127.026629]

# coord1과 coord2 사이의 거리를 계산합니다.
total_distance = geodesic(coord1, coord2).m

# coord1과 coord2를 1.11m 간격으로 나누어진 좌표 리스트를 만듭니다.
interval = 1.11
num_points = int(total_distance // interval)
frac_distance = interval / total_distance
coord_list = []
for i in range(num_points+1):
    lat = coord1[0] + i * frac_distance * (coord2[0] - coord1[0])
    lon = coord1[1] + i * frac_distance * (coord2[1] - coord1[1])
    coord_list.append([lat, lon])

print(coord_list)

