/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geodesic_distance_metric_persistence;

import Transitive_closure_homology.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import jplex_explore.Graph3;
import jplex_explore.ReadFromFiles;
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
	private boolean ajacentMatrix[][] = null;
	private int degreeArray[] = null;
	private int vertexCountWithSameDegree[] = null;
        FileOutputStream fos;
        FileWriter fileWriter;
        int cliquecount;
 
        boolean stableflag = false;
        int k_closure = 1;
        static Graph_writer gwriter = new Graph_writer();
        static String graph_base_filename = "graph";
        String clique_base_filename = "clique";
        File gdistf = new File("geodist.dist");
        FileWriter gdistfw;
        BufferedWriter gdistbw;
    public static void main(String[] args) {
        // TODO code application logic here
        //String filename = "datasets/0 (copy).edges";
        String filename = "datasets/testcase_2.edges";
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
            //g.printadjmat();
            //g.compute_degre();
            //g.getAllCliques();
            gwriter.write_graph(g.ajacentMatrix,graph_base_filename+g.k_closure+".edges","edgelist");
            while(true){    
                    if(g.stableflag)    break;
                    else{
                        g.k_closure++;
                        g.compute_transitive_closure();

                        gwriter.write_graph(g.ajacentMatrix,graph_base_filename+g.k_closure+".edges","edgelist");
                       // g.compute_degre(); // compute degree each time before you run cliqe algorithm
                        //g.init_cliquewriter(g.k_closure);
                        //g.getAllCliques();
                        System.out.println(g.k_closure +"-th closure:\n");
                       // g.printadjmat();
                    }
                    
            }
            g.close_geodistwriter();
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public Iterative_trans_closure(String fname) {
            init_geodistwriter();
            
		// TODO Auto-generated constructor stub     
            this.graphFname = fname;
    }
    	public void compute_transitive_closure(){
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
                                    this.add_vertextovertex_dist(row+1,i+1,this.k_closure);
                                    changed = true;
                                }                               
                            }
                        }
                    }
                }
            }
            if(!changed)    this.stableflag = true;
        }
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
                //System.out.println(vertexCount);
                formadjacency_mat(lines,vertexCount);
                //compute_degre();
		return true;
	}
	public void formadjacency_mat(Vector<String> lines,int vertexCount){

		//initialize adjacent matrix
                ajacentMatrix = new boolean[vertexCount][vertexCount];
                int[] flag_file = new int[vertexCount+1]; // flag for checking whether a vertex has been writen as simplex in the file or not
		for(int i = 0; i < vertexCount; i++){
			for(int j = 0; j < vertexCount; j++){
				ajacentMatrix[i][j] = false; 
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
			ajacentMatrix[sourceNodeIndex - 1][targetNodeIndex - 1] = true;
			ajacentMatrix[targetNodeIndex - 1][sourceNodeIndex - 1] = true;
                    try {
                        this.gdistbw.write(tokens[0]+" "+tokens[1]+" "+"1\n");
                        this.gdistbw.write(tokens[1]+" "+tokens[0]+" "+"1\n");
                    } catch (IOException ex) {
                        Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
                    }
                        
                        if ( flag_file[sourceNodeIndex] == 0){ 
                            flag_file[sourceNodeIndex] = 1;
                          
                        }
                        if ( flag_file[targetNodeIndex] == 0){ 
                            flag_file[targetNodeIndex] = 1;
                           
                        }                  
		}
		
        }

        private void printadjmat(){
            for (int i = 0; i < this.ajacentMatrix.length; i++) {
                for (int j = 0; j < this.ajacentMatrix[0].length; j++) {
                    if(this.ajacentMatrix[i][j])
                        System.out.print("1 ");
                    else
                        System.out.print("0 ");
                }
                System.out.println("");
            }
            System.out.println("");
        }

    private void init_geodistwriter() {
            try {
                 this.gdistfw = new FileWriter(gdistf);
                 this.gdistbw = new BufferedWriter(this.gdistfw);
                 
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Graph3.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Graph3.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    private void add_vertextovertex_dist(int u,int v,int geodist){
        try {
            this.gdistbw.write(String.valueOf(u)+" "+String.valueOf(v)+" "+String.valueOf(geodist));
            this.gdistbw.newLine();
             this.gdistbw.write(String.valueOf(v)+" "+String.valueOf(u)+" "+String.valueOf(geodist));
            this.gdistbw.newLine();
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
}
