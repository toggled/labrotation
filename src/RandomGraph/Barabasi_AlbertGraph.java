/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraph;

import java.util.Random;
import java.util.ArrayList;
/**
 *
 * @author naheed
 */
public class Barabasi_AlbertGraph extends Graph implements RandomGraph {
    int num_nodes,degree_pernode;
    
    public Barabasi_AlbertGraph(Parameter graph_parameters) {

        num_nodes = (Integer) graph_parameters.get("N");
        degree_pernode = (Integer) graph_parameters.get("D");
        
        System.out.println("N: "+num_nodes+" D: "+degree_pernode);
    }
    public String get_name(){
        return "Bar-Albert_"+String.valueOf(this.num_nodes)+"_"+String.valueOf(this.numedges);
    }
    @Override
    public Graph generate() {
        
        this.initialize_graph(num_nodes); // Only the N nodes 
        ArrayList <Integer> existing_nodes  = new ArrayList ();
        ArrayList <Integer> target_nodes =  new ArrayList();
        for (int i = 0; i < degree_pernode; i++) {
            existing_nodes.add(i);
        }
        
        for (int curnode = degree_pernode; curnode < num_nodes; curnode++) {
            for (int added_edges_forcurnode = 0; added_edges_forcurnode < degree_pernode; ) {
               
                //System.out.println("iteration: "+curnode);
                //keep a running talley of the probability
                double prob = 0;
                //Choose a random number
                double randNum = new Random().nextDouble();
                //System.out.println("random: "+randNum);
                for(int potential_node = 0; potential_node < curnode ; potential_node++){
                    if(this.numedges == 0){ // during first iteration when there are no edges.
                        //prob+= new Random().nextDouble();   
                        prob = 1.0;
                    }
                    else
                        prob += (double) this.get_degree(potential_node)/ (double)(this.numedges*2);
                    //System.out.println("prob: "+prob);
                    if(prob>=randNum){ 
                        //System.out.println("potential node: "+potential_node);
                        if(this.adjmat[potential_node][curnode] == 0 && added_edges_forcurnode<degree_pernode){
                            this.add_edges(potential_node, curnode);
                            added_edges_forcurnode++;
                        }
                        //target_nodes.add(potential_node);
                    }

                }
            }
           //this.print_matrix();
            //System.out.println("");
            existing_nodes.add(curnode);
                
            
        }
        
        return this;
    }

    @Override
    public String getGraphParam(String param_key) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
