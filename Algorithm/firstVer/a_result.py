import json
import a_star_al
import nearest

#현재 사용자의 위도 경도 값
lati1 = 37.564324
longi1 = 126.976498

#목적지 위도 경도 값
lati2 = 37.563824
longi2 = 126.978752

startID = nearest.find_nearest_node(lati1, longi1, json.load(open('node.json')))
endID = nearest.find_nearest_node(lati2, longi2, json.load(open('node.json')))

graph01 = a_star_al.Graph()
result = graph01.a_star_algorithm(startID, endID)

with open('path.json', 'w') as f :
    json.dump(result, f, ensure_ascii=False, indent="\t")