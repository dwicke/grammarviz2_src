package edu.gmu.grammar.classification.util;

public class ClosestMatch {
	private int[] position;
	private double dist;
	private double[] patternTS;

	public ClosestMatch(int[] position, double dist, double[] patternTS){
		this.position = position;
		this.dist = dist;
		this.patternTS = patternTS;
	}
	
	public int[] getPosition() {
		return position;
	}

	public void setPosition(int[] position) {
		this.position = position;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}

	public double[] getPatternTS() {
		return patternTS;
	}

	public void setPatternTS(double[] patternTS) {
		this.patternTS = patternTS;
	}


}
