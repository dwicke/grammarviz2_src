package edu.gmu.grammar.patterns;


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

}
