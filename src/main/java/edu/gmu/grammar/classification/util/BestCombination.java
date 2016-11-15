package edu.gmu.grammar.classification.util;

import edu.gmu.grammar.patterns.TSPatterns;

import java.util.HashMap;

public class BestCombination {
    private double minimalError;
    private HashMap<String, TSPatterns> thisRPatterns;
    private int[] bestParams;
    
    public BestCombination(double minimalError, int[] bestParams, HashMap<String, TSPatterns> thisRPatterns){
    	this.minimalError = minimalError;
    	this.bestParams = bestParams;
    	this.thisRPatterns = thisRPatterns;
    }
    
	public double getMinimalError() {
		return minimalError;
	}
	public void setMinimalError(double minimalError) {
		this.minimalError = minimalError;
	}
	public HashMap<String, TSPatterns> getThisRPatterns() {
		return thisRPatterns;
	}
	public void setThisRPatterns(HashMap<String, TSPatterns> thisRPatterns) {
		this.thisRPatterns = thisRPatterns;
	}
	public int[] getBestParams() {
		return bestParams;
	}
	public void setBestParams(int[] bestParams) {
		this.bestParams = bestParams;
	}

}
