package edu.gmu.grammar.classification.util;

public class DistMatrixElement {
	private String patternClassLabelString;
	// pattern index in a class.
	private int patternIdx;

	private String tsClassLabelString;
	// time series index in a class.
	private int tsIdx;

	private double dist;

	public DistMatrixElement(String patternClassLabelString, int patternIdx,
			String tsClassLabelString, int tsIdx, double dist) {
		this.patternClassLabelString = patternClassLabelString;
		this.patternIdx = patternIdx;
		this.tsClassLabelString = tsClassLabelString;
		this.tsIdx = tsIdx;
		this.dist = dist;
	}

	public String getTsClassLabelString() {
		return tsClassLabelString;
	}

	public void setTsClassLabelString(String tsClassLabelString) {
		this.tsClassLabelString = tsClassLabelString;
	}

	public int getPatternIdx() {
		return patternIdx;
	}

	public void setPatternIdx(int patternIdx) {
		this.patternIdx = patternIdx;
	}

	public int getTsIdx() {
		return tsIdx;
	}

	public void setTsIdx(int tsIdx) {
		this.tsIdx = tsIdx;
	}

	public String getPatternClassLabelString() {
		return patternClassLabelString;
	}

	public void setPatternClassLabelString(String patternClassLabelString) {
		this.patternClassLabelString = patternClassLabelString;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

}
