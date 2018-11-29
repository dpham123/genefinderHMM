/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genefinderhmm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dpham123
 */
class HMM {
    
    HMM(File states, File transitions, File sequence) throws IOException{
        parseFile(states, transitions, sequence);
    }
    
    private void parseFile(File states_fileName, File transitions_fileName, File sequence_fileName) throws IOException{
        FileReader fr = new FileReader(states_fileName);
        BufferedReader br = new BufferedReader(fr);
        
        String line = br.readLine();
        HashSet<String[]> states = new HashSet<>();
        
        // Parses states file
        while (line != null){
            String[] splitStuff = line.split("	");
            states.add(splitStuff);
            line = br.readLine();
        }
    }
    
    public static void main(String[] args){
        try {
            HMM test = new HMM(new File("data/language_states.txt"), null, null);
        } catch (IOException ex) {
            Logger.getLogger(HMM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
