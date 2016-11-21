package edu.gmu.grammar.transform;

import edu.gmu.grammar.patterns.TSPattern;

public class PatternsAndTransformedData {
	private TSPattern[] allPatterns;
	private double[][] transformedTS;
	public TSPattern[] getAllPatterns() {
		return allPatterns;
	}
	public void setAllPatterns(TSPattern[] allPatterns) {
		this.allPatterns = allPatterns;
	}
	public double[][] getTransformedTS() {
		return transformedTS;
	}
	public void setTransformedTS(double[][] transformedTS) {
		this.transformedTS = transformedTS;
	}
	
	
}
