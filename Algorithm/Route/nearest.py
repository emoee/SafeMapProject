import numpy as np
from sklearn.neighbors import KDTree
import json

def find_nearest_node(wktlist):
    with open('Project/node.json', 'r') as f:
        nodes = json.load(f)

    # nodes 딕셔너리에서 좌표 정보만 추출하여 NumPy 배열 형태로 node_positions 변수에 저장합니다.
    node_positions = np.array(list(nodes.values()))

    # 추출한 좌표 정보를 이용하여 KDTree를 생성합니다. 이 때 leaf_size 매개변수는 트리 구조 생성 시 각 노드가 가지는 최대 데이터 수를 나타내는 값입니다. 
    # 이 값을 높이면 트리가 더 높아지므로 검색 속도는 느려지지만, 더 정확한 결과를 얻을 수 있습니다.
    tree = KDTree(node_positions, leaf_size=2)

    #print(type(tree))

    # 생성한 KDTree에서 입력 좌표와 가장 가까운 노드를 찾습니다. query() 함수는 입력된 좌표와 가장 가까운 이웃 k개의 거리와 인덱스를 반환합니다. 
    # 이 함수에서는 k=1로 설정하여 가장 가까운 노드 1개의 거리와 인덱스만 반환하도록 합니다. 
    # 반환값인 dist와 ind는 각각 거리와 인덱스를 나타내는 NumPy 배열입니다.
    dist, ind = tree.query([wktlist], k=1)
    
    nearest_node_key = list(nodes.keys())[ind[0][0]]
    return nearest_node_key

# nearest_node_key = find_nearest_node([37.504603 ,127.024747])
# print(nearest_node_key)  # 출력 결과: 164469