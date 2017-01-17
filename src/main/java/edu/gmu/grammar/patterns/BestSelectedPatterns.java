package edu.gmu.grammar.patterns;


import java.util.Arrays;

public class BestSelectedPatterns {
	private double minimalError;
	private TSPattern[] bestSelectedPatterns;
	private int[] bestParams;

	public BestSelectedPatterns(double minimalError, int[] bestParams,
			TSPattern[] bestSelectedPatterns) {
		this.minimalError = minimalError;
		this.bestParams = bestParams;
		this.bestSelectedPatterns = bestSelectedPatterns;
	}

	public double getMinimalError() {
		return minimalError;
	}

	public void setMinimalError(double minimalError) {
		this.minimalError = minimalError;
	}

	public TSPattern[] getBestSelectedPatterns() {
		return bestSelectedPatterns;
	}

	public void setBestSelectedPatterns(TSPattern[] bestSelectedPatterns) {
		this.bestSelectedPatterns = bestSelectedPatterns;
	}

	public int[] getBestParams() {
		return bestParams;
	}

	public void setBestParams(int[] bestParams) {
		this.bestParams = bestParams;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Minimal Error: " + this.minimalError + "\n");
		output.append("Best Parameters: " + Arrays.toString(this.bestParams) + "\n");
		output.append("Best Patterns:\n");
		for(int i = 0; i < this.bestSelectedPatterns.length; i++) {
			output.append(this.bestSelectedPatterns[i].toString() + "\n");
		}

		return output.toString();
	}

}
