#import pkg_resources
#pkg_resources.require("pyparsing == 1.5.7")
import matplotlib.pyplot as plt
import networkx as nx
import sys


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

def create_snapgraph(filename=None):
    # filename = 'datasets/414.edges'
    if filename is None:    
        filename = 'graph1.edges'
    with open(filename,'r') as f:
        while 1:
            #print f.readline().split()
            line = f.readline()
            if not line:
                break
            nodes = line.split()
            if len(nodes)==1:
                G.add_node(nodes[0])
            else:
                G.add_node(nodes[0])
                G.add_node(nodes[1])                
                G.add_edge(nodes[0],nodes[1])
            

#create_snapgraph()
#create_myfbgraph()
#nx.draw(G,node_size = 150,with_labels = 1)
#plt.show()
if __name__ == "__main__":
	if len(sys.argv) == 2:
		for iter in range(int(sys.argv[1])):
			create_snapgraph("graph"+str(iter+1)+".edges")
		#create_myfbgraph()
			nx.draw(G,node_size = 150,with_labels = 1)
			plt.show()
	elif len(sys.argv) == 3:
		#process parameters
		if sys.argv[1] == '-d':
			dirctry = sys.argv[2]
			import os
			for file in os.listdir(dirctry):
				if file.endswith(".edges"):
					create_snapgraph(dirctry+"/"+file)
					nx.draw(G,node_size = 150,with_labels = 1)
					plt.show()
		if sys.argv[1] == '-ds':
			dirctry = sys.argv[2]
			import os
			for file in os.listdir(dirctry):
				if file.endswith(".edges"):
					create_snapgraph(dirctry+"/"+file)
					nx.draw(G,node_size = 150,with_labels = 1)
					plt.savefig(dirctry+'/'+file.split('.')[0]+'.eps',format='eps')
	else:
		pass
