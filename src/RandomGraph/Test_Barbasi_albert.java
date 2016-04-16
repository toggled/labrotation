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
public class Test_Barbasi_albert {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        int deg_eachnode = 5; // Always even Number is expected
        Parameter params = new Parameter();
        params.put("name", "Barbasi-Albert");
        params.put("N", 20); //Number of Nodes
        params.put("m", deg_eachnode); // Degree D
                                // Edges E = ND/2 always (For a fixed N and D)
        
        Graph randgr = new Barabasi_AlbertGraph(params).generate();
        //System.out.println(randgr.graph_parameters.get("name"));
        randgr.write_graph("barbasi-albert.edges");
    }
    
}
