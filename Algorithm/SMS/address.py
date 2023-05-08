from geopy.geocoders import Nominatim

def reverse_geocode(latitude, longitude):
    # Nominatim 객체 생성
    geolocator = Nominatim(user_agent="my-application-1")

    # 위도와 경도를 이용하여 주소 가져오기
    location = geolocator.reverse(str(latitude) + " " + str(longitude))

    # 주소 출력 (문자열 분리 후 뒤집기, 두 번째 값 삭제)
    address_list = location.address.split(', ')
    address_list = address_list[1:-2]
    reversed_address = ' '.join(address_list[::-1])
    
    return reversed_address

#lat = 37.211745
#lon = 126.953032
#map_link = f'https://www.google.com/maps?q={lat},{lon}'
#print(map_link)

#print(reverse_geocode(37.211745, 126.953032))