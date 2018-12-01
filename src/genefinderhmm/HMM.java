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
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dpham123
 */
class HMM {
    private Set<State> states;
    private String sequence;
    private double[][] transitionProbabilities;
    private double[][] dynamicTable;
    
    HMM(File states, File transitions, File sequence) throws IOException{
        this.sequence = "";
        parseFile(states, transitions, sequence);
        dynamicTable = new double[this.states.size()][this.sequence.length()];
    }
    
    private void parseFile(File states_fileName, File transitions_fileName, File sequence_fileName) throws IOException{
        FileReader fr = new FileReader(states_fileName);
        BufferedReader br = new BufferedReader(fr);
        
        String line = br.readLine();
        states = new HashSet<>();
        
        // Parses states file
        while (line != null){
            String[] splitStuff = line.split("	");
            states.add(new State(splitStuff));
            line = br.readLine();
        }
        
        fr = new FileReader(transitions_fileName);
        br = new BufferedReader(fr);
        line = br.readLine();
        line = br.readLine();
        String[] tempSplit = line.split("	");
        transitionProbabilities = new double[tempSplit.length][tempSplit.length];
        
        // Parses transitions file
        int counter = 1;
        while (line != null){
            String[] splitStuff = line.split("	");
            for (int i = 1; i < splitStuff.length; i++){
                transitionProbabilities[counter][i] = Double.parseDouble(splitStuff[i]);
            }
            line = br.readLine();
            counter++;
        }
        
        fr = new FileReader(sequence_fileName);
        br = new BufferedReader(fr);
        line = br.readLine();
        line = br.readLine();
        
        // Parses observations file
        while (line != null){
            sequence += line;
            line = br.readLine();
        }
        
        for (int i = 0; i < transitionProbabilities.length; i++){
            for (int j = 0; j < transitionProbabilities[0].length; j++){
                System.out.print(transitionProbabilities[i][j] + " ");
            }
            System.out.println();
        }
        
    }
    
    void viterbi(){
        
        for (int i = 1; i < sequence.length(); i++){
            
        }
    }
    
    public static void main(String[] args){
        try {
            HMM test = new HMM(new File("data/Ecoli_states.txt"), 
                    new File("data/Ecoli_transitions.txt"), new File("data/Ecoli_observation_290.txt"));
            
        } catch (IOException ex) {
            Logger.getLogger(HMM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
