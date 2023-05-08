from firebase_admin import credentials, firestore, initialize_app
from time import sleep, strftime, localtime
from address import reverse_geocode
import send_sms
from send_sms import send_sms

# Firebase Admin SDK 설정
cred = credentials.Certificate('/Users/yoon/Downloads/safe-navigation-app-6fc08-firebase-adminsdk-sy8t7-364b14f110.json')
initialize_app(cred)

# Firestore 데이터베이스에서 데이터 가져오기
db = firestore.client()

def on_snapshot(doc_snapshot, changes, read_time):
    for doc in doc_snapshot:
        data = doc.to_dict()
        if changes and 'Hnode' in data:
            lat = data['Hnode']['latitude']
            lon = data['Hnode']['longitude']
            address = reverse_geocode(lat, lon)
            current_time = strftime("%Y-%m-%d %H:%M:%S", localtime())
            sms = '[abb]\n{0} 위급상황 발생\n아이 위치:{1}'.format(current_time,address)
            send_sms(1066529450, 'test', sms)

# 이벤트 리스너 등록
doc_watch = db.collection('Report').on_snapshot(on_snapshot)

# 무한 루프 생성
while True:
    sleep(1)