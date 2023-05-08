from firebase_admin import credentials, firestore, initialize_app
from time import sleep, strftime, localtime
from nearest import find_nearest_node
from a_star_al import Graph
from nearSafe import find_closest
from address import reverse_geocode
import send_sms
from send_sms import send_sms

# Firebase Admin SDK 설정
cred = credentials.Certificate('/Users/yoon/Downloads/safe-navigation-app-6fc08-firebase-adminsdk-sy8t7-364b14f110.json')
initialize_app(cred)

# Firestore 데이터베이스에서 데이터 가져오기
db = firestore.client()

def on_snapshot(doc_snapshot, changes, read_time):
    for change in changes:
        if change.type.name == 'MODIFIED':
            doc_ref = db.collection('PATH2').document(change.document.id)
            data = change.document.to_dict()

            #도착지 검색 시 경로 업로드
            if 'destination' and 'Hnode' in data:
                start_lat = data['Hnode']['latitude']
                start_lon = data['Hnode']['longitude']
                startGPS = [start_lat, start_lon]

                end_lat = data['destination']['latitude']
                end_lon = data['destination']['longitude']
                endGPS = [end_lat, end_lon]

                startID = find_nearest_node(startGPS)
                endID = find_nearest_node(endGPS)

                result = Graph().a_star_algorithm(startID, endID)

                path = []
                for lat_lon in result["lati,longi"]:
                    path.append(firestore.GeoPoint(lat_lon[0], lat_lon[1]))
                doc_ref.set({'path': path, 'total': result["Total distance"]})

            #근처 안전 시설
            else:
                # Hnode 필드의 위도(latitude)와 경도(longitude) 값을 가져와 startGPS에 저장
                start_lat = data['Hnode']['latitude']
                start_lon = data['Hnode']['longitude']
                result = find_closest(start_lat,start_lon)
                print(result)

                # 결과를 Firestore에 업로드
                doc_ref = db.collection('Safe').document(change.document.id)
                doc_ref.set(result)

def on_snapshot2(doc_snapshot, changes, read_time):
    for change in changes:
        if change.type.name == 'MODIFIED':
            doc_ref = db.collection('PATH2').document(change.document.id)
            data = change.document.to_dict()

            if 'Hnode' in data:
                lat = data['Hnode']['latitude']
                lon = data['Hnode']['longitude']
                address = reverse_geocode(lat, lon)
                current_time = strftime("%H:%M", localtime())
                map_link = f'https://www.google.com/maps?q={lat},{lon}'

                sms = '[abb 신고알림]\n아이 위치 :\n{0}'.format(address)
                sms2 = '{}'.format(map_link)
                send_sms(1066529450, 'test', sms)
                send_sms(1066529450, 'test', sms2)


# 이벤트 리스너 등록
doc_watch = db.collection('GPS').on_snapshot(on_snapshot)
doc_watch2 = db.collection('Report').on_snapshot(on_snapshot2)

# 무한 루프 생성
while True:
    sleep(1)