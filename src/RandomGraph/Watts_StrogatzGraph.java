/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraph;

import java.util.Random;

/**
 *
 * @author naheed
 */
public class Watts_StrogatzGraph extends Graph implements RandomGraph {
    int num_nodes,averagedegree;
    double rewiringprob;
    int num_edges;
    public Watts_StrogatzGraph(Parameter graph_parameters) {

        num_nodes = (Integer) graph_parameters.get("N");
        averagedegree = (Integer) graph_parameters.get("D");
        rewiringprob = (Double) graph_parameters.get("p");
        num_edges = (num_nodes*averagedegree)/2;
        //System.out.println("N: "+num_nodes+" D: "+averagedegree+" p: "+rewiringprob);
    }

    @Override
   public String toString(){
       return "Watts_Strogatz_"+String.valueOf(this.num_nodes)+"_"+String.valueOf(this.num_edges);
   }

    @Override
    public Graph generate() {
        this.initialize_graph(num_nodes);
        // Building the ring lattice
        for (int d = 1; d <= averagedegree/2; d++) { // N nodes and NK/2 edges
            for (int u = 0; u < num_nodes; u++) {
                this.add_edges(u, (u+d)%num_nodes);
            }
        }
        //this.print_matrix();
        //System.out.println(this.get_degree(0));
        // Randomly rewire
         Random random = new Random();
        for (int u = 0; u < num_nodes; u++) {
            for (int v = u+1; v < num_nodes; v++) {
                if(adjmat[u][v]==1){
                    if(random.nextDouble()< rewiringprob){
                       // System.out.println("rewiring: "+u+" "+v);
                        int w = random.nextInt(num_nodes);
                        while(w==u || this.adjmat[w][u]==1){
                            w = random.nextInt(num_nodes);
                        }
                        this.remove_edges(u, v);
                        this.add_edges(u, w);
                    }
                }
            }
        }
        //System.out.println("\n");
        //this.print_matrix();
        return this;
    }

    @Override
    public String getGraphParam(String key) {
        if (key.equals("N")) return String.valueOf(num_nodes);
        return "Missing Param";
    }

}
