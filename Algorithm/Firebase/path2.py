from firebase_admin import credentials, firestore, initialize_app
from time import sleep
from nearest import find_nearest_node
from a_star_al import Graph

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

            if 'destination' in data and 'Hnode' in data:
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


# 이벤트 리스너 등록
doc_watch = db.collection('GPS').on_snapshot(on_snapshot)

# 무한 루프 생성
while True:
    sleep(1)