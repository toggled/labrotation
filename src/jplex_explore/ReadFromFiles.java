package jplex_explore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Vector;


public class ReadFromFiles {
        Hashtable<Integer, Integer> mapping = new Hashtable< Integer, Integer>();
	public boolean readin(String inFname, Vector<String> lines) throws IOException{
		File file = new File(inFname);
		if(file.isFile() && file.exists()){
			InputStreamReader read = new InputStreamReader(new FileInputStream(file));
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
                        int id = 1;
			while((lineTxt = bufferedReader.readLine()) != null){
                            String[] tokens = lineTxt.split(" ");
                            int sourceNodeIndex = Integer.parseInt(tokens[0]);
                            int targetNodeIndex = Integer.parseInt(tokens[1]);
                            if(mapping.getOrDefault(sourceNodeIndex, -1) ==-1 ){
                                mapping.put(sourceNodeIndex, id++);
                            }
                            if(mapping.getOrDefault(targetNodeIndex, -1) ==-1 ){
                                mapping.put(targetNodeIndex, id++);
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
            return mapping.size();
        }
        
}
