package jplex_explore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Graph3 {
	String graphFname = null;
	private int vertexCount = 0;
	private boolean ajacentMatrix[][] = null;
	private int degreeArray[] = null;
	private int vertexCountWithSameDegree[] = null;
        File f = new File("simplices.out");
        FileOutputStream fos;
        BufferedWriter bw;
        FileWriter fileWriter;
        int cliquecount;
        BufferedWriter writer ;
	public static void main(String[] args) throws IOException{
                String filename = "datasets/0.edges";
		// TODO Auto-generated method stub
		/*if(args.length != 1){
			System.err.println("example command: java -cp ./ Graph graph-file");
			return ;
		}*/
		Graph3 g = new Graph3(filename);
		if(g.init())
			g.getAllCliques();
                
	}
	public Graph3(String fname) {
            try {
                //this.fos = new FileOutputStream(this.f);
                //this.bw = new BufferedWriter(new OutputStreamWriter(this.fos));
                this.fileWriter = new FileWriter(f);
                this.writer = new BufferedWriter(fileWriter);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Graph3.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(Graph3.class.getName()).log(Level.SEVERE, null, ex);
            }
		// TODO Auto-generated constructor stub
		this.graphFname = fname;
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
				return false;
			}
			int sourceNodeIndex = Integer.parseInt(tokens[0]);
			int targetNodeIndex = Integer.parseInt(tokens[1]);
                        //System.out.println(lines.get(i));
			ajacentMatrix[sourceNodeIndex - 1][targetNodeIndex - 1] = true;
			ajacentMatrix[targetNodeIndex - 1][sourceNodeIndex - 1] = true;
                        if ( flag_file[sourceNodeIndex] == 0){ 
                            flag_file[sourceNodeIndex] = 1;
                            this.writer.write(tokens[0]+"\n");
                        }
                        if ( flag_file[targetNodeIndex] == 0){ 
                            flag_file[targetNodeIndex] = 1;
                            this.writer.write(tokens[1]+"\n");
                        }
                        this.writer.write(tokens[0]+" "+tokens[1]+"\n");
		}
		//initialize degree array;
		degreeArray = new int[vertexCount];
		for(int i = 0; i < vertexCount; i++){
			degreeArray[i] = 0;
			for(int j = 0; j < vertexCount; j++){
				if(ajacentMatrix[i][j])
					degreeArray[i]++;
			}
			//System.out.println(degreeArray[i]);
		}
		//get maximum degree
		int maximumDegree = 0;
		for(int i = 0; i < vertexCount; i++)
			if(degreeArray[i] > maximumDegree)
				maximumDegree = degreeArray[i];
		//initialize vertex count with same degree
		vertexCountWithSameDegree = new int[maximumDegree];
		for(int i = 0; i < maximumDegree; i++)
			vertexCountWithSameDegree[i] = 0;
		for(int i = 0; i < vertexCount; i++){
			vertexCountWithSameDegree[degreeArray[i] - 1]++;
		}
		return true;
	}
	
	public void getAllCliques() throws IOException{
		for(int i = 1; i < vertexCountWithSameDegree.length; i++){
			//check whether enough vertexes with enough degree exist
			int candidateVertexesCount = 0;
			for(int j = i; j < vertexCountWithSameDegree.length; j++)
				candidateVertexesCount+=vertexCountWithSameDegree[j];
			if(candidateVertexesCount >= i + 2){
				System.out.println("cliques with vertexes: " + (i + 2));
				Vector<Integer> vertexes = new Vector<Integer>();
				for(int j = 0; j < vertexCount; j++){
					if(degreeArray[j] >= (i + 1))
						vertexes.add(j);
				}
				int[] cliqueVertexes = new int[i + 2];
				System.out.println("candidate vertexes count: " + vertexes.size());
                                cliquecount = 0;
				getCliquesWithSpecificDegree(0, 0, cliqueVertexes, vertexes);
                                System.out.println(cliquecount); //checking the cliquecount. It clearly mismatches
                                                            // the number of line in the output file.
			}
		}
            try {
                //this.bw.flush();
                //this.bw.close();
                this.writer.close();
		//this.bw.close();
                
            } catch (IOException ex) {
                Logger.getLogger(Graph3.class.getName()).log(Level.SEVERE, null, ex);
            }
	}
	private void getCliquesWithSpecificDegree(int index, int start, int[] cliqueVertexes, Vector<Integer> candidateVertexes) throws IOException{
		if(index < cliqueVertexes.length){
			for(int i = start; i <= candidateVertexes.size() - (cliqueVertexes.length - index); i++){
				int currentVertex = candidateVertexes.get(i);
				boolean connectedWithFrontVertexes = true;
				for(int j = 0; j < index; j++){
					if(!ajacentMatrix[cliqueVertexes[j]][currentVertex]){
						connectedWithFrontVertexes = false;
						break;
					}
				}
				if(!connectedWithFrontVertexes)
					continue;
				cliqueVertexes[index] = candidateVertexes.get(i);
				getCliquesWithSpecificDegree(index + 1, i + 1, cliqueVertexes, candidateVertexes);
			}
		}else{
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
			if(fullConnected){
                                String vertices_str = "";
				for(int i = 0; i < cliqueVertexes.length; i++){
                                        vertices_str += Integer.toString(cliqueVertexes[i] + 1) + " ";
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
}
