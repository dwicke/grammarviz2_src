package edu.gmu.grammar.patterns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

public class TSPattern implements Cloneable, Comparable<TSPattern>, Serializable {

	private static final long serialVersionUID = 4268364054811283654L;

	private int frequency;
	private double[] patternTS;
	private double error = 0;
	private String label;
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
		this.error = another.error;
		this.label = another.label;
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

	public String getLabel() {
		return label;
	}

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
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

		return output.toString();
	}
}