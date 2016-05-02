package Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author naheed
 */
public class FileFolder {
    public static boolean CreateFolderifnotexists(String path,String foldername){
        File dir = new File(path+"/"+foldername);
        if(!dir.exists()){
                return dir.mkdir();
        }
        return true;
    }
    public static boolean CreateFileifnotexists(String filename,String path){
        File newfile = new File(path+"/"+filename);
        FileWriter fileWriter;
        BufferedWriter writer;
        
        if(!newfile.exists()){
            try {
                fileWriter = new FileWriter(newfile);
                writer = new BufferedWriter(fileWriter);
            } catch (IOException ex) {
                Logger.getLogger(FileFolder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return true;
    }
}
