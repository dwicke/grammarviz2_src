package edu.gmu.grammar.classification.util;

public class DistToClass {

	private double dist;
	private String label;
	public DistToClass(double dist, String label) {
		this.dist = dist;
		this.label = label;
	}
	public double getDist() {
		return dist;
	}
	public void setDist(double dist) {
		this.dist = dist;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
}
