/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package genefinderhmm;

import java.util.HashMap;

/**
 *
 * @author Unknown
 */
class State {
    private final String stateName;
    private HashMap<String, Double> stateEmissions; 
    
    State(String[] parsedState){
        stateName = parsedState[0];
        stateEmissions = new HashMap<>();
        
        String[] parsedEmissions = parsedState[1].split(",");
        String[] parsedEmissionProbabilities = parsedState[2].split(",");
        
        for (int i = 0 ; i < parsedEmissions.length; i++){
            stateEmissions.put(parsedEmissions[i], Double.parseDouble(parsedEmissionProbabilities[i]));
        }
    }
    
    double getEmissionProbability(String s){
        if (stateEmissions.containsKey(s)){
            return stateEmissions.get(s);
        } 
        return 0;
    }
    
    String getName(){
        return stateName;
    }
}
