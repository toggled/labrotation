/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geodesic_distance_metric_persistence_vrips_incomplete;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author naheed
 */
public class Iterative_trans_closure {

    /**
     * @param args the command line arguments
     */
    String graphFname = null;
	private int vertexCount = 0;
	private int ajacentMatrix[][] = null;
        private int geodist_Matrix[][] = null;
	private int degreeArray[] = null;
	private int vertexCountWithSameDegree[] = null;
        FileOutputStream fos;
        FileWriter fileWriter;
        int cliquecount;
 
        boolean stableflag = false;
        int k_closure = 1;
        //static Graph_writer gwriter = new Graph_writer();
        static String graph_base_filename = "graph";
        
        File gdistf = new File("geodist.dist");
        FileWriter gdistfw;
        BufferedWriter gdistbw;
        File gdist_matfile = new File("geodistMatrix.dist");
        FileWriter gdistfw_mat;
        BufferedWriter gdistbw_mat;
        
    public static void main(String[] args) {
        // TODO code application logic here
        //String filename = "../datasets/0 (copy).edges";
        String filename = "../datasets/testcase_2.edges";
        //String filename = "../datasets/3437.edges";
        //String filename = "../datasets/newdata.edges";
            //String filename = "CA-GrQc.txt";
                 
		// TODO Auto-generated method stub
		/*if(args.length != 1){
			System.err.println("example command: java -cp ./ Graph graph-file");
			return ;
		}*/
                
		Iterative_trans_closure g = new Iterative_trans_closure(filename);
                
        try {
            if(!g.init())
                    return;
             g.printadjmat();
            //g.compute_degre();
            //g.getAllCliques();
           // gwriter.write_graph(g.ajacentMatrix,graph_base_filename+g.k_closure+".edges","edgelist");
            while(true){    
                    if(g.stableflag)    break;
                    else{
                        g.k_closure++;
                        g.compute_transitive_closure();

                       // gwriter.write_graph(g.ajacentMatrix,graph_base_filename+g.k_closure+".edges","edgelist");
                       // g.compute_degre(); // compute degree each time before you run cliqe algorithm
                        //g.init_cliquewriter(g.k_closure);
                        //g.getAllCliques();
                        System.out.println(g.k_closure +"-th closure:\n");
                        g.printadjmat();
                    }
                    
            }
            g.close_geodistwriter();
            g.gen_configfile();
            g.write_geodistmatrix(); // Write the whole distance Matrix for post-processing
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Iterative_trans_closure(String fname) {
            init_geodistwriter();
            
		// TODO Auto-generated constructor stub     
            this.graphFname = fname;
    }
    	/*public void compute_transitive_closure(){
            boolean changed = false;
            boolean [][] copy_adjmat = new boolean[ajacentMatrix.length][];
            for(int i = 0; i < ajacentMatrix.length; i++)
                copy_adjmat[i] = ajacentMatrix[i].clone();
            
            for (int row = 0; row < copy_adjmat.length; row++) {
                //boolean[] ajacentMatrix1 = copy_adjmat[row];
                for (int col = 0; col < copy_adjmat[0].length; col++) {
                    if(row==col) continue;
                    //boolean b = ajacentMatrix1[col];
                    
                    if(copy_adjmat[row][col]==true){
                        boolean[] columnsadjacency = copy_adjmat[col];
                        for (int i = col; i < columnsadjacency.length ; i++) {
                            if(i==row) continue;
                            if(columnsadjacency[i] == true){
                                if(this.ajacentMatrix[row][i] == false){
                                    this.ajacentMatrix[row][i] = true;
                                    this.ajacentMatrix[i][row] = true;
                                    this.add_vertextovertex_dist(row,i,this.k_closure);
                                    changed = true;
                                }                               
                            }
                        }
                    }
                }
            }
            if(!changed)    this.stableflag = true;
        }
    */
	public boolean init() throws IOException{
		ReadFromFiles reader = new ReadFromFiles();
		Vector<String> lines = new Vector<String>();
		if(!reader.readin(graphFname, lines)){
			System.err.println("Failed to read file: " + graphFname);
			return false;
		}else if(lines.size() <= 1){
			System.err.println("at least one edge needed");
			return false;
		}
                //get vertex count
		//vertexCount = Integer.parseInt(lines.get(0));
                
                vertexCount = reader.getVertexCount();
               // System.out.println(vertexCount);
                formadjacency_mat(lines,vertexCount);
                //compute_degre();
		return true;
	}
    public void compute_transitive_closure(){
        stableflag = true;
        int [][] copy_adjmat = new int[ajacentMatrix.length][];
            for(int i = 0; i < ajacentMatrix.length; i++)
                copy_adjmat[i] = ajacentMatrix[i].clone();
        //this.printadjmat();
        for(int row = 0; row< copy_adjmat[0].length; row++){
            
            for (int col = 0; col < copy_adjmat[0].length; col++) {
                int sum = 0;
                if(row!=col){
                    for (int row2 = 0; row2 < copy_adjmat[0].length; row2++) {

                        sum += (copy_adjmat[row][row2]*copy_adjmat[row2][col]) ;

                    }
                //System.out.print(sum+" ");
                    if( sum > 0){
                        if(ajacentMatrix[row][col]!=1){
                            ajacentMatrix[row][col] = 1 ; //Adding new edge here
                            this.add_vertextovertex_dist(row,col,this.k_closure);
                            //System.out.println(row+"-"+col+":"+this.k_closure);
                            stableflag = false;
                            }
                    }
                }
            }  
            
            //System.out.println();
            //System.exit(1);
        }
       //this.printadjmat();
      // System.exit(1);
    }
    
	public void formadjacency_mat(Vector<String> lines,int vertexCount){

		//initialize adjacent matrix
                ajacentMatrix = new int[vertexCount][vertexCount];
                geodist_Matrix = new int[vertexCount][vertexCount];
                int[] flag_file = new int[vertexCount+1]; // flag for checking whether a vertex has been writen as simplex in the file or not
		for(int i = 0; i < vertexCount; i++){
			for(int j = 0; j < vertexCount; j++){
				ajacentMatrix[i][j] = 0; 
                                geodist_Matrix[i][j] = 0;
			}
		}
		for(int i = 0; i < lines.size(); i++){
			String[] tokens = lines.get(i).split(" ");
			if(tokens.length != 2){
				System.err.println("the format of each line/vertex: \"source-node-index target-node-index\"");
				
			}
			int sourceNodeIndex = Integer.parseInt(tokens[0]);
			int targetNodeIndex = Integer.parseInt(tokens[1]);
                        //System.out.println(lines.get(i));
			ajacentMatrix[sourceNodeIndex][targetNodeIndex] = geodist_Matrix[sourceNodeIndex][targetNodeIndex]= 1;
			ajacentMatrix[targetNodeIndex][sourceNodeIndex] = geodist_Matrix[targetNodeIndex][sourceNodeIndex]=1;
                    try {
                        this.gdistbw.write(tokens[0]+" "+tokens[1]+" "+"1\n");
                        this.gdistbw.write(tokens[1]+" "+tokens[0]+" "+"1\n");
                    } catch (IOException ex) {
                        Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
                        if ( flag_file[sourceNodeIndex] == 0){ 
                            flag_file[sourceNodeIndex] = 1;
                            try {
                                this.gdistbw.write(tokens[0]+" "+tokens[0]+" "+"0\n");
                            } catch (IOException ex) {
                                Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        if ( flag_file[targetNodeIndex] == 0){ 
                            flag_file[targetNodeIndex] = 1;
                            try {
                                this.gdistbw.write(tokens[1]+" "+tokens[1]+" "+"0\n");
                            } catch (IOException ex) {
                                Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }                  
		}
		
        }

        private void printadjmat(){
            for (int i = 0; i < this.ajacentMatrix.length; i++) {
                for (int j = 0; j < this.ajacentMatrix[0].length; j++) {
                    System.out.print(ajacentMatrix[i][j]+" ");
                }
                System.out.println("");
            }
            System.out.println("");
        }

    private void init_geodistwriter() {
           
        try {
            this.gdistfw = new FileWriter(gdistf);
            this.gdistbw = new BufferedWriter(this.gdistfw);
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }
                 

    }
    private void init_geodistwriter_matrix() {
           
        try {
            this.gdistfw_mat = new FileWriter(gdist_matfile);
            this.gdistbw_mat = new BufferedWriter(this.gdistfw_mat);
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }
                 

    }
    private void add_vertextovertex_dist(int u,int v,int geodist){
        geodist_Matrix[u][v] = geodist;
        try {
            this.gdistbw.write(String.valueOf(u)+" "+String.valueOf(v)+" "+String.valueOf(geodist));
            this.gdistbw.newLine();
            //this.gdistbw.write(String.valueOf(v)+" "+String.valueOf(u)+" "+String.valueOf(geodist));
            //this.gdistbw.newLine();
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void close_geodistwriter(){
        try {
            this.gdistbw.close();
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void close_geodistwriter_mat(){
        try {
            this.gdistbw_mat.close();
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    private void write_geodistmatrix(){
        init_geodistwriter_matrix();
        for (int i = 0; i < geodist_Matrix[0].length; i++) {
            try {
                for (int j = 0; j < geodist_Matrix[i].length; j++) {

                        this.gdistbw_mat.write(geodist_Matrix[i][j]+" ");
                    
                }
                this.gdistbw_mat.newLine();
            } catch (IOException ex) {
                Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        close_geodistwriter_mat();
    }
    private void gen_configfile(){
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            File f = new File("cliquecon.cfg");
            fw = new FileWriter(f);
             bw = new BufferedWriter(fw);
            bw.write("numvertices="+vertexCount+"\n");
            bw.write("maxclosure="+String.valueOf(this.k_closure-1)+"\n");
            bw.write("graphfile="+graph_base_filename+"\n");
        } catch (IOException ex) {
            Logger.getLogger(Transitive_closure_homology.Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
            
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Transitive_closure_homology.Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
