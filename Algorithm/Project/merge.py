import json

with open('Project/weight_element_seoul.json') as f:
    data = json.load(f)

result = {}
for node in data:
    node_key = node['node_key']
    weights = node['weights']
    value = weights['CCTV'] * 0.3 + weights['Roadside'] * 0.01 + weights['Children'] * 0.01 + weights['Bus'] * 0.01 + weights['Alcol'] * 1.7
    result[node_key] = value

with open('result.json', 'w') as f:
    json.dump(result, f)
