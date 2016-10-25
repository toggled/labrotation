package Transitive_closure_homology;

import jplex_explore.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;


public class ReadFromFiles {
        static Hashtable<Integer, Integer> mapping = new Hashtable< Integer, Integer>();  // mapping function from graph id given as input to internal representation.
                                                                                    // coz graph id won't always be starting with 1. so need some internal representation.
	public boolean readin(String inFname, Vector<String> lines) throws IOException{
		File file = new File(inFname);
		if(file.isFile() && file.exists()){
			InputStreamReader read = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
                        int id = 0; // internal representation of the vertex id's (starts from 1)
                        String delem = " ";
			while((lineTxt = bufferedReader.readLine()) != null){
                            if(id>= Integer.MAX_VALUE)  break; // Java cannot handle 2D array larger than 2^31-1
                            String[] tokens = null;
                            //  String[] tokens = lineTxt.split(" ");
                            StringTokenizer st = new StringTokenizer(lineTxt, delem);
                            if(st.countTokens()<2)
                                continue;
                            //System.out.println(st.nextToken("\t"));
                            //tokens[0] = st.nextToken("\t");
                            //tokens[1] = st.nextToken("\t");
                            //System.out.println(tokens[0]+" "+tokens[1]);
                            int sourceNodeIndex = Integer.parseInt(st.nextToken(delem));
                            //System.out.println(sourceNodeIndex);
                            int targetNodeIndex = Integer.parseInt(st.nextToken(delem));
                            //System.out.println(targetNodeIndex);
                            if(mapping.getOrDefault(sourceNodeIndex, -1) ==-1 ){
                                //if (id == 915) System.out.println(sourceNodeIndex);
                                mapping.put(sourceNodeIndex, id++);
                                //mapping.put(sourceNodeIndex, sourceNodeIndex);
                                
                            }
                            if(mapping.getOrDefault(targetNodeIndex, -1) ==-1 ){
                                //if (id == 915) System.out.println(targetNodeIndex);
                                mapping.put(targetNodeIndex, id++);
                                //mapping.put(targetNodeIndex, targetNodeIndex);
                                
                            }
                            
                            lines.add(new String(mapping.get(sourceNodeIndex)+" "+mapping.get(targetNodeIndex)));
                        }
			read.close();
			return true;
		}else{
			System.err.println("can not find input file: " + inFname);
			return false;
		}
	}
        public int getVertexCount(){
            // the number of vertices in the graph. This function reduces the need for manually giving
             //number of vertices as input in the first line (which usually isn't given in the social graph datasets)
            return mapping.size(); 
        }
        
}
