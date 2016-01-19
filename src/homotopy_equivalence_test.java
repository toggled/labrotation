/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import edu.stanford.math.plex4.api.Plex4;
import edu.stanford.math.plex4.homology.barcodes.BarcodeCollection;
import edu.stanford.math.plex4.homology.barcodes.Interval;
import edu.stanford.math.plex4.homology.chain_basis.Simplex;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceAlgorithm;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceBasisAlgorithm;
import edu.stanford.math.plex4.streams.impl.ExplicitSimplexStream;

import edu.stanford.math.plex4.metric.impl.EuclideanMetricSpace;
import edu.stanford.math.plex4.streams.impl.VietorisRipsStream;
import edu.stanford.math.plex4.visualization.BarcodeVisualizer;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import jplex_explore.BasicHomology_triangulation;
/**
 *
 * @author naheed
 */
public class homotopy_equivalence_test {

    /**
     * @param args the command line arguments
     */
    private static void generate_barcode_image(BarcodeCollection<Double> circle_intervals,double tmax,int dmax,String Fileprefix) {
        List<Interval<Double>> interv;
        Interval in;
        BufferedImage im;
        
        for (int i = 0; i < dmax; i++) {
            interv = circle_intervals.getIntervalsAtDimension(i);
            try {
                im = BarcodeVisualizer.drawBarcode(interv, "dimension: " + i, tmax); // last argument maximum limit of bar interval
                File outputfile = new File(Fileprefix + i + ".png");
                ImageIO.write(im, "png", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        double [][] pointcloud1 = new double[][]{
            {-1, 0},
            {1, 0},
            {1, 2},
            {-1, 2},
            {0, 3}
        };
        
        EuclideanMetricSpace ems = new EuclideanMetricSpace(pointcloud1);
        int maxdim = 3; //dmax
        double max_filtration_val = 4; //tmax
        int numdivision = 100; // default is 20
        
        VietorisRipsStream vs = new VietorisRipsStream(ems,max_filtration_val,maxdim,numdivision);
        vs.finalizeStream();
        
        AbstractPersistenceAlgorithm<Simplex> persistence 
			= Plex4.getModularSimplicialAlgorithm(maxdim, 2);
        BarcodeCollection<Double> intervals 
			= persistence.computeIntervals(vs);
        System.out.println(intervals);
        generate_barcode_image(intervals,max_filtration_val,maxdim-1,"test_homology");
        generate_representative_cycle(vs,persistence);
        
    }
    private static void generate_representative_cycle(VietorisRipsStream stream,AbstractPersistenceAlgorithm<Simplex> persistence) {
        AbstractPersistenceBasisAlgorithm abs = (AbstractPersistenceBasisAlgorithm) persistence;
        System.out.println(abs.computeAnnotatedIntervals(stream));
    }
}
