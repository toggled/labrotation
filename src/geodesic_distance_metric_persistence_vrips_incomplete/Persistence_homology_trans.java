package geodesic_distance_metric_persistence_vrips_incomplete;

import Transitive_closure_homology.*;
import edu.stanford.math.plex4.api.Plex4;
import edu.stanford.math.plex4.homology.barcodes.AnnotatedBarcodeCollection;
import edu.stanford.math.plex4.homology.barcodes.BarcodeCollection;
import edu.stanford.math.plex4.homology.barcodes.Interval;
import edu.stanford.math.plex4.homology.chain_basis.Simplex;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceAlgorithm;
import edu.stanford.math.plex4.homology.interfaces.AbstractPersistenceBasisAlgorithm;
import edu.stanford.math.plex4.metric.impl.ExplicitMetricSpace;
import edu.stanford.math.plex4.streams.impl.ExplicitSimplexStream;
import edu.stanford.math.plex4.streams.impl.VietorisRipsStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Level;
import java.util.logging.Logger;
import edu.stanford.math.plex4.visualization.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;

public class Persistence_homology_trans {
    String graph_filename;
    static int max_closure;
    ExplicitSimplexStream stream;
    int maxdimension;
    int kclosure;
    int num_vertices;
    double [][] distance_array;
    public Persistence_homology_trans() {
        read_clique_config();
        this.read_distancefile(); 
        //print_distfile();
        ExplicitMetricSpace mspace;
        mspace = new ExplicitMetricSpace(this.distance_array);
        
        //System.out.println(mspace.distance(4, 5));
        int N = this.kclosure;
        int tmax = N-1;
        int maxdimension = 3;
        VietorisRipsStream vstream = new VietorisRipsStream(mspace,maxdimension,tmax,N); 
        
        AbstractPersistenceAlgorithm<Simplex> persistence
                = Plex4.getModularSimplicialAlgorithm(maxdimension, 2);
        BarcodeCollection<Double> circle_intervals
                = persistence.computeIntervals(vstream); // computing circle intervals
        
        System.out.println(vstream.getSize());
        System.out.println(circle_intervals.getBettiNumbers());
    }
    
    private void generate_barcode_image(BarcodeCollection<Double> circle_intervals,int maxdim) {
        List<Interval<Double>> interv;
        Interval in;
        BufferedImage im;
        for (int i = 0; i < maxdim; i++) {
            interv = circle_intervals.getIntervalsAtDimension(i);
            try {
                im = BarcodeVisualizer.drawBarcode(interv, "dimension: " + i, 8.0); // last argument maximum limit of bar interval
                File outputfile = new File(kclosure+this.graph_filename + i + "_barcode.png");
                ImageIO.write(im, "png", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

   /* public static void main(String[] args) {
        BasicHomology_triangulation_trans obj = new BasicHomology_triangulation_trans();
        obj.read_clique_config();
        for (int i = 1; i <= obj.max_closure; i++) {
            obj.build_stream(i);
        }
        
        
    }
*/
    private void generate_representative_cycle(ExplicitSimplexStream stream,AbstractPersistenceAlgorithm<Simplex> persistence,BarcodeCollection<Double> circle_intervals) {
        AbstractPersistenceBasisAlgorithm abs = (AbstractPersistenceBasisAlgorithm) persistence;
        //System.out.println(abs.computeAnnotatedIntervals(stream));
        // Write them to a file
        File f = new File(kclosure+"homology"+".out");
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
                    Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
            
             itt = it.getGeneratorIterator();
            while(itt.hasNext()){
                String s = itt.next().toString();
                try {
                    System.out.println(s);
                    bw.newLine();
                    bw.write(s);
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
            try {
                bw.newLine();
                bw.write(circle_intervals.getBettiNumbers());
                bw.close();
                //this.bw.newLine();
            } catch (IOException ex) {
                Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private static int addhigherelement(ExplicitSimplexStream stream, String simplicesout) {
        /*
        Read all the cliques from a file line by line and add it to the simplical complex
        */
        File f = new File(simplicesout);
        
        int maxclique = 1;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader br;
            br = new BufferedReader(isr);
            String lineTxt = null;
            
            while((lineTxt = br.readLine()) != null){
                String[] tokens = lineTxt.split(" ");
                int[] elem = new int[tokens.length];
                maxclique = (tokens.length>maxclique?tokens.length:maxclique);
                for (int i = 0; i < tokens.length; i++) {
                    elem[i] = Integer.valueOf(tokens[i]);
                }
                if(tokens.length == 1){
                    stream.addVertex(Integer.valueOf(tokens[0])); // when we add 0-simplex or clique of size 1
                    //System.out.println(tokens[0]);
                }
                else 
                    stream.addElement(elem);
            }
            System.out.println("stream size: "+stream.getSize());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maxclique;
        
         
    }

    private void read_distancefile() {
        this.distance_array = new double[this.num_vertices][this.num_vertices];
        String config_name = "geodist.dist";
        File f = new File(config_name);
        FileReader fr = null;
        try {
            fr = new FileReader(f);
        } catch (IOException ex) {
            Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader br = new BufferedReader(fr);
        try {
            String val;
            while((val = br.readLine())!=null){
         
                StringTokenizer st  = new StringTokenizer(val," ");
                
                int from  = Integer.valueOf(st.nextToken());
                int to  = Integer.valueOf(st.nextToken());
                int dist  = Integer.valueOf(st.nextToken());
                this.distance_array[from][to] = dist;
            }
        } catch (IOException ex) {
            Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void print_distfile(){
     for (int i = 0; i < this.distance_array.length; i++) {
         for (int j = 0; j < this.distance_array[0].length; j++) {
             System.out.print(this.distance_array[i][j]+" ");
         }
         System.out.println("");
     }
 }
    public ExplicitSimplexStream build_stream(int kth_closure) {
        kclosure = kth_closure;
        ExplicitSimplexStream stream = new ExplicitSimplexStream();
        
  
        this.maxdimension = addhigherelement(stream,this.graph_filename+"_"+kth_closure+".out");
        
        stream.finalizeStream();
         return stream;
       
    }
    void compute_betti_nums(){
         System.out.println("Size of complex: " + this.stream.getSize());
        System.out.println("maxdimension: "+ maxdimension);
        AbstractPersistenceAlgorithm<Simplex> persistence
                = Plex4.getModularSimplicialAlgorithm(maxdimension, 2);
        
        BarcodeCollection<Double> circle_intervals
                = persistence.computeIntervals(this.stream); // computing betti intervals
        
        //System.out.println(circle_intervals); // printing betti intervals
        //System.out.println(circle_intervals.getBettiNumbers());
        generate_barcode_image(circle_intervals,maxdimension);
        generate_representative_cycle(this.stream,persistence,circle_intervals);
        //System.out.println(stream.validateVerbose());
    }
    void add_to_stream(String simplicesfile,int k){
        kclosure = k;
        File f = new File(simplicesfile);
        int maxclique = 1;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader br;
            br = new BufferedReader(isr);
            String lineTxt = null;
            
            while((lineTxt = br.readLine()) != null){
                String[] tokens = lineTxt.split(" ");
                int[] elem = new int[tokens.length];
                maxclique = (tokens.length>maxclique?tokens.length:maxclique);
                for (int i = 0; i < tokens.length; i++) {
                    elem[i] = Integer.valueOf(tokens[i]);
                }
                if(tokens.length == 1){
                    this.stream.addVertex(Integer.valueOf(tokens[0])); // when we add 0-simplex or clique of size 1
                    //System.out.println(tokens[0]);
                }
                else 
                    this.stream.addElement(elem);
            }
            System.out.println("stream size: "+this.stream.getSize());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Persistence_homology_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.stream.finalizeStream();
    }
    private void read_clique_config() {
        String config_name = "cliquecon.cfg";
        File f = new File(config_name);
        FileReader fr = null;
        try {
            fr = new FileReader(f);
        } catch (IOException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        BufferedReader br = new BufferedReader(fr);
        try {
            // Reading the cliqeu base file name
            String option = br.readLine();
            StringTokenizer st  = new StringTokenizer(option,"=");
            st.nextToken();
            this.num_vertices = Integer.valueOf(st.nextToken());
            
             option = br.readLine();
             st  = new StringTokenizer(option,"=");
             st.nextToken();
             this.max_closure  = Integer.valueOf(st.nextToken());
             
             option = br.readLine();
             st  = new StringTokenizer(option,"=");
             st.nextToken();
             this.graph_filename  = st.nextToken();
             
        } catch (IOException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
