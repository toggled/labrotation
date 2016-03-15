/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package RandomGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author naheed
 */
public class Graph_writer {
    //write the graph provided by the adjacency matrix as an edgelist
    static void write_graph(int adj_mat[][],String filename,String option){
        File fobj;
        FileWriter fwriterobj = null;
        BufferedWriter writer = null ;
        try {
            fobj = new File(filename);
            fwriterobj = new FileWriter(fobj);
             writer = new BufferedWriter(fwriterobj);
            if(option.equalsIgnoreCase("edgelist")){
                //System.out.println("hi"+adj_mat.length);
                //write the graph as an edgelist.
                for(int i = 0 ; i<adj_mat.length; i++){
                    for (int j = i; j < adj_mat[i].length; j++) {
                        if(adj_mat[i][j]==1){
                            writer.write(String.valueOf(i)+" "+String.valueOf(j)+"\n");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Graph_writer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                //fwriterobj.close();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Graph_writer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    // Use this function when you sample the graph, Since some of the nodes might be disconnected
    // from others and adjacency matrix carry info about edges only.
    static void write_graph(boolean adj_mat[][],String filename,String option,int []nodelist){
        File fobj;
        FileWriter fwriterobj = null;
        BufferedWriter writer = null ;
        try {
            fobj = new File(filename);
            fwriterobj = new FileWriter(fobj);
             writer = new BufferedWriter(fwriterobj);
            if(option.equalsIgnoreCase("edgelist")){
                //System.out.println("hi"+adj_mat.length);
                //write the graph as an edgelist.
                for (int o : nodelist) {
                    writer.write(o+"\n");
                }
                for(int i = 0 ; i<adj_mat.length; i++){
                    for (int j = i; j < adj_mat[i].length; j++) {
                        if(adj_mat[i][j]){
                            writer.write(String.valueOf(i)+" "+String.valueOf(j)+"\n");
                        }
                    }
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Graph_writer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                //fwriterobj.close();
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(Graph_writer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
