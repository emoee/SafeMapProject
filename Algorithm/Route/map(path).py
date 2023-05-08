import folium
from result import result

# 지도 초기 위치 설정
latitude = 37.500288
longitude = 127.024768
'''
강남
latitude = 37.499753
longitude = 127.029465
'''
'''
사당
latitude = 37.483517
longitude = 126.979604
'''
map = folium.Map(location=[latitude, longitude], zoom_start=30)

# 시각화할 위도, 경도 정보 리스트
locations = result['lati,longi']

# 지도위에 마커와 라인을 표시
for i, location in enumerate(locations):
    folium.Marker(location=location, icon=folium.Icon(color='blue'), popup=str(i+1)).add_to(map)
    if i < len(locations) - 1:
        folium.PolyLine([location, locations[i+1]], color='red', weight=2.5).add_to(map)

# 지도를 HTML 파일로 저장
map.save('Route/map(path).html')