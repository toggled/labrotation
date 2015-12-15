/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transitive_closure_homology;

/**
 *
 * @author naheed
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        BasicHomology_triangulation_trans obj = new BasicHomology_triangulation_trans();
        obj.stream = obj.build_stream(1);
        obj.compute_betti_nums();  
        for(int i  = 2; i<=obj.max_closure;i++){
            obj.add_to_stream(obj.clique_base_filename+"_"+i+".out");
            obj.compute_betti_nums();
        }
    }
    
}
