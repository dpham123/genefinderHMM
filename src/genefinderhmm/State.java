/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genefinderhmm;

/**
 *
 * @author Unknown
 */
class State {
    private String stateName;
    private String[] stateEmissions;
    private int[] stateEmissionProbabilities;
    
    State(String[] parsedState){
        stateName = parsedState[0];
        stateEmissions = parsedState[1].split(",");
        
        String[] parsedEmissions = parsedState[2].split(",");
        
        int counter = 0;
        for (String s : parsedEmissions){
            stateEmissionProbabilities[counter] = Integer.parseInt(s);
            counter++;
        }
    }
}
