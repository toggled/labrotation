#import pkg_resources
#pkg_resources.require("pyparsing == 1.5.7")
import matplotlib.pyplot as plt
import networkx as nx



G = nx.Graph()
def create_myfbgraph():
    filename = 'datasets/friends.txt'
    with open(filename,'r') as f:
        while 1:
            #print f.readline().split()
            line = f.readline()
            if not line:
                break
            #nodea,nodeb = line.split()
            nodea,nodeb = line.split(',')
            if nodea == '1':
                G.add_node(nodea)
                G.add_node(nodeb)
                G.add_edge(nodea,nodeb,color = 'green')
            else:
                G.add_node(nodea)
                G.add_node(nodeb)
                G.add_edge(nodea,nodeb)
        
        #G.add_nodes_from([1,2,3])
        #G.add_edges_from( [ (1,2), (2,3)] )
        #G.add_edges_from( [ (1,3) ], color='red')
def create_snapgraph():
    # filename = 'datasets/414.edges'    
    filename = 'graph2.edges'
    with open(filename,'r') as f:
        while 1:
            #print f.readline().split()
            line = f.readline()
            if not line:
                break
            nodea,nodeb = line.split()
            #nodea,nodeb = line.split(',')
            if nodea == '200':
                G.add_node(nodea,color = 'green')
            else:
                G.add_node(nodea)
            G.add_node(nodeb)
            G.add_edge(nodea,nodeb)
            
create_snapgraph()
#create_myfbgraph()
nx.draw(G,node_size = 150,with_labels = 1)
plt.show()
