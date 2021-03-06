/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Transitive_closure_homology;

import static Transitive_closure_homology.Iterative_trans_closure_forpython.filename;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
    private boolean ajacentMatrix[][] = null;
    private int degreeArray[] = null;
    private int vertexCountWithSameDegree[] = null;
    File f = new File("cliques.out");
    FileOutputStream fos;
    FileWriter fileWriter;
    int cliquecount;
    BufferedWriter writer;
    boolean stableflag = false;
    int k_closure = 1;
    static Graph_writer gwriter = new Graph_writer();
    static String graph_base_filename = "graph";
    String clique_base_filename = "clique";
    static String filename, Directory_name, working_dir, fullpath_output;
    static int[]  nodelist;
    public static void main(String[] args) {
        // TODO code application logic here
        // filename = "../datasets/0 (copy).edges";
        // filename = "../datasets/friends.txt";
        // filename = "../datasets/3437.edges";
        //filename = "../datasets/testcase_2.edges";
        filename = "/Users/naheed/NetBeansProjects/Toy-2 262143/graph1.edges";
        // filename = "../datasets/newdata.edges";
        // filename = "CA-GrQc.txt";
        parsefilename();
		// TODO Auto-generated method stub
		/*if(args.length != 1){
         System.err.println("example command: java -cp ./ Graph graph-file");
         return ;
         }*/
        Iterative_trans_closure g = new Iterative_trans_closure(filename);

        try {
            if (!g.init()) {
                return;
            }
            //g.printadjmat();

            g.compute_degre();
            g.getAllCliques();
            gwriter.write_graph(g.ajacentMatrix, fullpath_output + graph_base_filename + g.k_closure + ".edges", "edgelist",nodelist);
            for (;;) {

                g.compute_transitive_closure();
                if (g.stableflag) {
                    break;
                }
                if(g.k_closure>2)                            
                    break;
                g.k_closure++;
                
                gwriter.write_graph(g.ajacentMatrix, fullpath_output + graph_base_filename + g.k_closure + ".edges", "edgelist",nodelist);
                g.compute_degre(); // compute degree each time before you run cliqe algorithm
                g.init_cliquewriter(g.k_closure);
                g.getAllCliques();
                System.out.println(g.k_closure + "-th closure:\n");
                    //g.printadjmat();

            }
            g.gen_configfile();
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static void parsefilename() {
        String[] segs = filename.split("/");
        String[] dotseg = segs[segs.length - 1].split("\\.");
        Directory_name = dotseg[0];
        //System.out.println(Directory_name);
        working_dir = System.getProperty("user.dir");
        fullpath_output = working_dir + "/" + Directory_name + "/";

    }

    public Iterative_trans_closure(String fname) {
        init_cliquewriter(k_closure);

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
                    
     if(copy_adjmat[row][col]==true){
     boolean[] columnsadjacency = copy_adjmat[col];
     for (int i = col; i < columnsadjacency.length ; i++) {
     if(i==row) continue;
     if(columnsadjacency[i] == true){
     if(this.ajacentMatrix[row][i] == false){
     this.ajacentMatrix[row][i] = true;
     this.ajacentMatrix[i][row] = true;
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

    public boolean init() throws IOException {
        ReadFromFiles reader = new ReadFromFiles();
        Vector<String> lines = new Vector<String>();
        if (!reader.readin(graphFname, lines)) {
            System.err.println("Failed to read file: " + graphFname);
            return false;
        } else if (lines.size() <= 1) {
            System.err.println("at least one edge needed");
            return false;
        }
                //get vertex count
        //vertexCount = Integer.parseInt(lines.get(0));

        vertexCount = reader.getVertexCount();
        //System.out.println(vertexCount);
        formadjacency_mat(lines, vertexCount);
        compute_degre();
        return true;
    }

    public void compute_transitive_closure() {
        stableflag = true;
        boolean[][] copy_adjmat = new boolean[ajacentMatrix.length][];
        for (int i = 0; i < ajacentMatrix.length; i++) {
            copy_adjmat[i] = ajacentMatrix[i].clone();
        }
        //this.printadjmat();
        for (int row = 0; row < copy_adjmat[0].length; row++) {

            for (int col = 0; col < copy_adjmat[0].length; col++) {
                int sum = 0;
                if (row != col) {
                    for (int row2 = 0; row2 < copy_adjmat[0].length; row2++) {
                        if (copy_adjmat[row][row2] & copy_adjmat[row2][col]) {
                            sum++;
                        }
                    }
                    //System.out.print(sum+" ");
                    if (sum > 0) {
                        if (!ajacentMatrix[row][col]) {
                            ajacentMatrix[row][col] = true;
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

    public void formadjacency_mat(Vector<String> lines, int vertexCount) {

        //initialize adjacent matrix
        ajacentMatrix = new boolean[vertexCount][vertexCount];
        nodelist = new int[vertexCount];
        int[] flag_file = new int[vertexCount + 1]; // flag for checking whether a vertex has been writen as simplex in the file or not
        for (int i = 0; i < vertexCount; i++) {
            for (int j = 0; j < vertexCount; j++) {
                ajacentMatrix[i][j] = false;
            }
        }
        for (int count=0, i = 0; i < lines.size(); i++) {
            String[] tokens = lines.get(i).split(" ");
            if (tokens.length != 2) {
                System.err.println("the format of each line/vertex: \"source-node-index target-node-index\"");

            }
            int sourceNodeIndex = Integer.parseInt(tokens[0]);
            int targetNodeIndex = Integer.parseInt(tokens[1]);
            //System.out.println(lines.get(i));

            ajacentMatrix[sourceNodeIndex][targetNodeIndex] = true;
            ajacentMatrix[targetNodeIndex][sourceNodeIndex] = true;
            if (flag_file[sourceNodeIndex] == 0) {
                flag_file[sourceNodeIndex] = 1;
                try {
                    this.writer.write(tokens[0] + "\n");
                    
                } catch (IOException ex) {
                    Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
                }
                nodelist[count++]=sourceNodeIndex;
            }
            if (flag_file[targetNodeIndex] == 0) {
                flag_file[targetNodeIndex] = 1;
                try {
                    this.writer.write(tokens[1] + "\n");
                } catch (IOException ex) {
                    Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
                }
                nodelist[count++]=targetNodeIndex;
            }
          /*  try {
                this.writer.write(tokens[0] + " " + tokens[1] + "\n");
            } catch (IOException ex) {
                Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
            }*/
        }

    }

    public void compute_degre() {
        //initialize degree array;
        degreeArray = new int[vertexCount];
        for (int i = 0; i < vertexCount; i++) {
            degreeArray[i] = 0;
            for (int j = 0; j < vertexCount; j++) {
                if (ajacentMatrix[i][j]) {
                    degreeArray[i]++;
                }
            }
            //System.out.println(degreeArray[i]);
        }
        //get maximum degree
        int maximumDegree = 0;
        for (int i = 0; i < vertexCount; i++) {
            if (degreeArray[i] > maximumDegree) {
                maximumDegree = degreeArray[i];
            }
        }
        //initialize vertex count with same degree
        vertexCountWithSameDegree = new int[maximumDegree + 1];
        for (int i = 0; i < maximumDegree; i++) {
            vertexCountWithSameDegree[i] = 0;
        }
        for (int i = 0; i < vertexCount; i++) {
            vertexCountWithSameDegree[degreeArray[i]]++;
        }
    }

    public void getAllCliques() throws IOException {
        for (int i = 0; i < vertexCountWithSameDegree.length; i++) {
            //check whether enough vertexes with enough degree exist
            int candidateVertexesCount = 0;
            for (int j = i; j < vertexCountWithSameDegree.length; j++) {
                candidateVertexesCount += vertexCountWithSameDegree[j];
            }
            if (candidateVertexesCount >= i + 2) {
                System.out.println("cliques with vertexes: " + (i + 2));
                Vector<Integer> vertexes = new Vector<Integer>();
                for (int j = 0; j < vertexCount; j++) {
                    if (degreeArray[j] >= (i + 1)) {
                        vertexes.add(j);
                    }
                }
                int[] cliqueVertexes = new int[i + 2];
                System.out.println("candidate vertexes count: " + vertexes.size());
                cliquecount = 0;
                getCliquesWithSpecificDegree(0, 0, cliqueVertexes, vertexes);
                System.out.println(cliquecount); //checking the cliquecount. It clearly mismatches
                // the number of line in the output file.
            }
        }

                //this.bw.flush();
        //this.bw.close();
        this.writer.close();
		//this.bw.close();

    }

    private void getCliquesWithSpecificDegree(int index, int start, int[] cliqueVertexes, Vector<Integer> candidateVertexes) throws IOException {
        if (index < cliqueVertexes.length) {
            for (int i = start; i <= candidateVertexes.size() - (cliqueVertexes.length - index); i++) {
                int currentVertex = candidateVertexes.get(i);
                boolean connectedWithFrontVertexes = true;
                for (int j = 0; j < index; j++) {
                    if (!ajacentMatrix[cliqueVertexes[j]][currentVertex]) {
                        connectedWithFrontVertexes = false;
                        break;
                    }
                }
                if (!connectedWithFrontVertexes) {
                    continue;
                }
                cliqueVertexes[index] = candidateVertexes.get(i);
                getCliquesWithSpecificDegree(index + 1, i + 1, cliqueVertexes, candidateVertexes);
            }
        } else {
            //check whether it is clique
            boolean fullConnected = true;
            /*for(int i = 0; i < cliqueVertexes.length; i++){
             for(int j = i + 1; j < cliqueVertexes.length; j++){
             if(!ajacentMatrix[cliqueVertexes[i]][cliqueVertexes[j]]){
             fullConnected = false;
             break;
             }
             }
             if(!fullConnected)
             break;
             }*/
            if (fullConnected) {
                String vertices_str = "";
                for (int i = 0; i < cliqueVertexes.length; i++) {
                    vertices_str += Integer.toString(cliqueVertexes[i]) + " ";
					//System.out.print((cliqueVertexes[i] + 1) + " ");

                }
                                //System.out.println(vertices_str); //what i am writing in the console
                //this.bw.write(vertices_str);
                //this.bw.newLine();
                this.writer.write(vertices_str); // What i am writing in the output
                cliquecount++;
                this.writer.newLine();
                this.writer.flush();
            }
        }
    }

    private void printadjmat() {
        for (int i = 0; i < this.ajacentMatrix.length; i++) {
            for (int j = 0; j < this.ajacentMatrix[0].length; j++) {
                if (ajacentMatrix[i][j]) {
                    System.out.print(1 + " ");
                } else {
                    System.out.print(0 + " ");
                }
            }
            System.out.println("");
        }
        System.out.println("");
    }

    private void init_cliquewriter(int k) {

        try {
            File dir = new File(fullpath_output);
            if (dir.exists()) {
                this.f = new File(fullpath_output + clique_base_filename + "_" + k + ".out");
                this.fileWriter = new FileWriter(f);
                this.writer = new BufferedWriter(fileWriter);
            } else {
                // attempt to create the directory here
                if (dir.mkdir()) {
                    //this.fos = new FileOutputStream(this.f);
                    //this.bw = new BufferedWriter(new OutputStreamWriter(this.fos));
                    this.f = new File(fullpath_output+clique_base_filename + "_" + k + ".out");
                    this.fileWriter = new FileWriter(f);
                    this.writer = new BufferedWriter(fileWriter);
                } else {
                    System.out.println("Cannot create output directory! Exiting.. \n");
                    System.exit(1);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void gen_configfile() {
        FileWriter fw = null;
        BufferedWriter bw = null;
        try {
            File f = new File("cliquecon.cfg");
            fw = new FileWriter(f);
            bw = new BufferedWriter(fw);
            bw.write("cliquefile=" + this.clique_base_filename + "\n");

            bw.write("maxclosure=" + String.valueOf(this.k_closure) + "\n");
            bw.write("graphfile=" + graph_base_filename + "\n");
            bw.write("outputdir=" + fullpath_output);
        } catch (IOException ex) {
            Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {

                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(Iterative_trans_closure.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
