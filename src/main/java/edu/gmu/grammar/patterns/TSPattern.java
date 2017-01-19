package edu.gmu.grammar.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

public class TSPattern implements Cloneable, Comparable<TSPattern> {

	private int frequency;
	private double[] patternTS;
	private int correctNN = 0;
	private int wrongNN = 0;
	private double error = 0;
	private String label;
	private ArrayList<double[]> patternsInClass = new ArrayList<double[]>();
	private HashMap<String, Integer> wrongClass = new HashMap<String, Integer>();
	private double nnDistSum;
	private int fromTS;
	private int startP;

	public TSPattern(int frequency, double[] patternTS, String classLabel, int startP) {
		super();
		this.frequency = frequency;
		this.patternTS = patternTS;
		this.label = classLabel;
		this.startP = startP;
	}

	public TSPattern(TSPattern another) {
		super();
		this.frequency = another.frequency;
		this.patternTS = another.patternTS;
		this.correctNN = another.correctNN;
		this.wrongNN = another.wrongNN;
		this.error = another.error;
		this.label = another.label;
		this.patternsInClass = another.patternsInClass;
		this.wrongClass = another.wrongClass;
		this.nnDistSum = another.nnDistSum;
		this.fromTS = another.fromTS;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public double[] getPatternTS() {
		return patternTS;
	}

	public void setPatternTS(double[] patternTS) {
		this.patternTS = patternTS;
	}

	public int getCorrectNN() {
		return correctNN;
	}

	public void setCorrectNN(int correctNN) {
		this.correctNN = correctNN;
	}

	public int getWrongNN() {
		return wrongNN;
	}

	public void setWrongNN(int wrongNN) {
		this.wrongNN = wrongNN;
	}

	public String getLabel() {
		return label;
	}

	public ArrayList<double[]> getPatternsInClass() {
		return patternsInClass;
	}

	public void setPatternsInClass(ArrayList<double[]> patternsInClass) {
		this.patternsInClass = patternsInClass;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public HashMap<String, Integer> getWrongClass() {
		return wrongClass;
	}

	public void setWrongClass(HashMap<String, Integer> wrongClass) {
		this.wrongClass = wrongClass;
	}

	public double getNnDistSum() {
		return nnDistSum;
	}

	public void setNnDistSum(double nnDistSum) {
		this.nnDistSum = nnDistSum;
	}

	public int getFromTS() {
		return fromTS;
	}

	public void setFromTS(int fromTS) {
		this.fromTS = fromTS;
	}

	public int getStartP() {
		return startP;
	}

	public void setStartP(int startP) {
		this.startP = startP;
	}

	@Override
	public int compareTo(TSPattern arg0) {
		Integer p1 = arg0.getFrequency();

		if (this.getFrequency() > p1) {
			return 1;
		} else if (this.getFrequency() < p1) {
			return -1;
		} else
			return 0;
	}

	@Override
	public String toString() {
		StringBuilder output = new StringBuilder();
		output.append("Label: " + this.label + "\n");
		output.append("Length PatternsTS (" + this.patternTS.length + "):\n");
		output.append(Arrays.toString(this.patternTS));
		//output.append("Patterns in class (" + this.patternsInClass.size() + "):\n");
		//for(int i = 0; i < this.patternTS.length; i++) {
		//	output.append(this.patternTS[i] );
		//}

		return output.toString();
	}
}