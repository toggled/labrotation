package jplex_explore;

import edu.stanford.math.plex4.api.Plex4;
import edu.stanford.math.plex4.homology.barcodes.AnnotatedBarcodeCollection;
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
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import java.util.HashMap;
public class BasicHomology_triangulation {

    private static void generate_barcode_image(BarcodeCollection<Double> circle_intervals,int maxdim) {
        List<Interval<Double>> interv;
        Interval in;
        BufferedImage im;
        for (int i = 0; i < maxdim; i++) {
            interv = circle_intervals.getIntervalsAtDimension(i);
            try {
                im = BarcodeVisualizer.drawBarcode(interv, "dimension: " + i, 8.0); // last argument maximum limit of bar interval
                File outputfile = new File("saved" + i + ".png");
                ImageIO.write(im, "png", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        ExplicitSimplexStream stream = new ExplicitSimplexStream();

        
       
        Scanner sn = null;
        //String dataset_file = "datasets/testcase.edges";
        String dataset_file = "datasets/414.edges";
        try {
            sn = new Scanner(new File(dataset_file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        int from, to;
        int[] flag = new int[1000];
        AdjMatrixGraph G = new AdjMatrixGraph(1000);
        
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
            G.addEdge(vertices[0],vertices[1]);
            // check whether to add a face
            //checkfaces(G,vertices,stream);
               
        }
        
        int maxdimension = addhigherelement(stream,"simplices.out");
        // int maxdimension = 3;
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
         /*
         stream.addVertex(1); 
         stream.addVertex(2);
         stream.addVertex(3);
         stream.addVertex(4);
         stream.addVertex(5);
         stream.addElement(new int[] {1,2});
         stream.addElement(new int [] {2, 3});
         stream.addElement(new int [] {3, 4});
             
         stream.addElement(new int[] {4, 1});
         stream.addElement(new int [] {3, 1});
         stream.addElement(new int[] {4, 2});
         stream.addElement(new int [] {4, 5});
         //stream.addElement(new int[] {5, 1});
                
         stream.addElement(new int [] {3, 4, 2});
         stream.addElement(new int [] {1, 4, 2});
         stream.addElement(new int [] {3, 1, 2});
         stream.addElement(new int [] {3, 1, 4});
         //stream.addElement(new int [] {4, 1, 5});
         //stream.addElement(new int [] {1, 2, 3, 4});
         */
         stream.finalizeStream();

        System.out.println("Size of complex: " + stream.getSize());

        AbstractPersistenceAlgorithm<Simplex> persistence
                = Plex4.getModularSimplicialAlgorithm(maxdimension, 2);
        
        BarcodeCollection<Double> circle_intervals
                = persistence.computeIntervals(stream); // computing betti intervals
        
        System.out.println(circle_intervals); // printing betti intervals

        //generate_barcode_image(circle_intervals,maxdimension);
        generate_representative_cycle(stream,persistence);
        //System.out.println(stream.validateVerbose());
        
    }

    private static void generate_representative_cycle(ExplicitSimplexStream stream,AbstractPersistenceAlgorithm<Simplex> persistence) {
        AbstractPersistenceBasisAlgorithm abs = (AbstractPersistenceBasisAlgorithm) persistence;
        //System.out.println(abs.computeAnnotatedIntervals(stream));
        // Write them to a file
        File f = new File("homology.out");
        FileOutputStream fos;
        BufferedWriter bw;
        
        try {
            fos = new FileOutputStream(f);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            AnnotatedBarcodeCollection it = abs.computeAnnotatedIntervals(stream);
            //System.out.println(it);
            Iterator itt = it.getIntervalIterator();
            while(itt.hasNext()){
                String s = itt.next().toString();
                try {
                    bw.write(s);
                    bw.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
            
             itt = it.getGeneratorIterator();
            while(itt.hasNext()){
                String s = itt.next().toString();
                try {
                    bw.newLine();
                    bw.write(s);
                    
                } catch (IOException ex) {
                    Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
            try {
                bw.close();
                //this.bw.newLine();
            } catch (IOException ex) {
                Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void checkfaces(AdjMatrixGraph G, int[] vertices,ExplicitSimplexStream stream) {
        if(G.E()<=2) return;
        
            Iterator it = (Iterator) G.adj(vertices[0]);
            while(it.hasNext()){
                int adjacent_v = (int) it.next();
                if(adjacent_v == vertices[1])   continue;
                if (G.contains(adjacent_v, vertices[1])){
                    stream.addElement(new int[]{vertices[0],vertices[1],adjacent_v});
                    //System.out.println("okay");
                    //System.out.println(adjacent_v);
                }
            }
        
    }

    private static int addhigherelement(ExplicitSimplexStream stream, String simplicesout) {
        File f = new File(simplicesout);
        int maxclique = 3;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader br;
            br = new BufferedReader(isr);
            String lineTxt = null;
            
            while((lineTxt = br.readLine()) != null){
                String[] tokens = lineTxt.split(" ");
                int[] elem = new int[tokens.length];
                maxclique = tokens.length;
                for (int i = 0; i < tokens.length; i++) {
                    elem[i] = Integer.valueOf(tokens[i]);
                }
                stream.addElement(elem);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicHomology_triangulation.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maxclique;
         
    }

}
