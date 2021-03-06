package jplex_explore;

import edu.stanford.math.plex4.api.Plex4;
import edu.stanford.math.plex4.homology.barcodes.BarcodeCollection;
import edu.stanford.math.plex4.homology.barcodes.Interval;
import edu.stanford.math.plex4.homology.chain_basis.Simplex;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceAlgorithm;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceBasisAlgorithm;
import edu.stanford.math.plex4.streams.impl.ExplicitSimplexStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.stanford.math.plex4.visualization.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

public class Homology {

    private static void generate_barcode_image(BarcodeCollection<Double> circle_intervals) {
        List<Interval<Double>> interv;
        Interval in;
        BufferedImage im;
        for (int i = 0; i < 2; i++) {
            interv = circle_intervals.getIntervalsAtDimension(i);
            try {
                im = BarcodeVisualizer.drawBarcode(interv, "dimension: " + i, 8.0); // last argument maximum limit of bar interval
                File outputfile = new File("saved" + i + ".png");
                ImageIO.write(im, "png", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(Homology.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        ExplicitSimplexStream stream = new ExplicitSimplexStream();

        
        Scanner sn = null;
        String dataset_file = "datasets/0.edges";
        try {
            sn = new Scanner(new File(dataset_file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Homology.class.getName()).log(Level.SEVERE, null, ex);
        }
        int from, to;
        int[] flag = new int[1000];
        while (sn.hasNextInt()) {
            int[] vertices = new int[2];
            vertices[0] = sn.nextInt();
            vertices[1] = sn.nextInt();
            if (flag[vertices[0]] == 0) {
                stream.addVertex(vertices[0]);
                flag[vertices[0]] = 1;
            }
            if (flag[vertices[1]] == 0) {
                stream.addVertex(vertices[1]);
                flag[vertices[1]] = 1;
            }
            stream.addElement(vertices);
        }

        /*
         stream.addVertex(0);
         stream.addVertex(1);
         stream.addVertex(2);
		
         stream.addElement(new int[] {0,1});
         stream.addElement(new int[] {1,2});
         stream.addElement(new int[] {2,0});
         */
        /*
         stream.addVertex(1, 0); 
         stream.addVertex(2, 0);
         stream.addVertex(3, 0);
         stream.addVertex(4, 0);
         stream.addVertex(5, 1);
         stream.addElement(new int[] {1,2}, 0);
         stream.addElement(new int [] {2, 3}, 0);
         stream.addElement(new int [] {3, 4}, 0);
             
         stream.addElement(new int[] {4, 1}, 0);
         stream.addElement(new int [] {3, 5}, 2);
         stream.addElement(new int [] {4, 5}, 3);
                
         stream.addElement(new int [] {3, 4, 5}, 7);
             
         */
        stream.finalizeStream();

        System.out.println("Size of complex: " + stream.getSize());

        AbstractPersistenceAlgorithm<Simplex> persistence
                = Plex4.getModularSimplicialAlgorithm(3, 2);
        
        BarcodeCollection<Double> circle_intervals
                = persistence.computeIntervals(stream); // computing betti intervals

        System.out.println(circle_intervals); // printing betti intervals

        //generate_barcode_image(circle_intervals);
        generate_representative_cycle(stream,persistence);
        
        
    }

    private static void generate_representative_cycle(ExplicitSimplexStream stream,AbstractPersistenceAlgorithm<Simplex> persistence) {
        AbstractPersistenceBasisAlgorithm abs = (AbstractPersistenceBasisAlgorithm) persistence;
        System.out.println(abs.computeAnnotatedIntervals(stream));
    }

}
