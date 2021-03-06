/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transitive_closure_homology;
import edu.stanford.math.plex4.homology.barcodes.BarcodeCollection;
/**
 *
 * @author naheed
 */
public class Barcode_Computer {

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) {
//        // TODO code application logic here
//        Barcode_Computer bc = new Barcode_Computer();
//        bc.runpersistence_algo();
//    }
    BasicHomology_triangulation_trans obj;
    
    void runpersistence_algo(){
       
            obj = new BasicHomology_triangulation_trans();
            obj.stream = obj.build_stream(1);
            System.out.println("max closure: "+BasicHomology_triangulation_trans.max_closure);
            for(int i  = 2; i<= BasicHomology_triangulation_trans.max_closure ;i++){


                //obj.compute_betti_nums();  
               //obj.add_to_stream("graph"+i+".edges", i); // for the tomita version
               obj.add_to_stream(obj.outputdir_path+obj.clique_base_filename+"_"+i+".out",i);

                //obj.compute_betti_nums();
            }
            obj.stream.finalizeStream();
            //System.out.println(obj.stream.toString());
            obj.compute_betti_nums();

    }
    BarcodeCollection<Double> getIntervals(){
        return obj.intervals;
    }
    
}
