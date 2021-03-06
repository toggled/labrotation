package Transitive_closure_homology;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import jplex_explore.AdjMatrixGraph;
import java.util.regex.*;

public class BasicHomology_triangulation_trans {
    String clique_base_filename;
    static int max_closure;
    ExplicitSimplexStream stream;
    int maxdimension;
    int kclosure;
    String outputdir_path;
    List <String> dim_birth_dataarray;
    public BarcodeCollection<Double> intervals;
    
    public BasicHomology_triangulation_trans() {
        this.read_clique_config();  
    }
    
    private void generate_barcode_image(BarcodeCollection<Double> circle_intervals,int maxdim) {
        List<Interval<Double>> interv;
        Interval in;
        BufferedImage im;
        for (int i = 0; i < maxdim; i++) {
            interv = circle_intervals.getIntervalsAtDimension(i);
            intervals = circle_intervals;  
            try {
                im = BarcodeVisualizer.drawBarcode(interv, "dimension: " + i, this.maxdimension); // last argument maximum limit of bar interval
                File outputfile = new File(outputdir_path+clique_base_filename + i + "_barcode.png");
                ImageIO.write(im, "png", outputfile);
            } catch (IOException ex) {
                Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
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
        File f = new File(outputdir_path+kclosure+"homology"+".out");
        FileOutputStream fos;
        BufferedWriter bw;
        dim_birth_dataarray = new ArrayList<String>(); // instantiate the list to store dim,birth,death
        
        try {
            fos = new FileOutputStream(f);
            bw = new BufferedWriter(new OutputStreamWriter(fos));
            AnnotatedBarcodeCollection it = abs.computeAnnotatedIntervals(stream);
            //System.out.println(it);
            Iterator itt = it.getIntervalIterator();
            
            
            while(itt.hasNext()){
                String s = itt.next().toString();
                try {
                    Store_birthanddate(s);
                    bw.write(s);
                    bw.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
            /*
            I am going to write the birth and death arraylist into disk
            */
            File fbirdeath = new File(outputdir_path+kclosure+"homology"+".bd");
             FileOutputStream fos_bd = new FileOutputStream(fbirdeath);
            BufferedWriter bw_bd = new BufferedWriter(new OutputStreamWriter(fos_bd));
            for (Iterator<String> iterator = dim_birth_dataarray.iterator(); iterator.hasNext();) {
                String next = iterator.next();
                try {
                    bw_bd.write(next);
                     bw_bd.newLine();
                } catch (IOException ex) {
                    Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
            try {
                bw_bd.close();
            } catch (IOException ex) {
                Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            itt = it.getGeneratorIterator();
            while(itt.hasNext()){
                String s = itt.next().toString();
                try {
                    //System.out.println(s);
                    bw.newLine();
                    bw.write(s);
                    
                    
                } catch (IOException ex) {
                    Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
                }
               
            }
            try {
                bw.newLine();
                bw.write(circle_intervals.getBettiNumbers());
                bw.close();
                //this.bw.newLine();
            } catch (IOException ex) {
                Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
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

    private int addhigherelement(ExplicitSimplexStream stream, String simplicesout) {
        /*
        Read all the cliques from a file line by line and add it to the simplical complex
        */
        //System.out.println("Adding from: "+simplicesout);
        File f = new File(simplicesout);
        
        int maxclique = 1;
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader br;
            br = new BufferedReader(isr);
            String lineTxt = null;
            
            while((lineTxt = br.readLine()) != null){
               // System.out.println(lineTxt);
                String[] tokens = lineTxt.split(" ");
                int[] elem = new int[tokens.length];
                maxclique = (tokens.length>maxclique?tokens.length:maxclique);
                for (int i = 0; i < tokens.length; i++) {
                    elem[i] = Integer.valueOf(tokens[i]);
                }
                if(tokens.length == 1){
                    stream.addVertex(Integer.valueOf(tokens[0]),kclosure-1); // when we add 0-simplex or clique of size 1
                    //System.out.println(tokens[0]);
                }
                else 
                    stream.addElement(elem,kclosure-1);
                   /* for (int i = 0; i < elem.length; i++) {
                            System.out.print(elem[i]+" ");
                        }
                        System.out.println("");*/
                //System.out.println("hk: "+(kclosure-1));
            }
            System.out.println("stream size: "+stream.getSize());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maxclique;
        
         
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
            for(int i=1;i<=4;i++){
                String option = br.readLine();
                StringTokenizer st  = new StringTokenizer(option,"=");
                st.nextToken();
                switch(i){
                        case 1:                        
                            this.clique_base_filename  = st.nextToken(); // first line is cliqe file prefix
                            break;
                        case 2:
                            //Reading maxclosure
                            this.max_closure  = Integer.valueOf(st.nextToken());
                            break;
                        case 3: // graph prefix is not needed here
                            break;
                        case 4:
                            outputdir_path = st.nextToken();
                            break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    public ExplicitSimplexStream build_stream(int kth_closure) {
        kclosure = kth_closure;
        ExplicitSimplexStream stream = new ExplicitSimplexStream();
        //for(int i = 1;i<=kth_closure;i++)
        //initialize_streamfromgraph(stream,"graph"+kth_closure+".edges"); //for the tomita version
        addhigherelement(stream,outputdir_path+this.clique_base_filename+"_"+kth_closure+".out");
        stream.finalizeStream();
        this.maxdimension = max_closure;
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
        System.out.println("Betti numbers: "+circle_intervals.getBettiNumbers());
        generate_barcode_image(circle_intervals, maxdimension);
        generate_representative_cycle(this.stream, persistence,circle_intervals);
        //System.out.println(stream.validateVerbose());
        
    }
    void add_to_stream(String simplicesfile,int k){
        System.out.println("Adding from: "+simplicesfile);
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
                //System.out.println("hi: "+ (k-1)) ;
                if(tokens.length == 1){
                    if(!this.stream.containsElement(new Simplex(elem)))
                        this.stream.addVertex(Integer.valueOf(tokens[0]),k-1); // when we add 0-simplex or clique of size 1
                    //System.out.println(tokens[0]);
                }
                else{ 
                    if(!this.stream.containsElement(new Simplex(elem))){
                        /*for (int i = 0; i < elem.length; i++) {
                            System.out.print(elem[i]+" ");
                        }
                        System.out.println("");*/
                        this.stream.addElement(elem,k-1);
                    }
                    else{
                       // System.out.print("already exists: ");
                       /* for (int i = 0; i < elem.length; i++) {
                            System.out.print(elem[i]+" ");
                        }
                        System.out.println("");*/
                    }
                }
            }
            System.out.println("stream size: "+this.stream.getSize());
            
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        //this.stream.finalizeStream();
        
    }
    
    private void initialize_streamfromgraph(ExplicitSimplexStream stream, String simplicesout) {
        /*
        Read all the cliques from a file line by line and add it to the simplical complex
        */
        System.out.println("Adding from: "+simplicesout);
        File f = new File(simplicesout);
        
        
        try {
            InputStreamReader isr = new InputStreamReader(new FileInputStream(f));
            BufferedReader br;
            br = new BufferedReader(isr);
            String lineTxt = null;
            
            while((lineTxt = br.readLine()) != null){
               // System.out.println(lineTxt);
                String[] tokens = lineTxt.split(" ");
                int[] elem = new int[tokens.length];
               
                for (int i = 0; i < tokens.length; i++) {
                    elem[i] = Integer.valueOf(tokens[i]);
                    stream.addVertex(elem[i],kclosure-1); // when we add 0-simplex or clique of size 1
                    //System.out.println(tokens[0]);
                }
                
                    stream.addElement(elem,kclosure-1);
                    //for (int i = 0; i < elem.length; i++) {
                            //System.out.print(elem[i]+" ");
                      //  }
                        //System.out.println("");
                //System.out.println("hk: "+(kclosure-1));
            }
            System.out.println("stream size: "+stream.getSize());
        } catch (FileNotFoundException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BasicHomology_triangulation_trans.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
         
    }

    private void Store_birthanddate(String s) {
        /*
        Returns a List for instance [0,[a,b),[c,d)] or [1,[x,y)] . 
        The first item is always the dimension of the complex and 
        next the birth and death intervals of the simplices on that dimension.        
        */
        
        double birth,data;
        StringTokenizer st = new StringTokenizer(s, "=");
        String dim = st.nextToken().trim();
        String pattern = "\\[(.*?)\\]";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        
        
        int i=0;
        while(m.find()){
            String the_interval_asstring = m.group().substring(2, m.group().length()-2);
            
            String birth_death_ar[] = the_interval_asstring.split(",");
            if(birth_death_ar[1].trim().equals("infinity"))
                birth_death_ar[1] = String.valueOf((double)maxdimension);
            dim_birth_dataarray.add(dim+" "+birth_death_ar[0].trim()+" "+birth_death_ar[1].trim());
            //System.out.println("match found: "+m.group().substring(2, m.group().length()-2));
           // System.out.println(dim+" "+birth_death_ar[0].trim()+" "+birth_death_ar[1].trim());
        }
       
    }


}
