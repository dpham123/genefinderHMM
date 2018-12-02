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
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dpham123
 */
class HMM {

    private State[] states;
    private String sequence;
    private double[][] transitionProbabilities;
    private double[][] dynamicTable;

    HMM(File states, File transitions, File sequence) throws IOException {
        this.sequence = "";
        parseFile(states, transitions, sequence);
        dynamicTable = new double[this.states.length][this.sequence.length()];
    }

    private void parseFile(File states_fileName, File transitions_fileName, File sequence_fileName) throws IOException {
        FileReader fr = new FileReader(states_fileName);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        LinkedList states = new LinkedList<>();

        // Parses states file
        while (line != null) {
            String[] splitStuff = line.split("	");
            states.add(new State(splitStuff));
            line = br.readLine();
        }
        this.states = (State[]) states.toArray(new State[0]);

        fr = new FileReader(transitions_fileName);
        br = new BufferedReader(fr);
        line = br.readLine();
        line = br.readLine();
        String[] tempSplit = line.split("	");
        transitionProbabilities = new double[tempSplit.length][tempSplit.length];

        // Parses transitions file
        int counter = 1;
        while (line != null) {
            String[] splitStuff = line.split("	");
            for (int i = 1; i < splitStuff.length; i++) {
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
        while (line != null) {
            sequence += line;
            line = br.readLine();
        }
        
        System.out.println("Transition Probabilities");
        System.out.println("----------------------------------------------");
        for (int i = 0; i < transitionProbabilities.length; i++) {
            for (int j = 0; j < transitionProbabilities[0].length; j++) {
                System.out.print(transitionProbabilities[i][j] + " ");
            }
            System.out.println();
        }

    }

    private void fillInTableEntry(int row, int column, double probability) {
        dynamicTable[row][column] = probability;
    }

    private double max(double[] numbers) {
        double max = numbers[0];

        for (double d : numbers) {
            if (d > max) {
                max = d;
            }
        }
        return max;
    }

    void viterbi() {
        fillInTableEntry(0, 0, Math.log10(states[0].getEmissionProbability(sequence.substring(0, 1))));
        fillInTableEntry(1, 0, Math.log10(0));
        fillInTableEntry(2, 0, Math.log10(0));
        fillInTableEntry(3, 0, Math.log10(0));
        double[] singleStateMax = new double[states.length];
        for (int i = 1; i < sequence.length(); i++) {
            for (int j = 0; j < states.length; j++) {
                for (int k = 0; k < states.length; k++) {
                    if ((states[j].getName().equals("Codons") || states[j].getName().equals("StopCodons")) && i - 3 >= 0){
                        singleStateMax[k] = dynamicTable[k][i - 3] + Math.log10(transitionProbabilities[k + 1][j + 1]);
                    } else if (states[j].getName().equals(".Intergenic") || states[j].getName().equals("StartCodons")){
                        singleStateMax[k] = dynamicTable[k][i - 1] + Math.log10(transitionProbabilities[k + 1][j + 1]);
                    } else {
                        singleStateMax[k] = Math.log10(0);
                    }
                    

                    if (!states[j].getName().equals(".Intergenic") && i + 2 < sequence.length()) {
                        singleStateMax[k] += Math.log10(states[j].getEmissionProbability(sequence.substring(i, i + 3)));
                    } else if (states[j].getName().equals(".Intergenic")){
                        singleStateMax[k] += Math.log10(states[j].getEmissionProbability(sequence.substring(i, i + 1)));
                    }
                }
                fillInTableEntry(j, i, max(singleStateMax));
            }
        }
        System.out.println("Dynamic Table");
        System.out.println("----------------------------------------------");
        for (int i = 0; i < dynamicTable[0].length; i++) {
            System.out.println(i);
            System.out.println("-------------------");
            for (int j = 0; j < dynamicTable.length; j++) {
                System.out.println(dynamicTable[j][i] + " ");
            }
        }
    }

    public static void main(String[] args) {
        try {
            HMM test = new HMM(new File("data/Ecoli_states.txt"),
                    new File("data/Ecoli_transitions.txt"), new File("data/E_coli_observation_290.txt"));
            test.viterbi();

        } catch (IOException ex) {
            Logger.getLogger(HMM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
