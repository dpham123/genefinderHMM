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
    private State[][] backtrackTable;

    HMM(File states, File transitions, File sequence) throws IOException {
        this.sequence = "";
        parseFile(states, transitions, sequence);
        dynamicTable = new double[this.states.length][this.sequence.length()];
        backtrackTable = new State[this.states.length][this.sequence.length()];
    }

    private void parseFile(File states_fileName, File transitions_fileName, File sequence_fileName) throws IOException {
        FileReader fr = new FileReader(states_fileName);
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        LinkedList states = new LinkedList<>();

        // Parses states file
        int counter = 0;
        while (line != null) {
            String[] splitStuff = line.split("	");
            states.add(new State(splitStuff, counter));
            line = br.readLine();
            counter++;
        }
        this.states = (State[]) states.toArray(new State[0]);

        fr = new FileReader(transitions_fileName);
        br = new BufferedReader(fr);
        line = br.readLine();
        line = br.readLine();
        String[] tempSplit = line.split("	");
        transitionProbabilities = new double[tempSplit.length][tempSplit.length];

        // Parses transitions file
        counter = 1;
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
    }

    private void fillInTableEntry(int row, int column, double[] probability) {
        dynamicTable[row][column] = probability[0];
        backtrackTable[row][column] = states[(int) probability[1]];
    }

    private void fillInitialColumn() {
        dynamicTable[0][0] = Math.log10(states[0].getEmissionProbability(sequence.substring(0, 1)));
        dynamicTable[1][0] = Math.log10(0);
        dynamicTable[2][0] = Math.log10(0);
        dynamicTable[3][0] = Math.log10(0);

        State nullState = new State("N");
        backtrackTable[0][0] = nullState;
        backtrackTable[1][0] = nullState;
        backtrackTable[2][0] = nullState;
        backtrackTable[3][0] = nullState;
    }

    State[][] getBacktrackTable() {
        return backtrackTable;
    }

    private double[] max(double[] numbers) {
        double[] max = new double[2];
        max[0] = numbers[0];
        max[1] = 0;

        for (int i = 0; i < numbers.length; i++) {
            if (numbers[i] > max[0]) {
                max[0] = numbers[i];
                max[1] = i;
            }
        }
        return max;
    }

    void viterbi() {
        // Fills initial column with initial probabilities
        fillInitialColumn();
        double[] singleStateMax = new double[states.length];

        // Iterates through sequence
        for (int i = 1; i < sequence.length(); i++) {
            // Iterates through "previous state"
            for (int j = 0; j < states.length; j++) {
                // Iterates through "current state"
                for (int k = 0; k < states.length; k++) {

                    // Bases codon transition probability on previous codon probability, hence [i - 3]
                    if ((states[k].getName().equals("Codons") || states[k].getName().equals("StopCodons")) && i - 3 >= 0) {
                        singleStateMax[k] = dynamicTable[k][i - 3] + Math.log10(transitionProbabilities[k + 1][j + 1]);

                    // Bases the "intergenic" and "start codon" transition probability on previous state
                    } else if (states[k].getName().equals(".Intergenic") || states[k].getName().equals("StartCodons")) {
                        singleStateMax[k] = dynamicTable[k][i - 1] + Math.log10(transitionProbabilities[k + 1][j + 1]);
                    } else {
                        singleStateMax[k] = Math.log10(0);
                    }

                    // If intergenic, the state can only emit 1 character
                    if (!states[j].getName().equals(".Intergenic") && i + 2 < sequence.length()) {
                        singleStateMax[k] += Math.log10(states[j].getEmissionProbability(sequence.substring(i, i + 3)));

                    // If a codon, the state can only emit 3 characters
                    } else if (states[j].getName().equals(".Intergenic")) {
                        singleStateMax[k] += Math.log10(states[j].getEmissionProbability(sequence.substring(i, i + 1)));
                    } else {
                        singleStateMax[k] = Math.log10(0);
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

    int[] findStates() {
        double max = Math.log10(0);
        int state = -1;
        int[] states = new int[dynamicTable[0].length];

        for (int j = dynamicTable.length - 1; j > 0; j--) {
            if (dynamicTable[j][dynamicTable[0].length - 1] >= max) {
                max = dynamicTable[j][dynamicTable[0].length - 1];
                state = j;
            }
        }

        for (int i = backtrackTable[0].length - 1; i > 0; i--) {
            states[i] = backtrackTable[state][i].getIndex();
            state = backtrackTable[state][i].getIndex();
        }
        return states;
    }

    public static void main(String[] args) {
        try {
            HMM test = new HMM(new File("data/Ecoli_states.txt"),
                    new File("data/Ecoli_transitions.txt"), new File("data/E_coli_observation_290.txt"));
            test.viterbi();
            int[] a = test.findStates();

            for (int i : a) {
                System.out.print(i);
            }

            /*
            State[][] backtrackTable = test.getBacktrackTable();
            for (int i = 0; i < backtrackTable[0].length; i++) {
                System.out.println(i);
                System.out.println("-------------------");
                for (int j = 0; j < backtrackTable.length; j++) {
                    System.out.println(backtrackTable[j][i].getIndex() + " ");
                }
            }
             */
        } catch (IOException ex) {
            Logger.getLogger(HMM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
