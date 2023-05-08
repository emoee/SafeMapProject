import json
#import a_star_al
import a_star2
import nearest
import math
import time

start = time.time()

#논현초
# startID = nearest.find_nearest_node([37.504603 ,127.024747])
# endID = nearest.find_nearest_node([37.499189, 127.023197])
# print(startID,endID)

#강남
#startID = nearest.find_nearest_node([37.498661 ,127.027775])
#endID = nearest.find_nearest_node([37.501318, 127.029222])

startID = nearest.find_nearest_node([37.502606 ,127.025115])
endID = nearest.find_nearest_node([37.499389, 127.026629])
print(startID,endID)

graph01 = a_star2.Graph()
result = graph01.a_star_algorithm(startID, endID)
print(result)

end = time.time()
print(f"{end - start:.5f} sec")
#result = graph01.a_star_algorithm('107672', '106906')
#result = graph01.a_star_algorithm('194041', '106906')

#with open('Route/path.json', 'w') as f :
#    json.dump(result, f, ensure_ascii=False, indent="\t")