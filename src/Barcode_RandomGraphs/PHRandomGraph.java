/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Barcode_RandomGraphs;

import RandomGraph.Graph;
import RandomGraph.Parameter;
import RandomGraph.Watts_StrogatzGraph;
import Util.FileFolder;
import java.util.Random;

/**
 *
 * @author naheed
 */
public class PHRandomGraph {

    /**
     * @param args the command line arguments
     */
    static int[] seedar = {7,13,23,29,11};
    static String filename = null;
    static int option = 1;
    static String foldername= null;
    public static void main(String[] args) {
        // TODO code application logic here
        for (int i = 0; i < seedar.length; i++) {
            Random rn = new Random(seedar[i]);
            Graph randgr = null;
            foldername = "watts-strogatz";
            filename = "graph1.edges";  
            String working_dir = System.getProperty("user.dir");
            String seed_randomdirname = null;
            //System.out.println(working_dir);
            
            if(!FileFolder.CreateFolderifnotexists(working_dir,foldername)){
                System.out.println("Couldn't create output directory");
                return ;
            }
            if(option==1){
                seed_randomdirname = "watts-strogatz_"+seedar[i];
                if(!FileFolder.CreateFolderifnotexists(working_dir+"/"+foldername+"/", seed_randomdirname)){
                    System.out.println("Couldn't create output directory");
                    return ;
                }
                          
                Parameter params = new Parameter();
                params.put("name", "Watts-Strogatz");
                params.put("N", 1000); //Number of Nodes
                params.put("D", 6); // Degree D
                                        // Edges E = ND/2 always (For a fixed N and D)
                params.put("p", 0.5); //Rewiring Probability
                randgr = new Watts_StrogatzGraph(params).generate();
                //System.out.println(randgr.graph_parameters.get("name"));
            }
            if(randgr!=null)
                randgr.write_graph(working_dir+"/"+foldername+"/"+ seed_randomdirname+"/"+filename);
            else{
                System.out.println("ERROR!! Cann't right graph into a file");
            }
        }        
    }
    
}
