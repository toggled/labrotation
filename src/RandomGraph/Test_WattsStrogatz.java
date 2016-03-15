/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraph;

/**
 *
 * @author naheed
 */
public class Test_WattsStrogatz {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int average_deg = 6; // Always even Number is expected
        Parameter params = new Parameter();
        params.put("name", "Watts-Strogatz");
        params.put("N", 10); //Number of Nodes
        params.put("D", average_deg); // Degree D
                                // Edges E = ND/2 always (For a fixed N and D)
        params.put("p", 0.5); //Rewiring Probability
        Graph randgr = new Watts_StrogatzGraph(params).generate();
        //System.out.println(randgr.graph_parameters.get("name"));
        randgr.write_graph("watts-strogatz.edges");
    }
    
}
