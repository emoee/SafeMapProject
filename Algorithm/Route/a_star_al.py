import json

class Graph:
    def __init__(self):
        self.adjacency_list = json.load(open('Project/graph.json'))
        self.wktlist = json.load(open('Project/node.json'))
        self.weights = {}
        with open('Project/weight_element_seoul.json', 'r') as f:
            weight_data = json.load(f)
            for row in weight_data:
                node_key = row['node_key']
                weights = row['weights']
                self.weights[node_key] = weights

        def w(weights):
            #return 1
            return weights['CCTV'] * 0.3 + weights['Roadside'] * 0.01 + weights['Children'] * 0.01 + weights['Bus'] * 0.01 + weights['Alcol'] * 1.2
        self.w = w

    def get_weighted_neighbors(self, v):
        neighbors = self.adjacency_list[v]
        weighted_neighbors = []
        for neighbor, distance in neighbors:
            if neighbor not in self.weights:
                weight = 1
            else: 
                weight = self.w(self.weights[neighbor])
                if weight == 0: weight = 1
            weighted_neighbors.append((neighbor, distance * weight))
        return weighted_neighbors
    
    def a_star_algorithm(self, start_node, stop_node):
        open_list = set([start_node])
        closed_list = set([])
        g = {}
        g[start_node] = 0
        parents = {}
        parents[start_node] = start_node
        while len(open_list) > 0:
            n = None
            for v in open_list:
                if n == None or g[v] + self.get_weighted_neighbors(v)[0][1] < g[n] + self.get_weighted_neighbors(n)[0][1]:
                    n = v
            if n == None:
                print('Path does not exist!')
                return None
            if n == stop_node:
                reconst_path = []
                path_wkt = []
                path_len = 0
                result = {}
                while parents[n] != n:
                    reconst_path.append(n)
                    n = parents[n]
                reconst_path.append(start_node)
                reconst_path.reverse()
                for i in range(len(reconst_path)-1):
                    current_node = reconst_path[i]
                    next_node = reconst_path[i+1]
                    for (neighbor, weight) in self.get_weighted_neighbors(current_node):
                        if neighbor == next_node:
                            for (neighbor_real, weight_real) in self.adjacency_list[current_node]:
                                if neighbor_real == neighbor:
                                    path_len += weight_real
                            break
                for pathnode in reconst_path:
                    if pathnode not in self.wktlist: pass
                    else: path_wkt.append(self.wktlist[pathnode])
                result["Path found"] = reconst_path
                result["Total distance"] = path_len
                result["lati,longi"] = path_wkt
                return result
            
            for (m, weight) in self.get_weighted_neighbors(n):
                if m not in open_list and m not in closed_list:
                    open_list.add(m)
                    parents[m] = n
                    g[m] = g[n] + weight
                else:
                    if g[m] > g[n] + weight:
                        g[m] = g[n] + weight
                        parents[m] = n
                        if m in closed_list:
                            closed_list.remove(m)
                            open_list.add(m)
            open_list.remove(n)
            closed_list.add(n)

        print('Path does not exist!')
        return None