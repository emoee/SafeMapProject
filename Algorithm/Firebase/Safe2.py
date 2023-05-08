from firebase_admin import credentials, firestore, initialize_app
from time import sleep
from nearSafe import find_closest

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

            if 'Hnode' in data:
                # Hnode 필드의 위도(latitude)와 경도(longitude) 값을 가져와 startGPS에 저장
                start_lat = data['Hnode']['latitude']
                start_lon = data['Hnode']['longitude']
                result = find_closest(start_lat,start_lon)
                print(result)

                # 결과를 Firestore에 업로드
                doc_ref = db.collection('Safe').document(change.document.id)
                doc_ref.set(result)

# 이벤트 리스너 등록
doc_watch = db.collection('GPS').on_snapshot(on_snapshot)

# 무한 루프 생성
while True:
    sleep(1)