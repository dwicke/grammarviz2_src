package edu.gmu.grammar.patterns;

import java.util.ArrayList;

public class PatternCandidates {
	ArrayList<int[]> patterns;
	double tSimilar;

	public PatternCandidates(ArrayList<int[]> patterns, double tSimilar) {
		this.patterns = patterns;
		this.tSimilar = tSimilar;
	}

	public ArrayList<int[]> getPatterns() {
		return patterns;
	}

	public void setPatterns(ArrayList<int[]> patterns) {
		this.patterns = patterns;
	}

	public double gettSimilar() {
		return tSimilar;
	}

	public void settSimilar(double tSimilar) {
		this.tSimilar = tSimilar;
	}

}
