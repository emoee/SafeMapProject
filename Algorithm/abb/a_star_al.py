import json

class Graph:
    def __init__(self):
        self.adjacency_list = json.load(open('Project/graph.json'))
        self.wktlist = json.load(open('Project/node.json'))
        self.w = json.load(open('Project/weight.json'))

    def get_weighted_neighbors(self, v):
        neighbors = self.adjacency_list[v]
        weighted_neighbors = []
        for neighbor, distance in neighbors:
            weight = self.w[neighbor]
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
                result = {}
                while parents[n] != n:
                    reconst_path.append(n)
                    n = parents[n]
                reconst_path.append(start_node)
                reconst_path.reverse()
                for pathnode in reconst_path:
                    if pathnode not in self.wktlist: pass
                    else: path_wkt.append(self.wktlist[pathnode])
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