from firebase_admin import credentials, firestore, initialize_app
import time
from coord import coord_list

# Firebase Admin SDK 설정
cred = credentials.Certificate('/Users/yoon/Downloads/safe-navigation-app-6fc08-firebase-adminsdk-sy8t7-364b14f110.json')
initialize_app(cred)

# Firestore 데이터베이스에서 데이터 가져오기
db = firestore.client()

doc_ref = db.collection('GPS').document('safe@gmail.com')
for coord in coord_list:
    doc_ref.set({
        'Hnode': {
            'latitude': coord[0],
            'longitude': coord[1]
        }
    })
    time.sleep(1) # 1초 대기
