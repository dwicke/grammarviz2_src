package edu.gmu.grammar.patterns;

import java.util.ArrayList;

public class PatternsSimilarity {

	private Boolean isTSimilarSetted = false;
	private double initialTSimilar;
	private double tSimilar = 0.02;
	private ArrayList<Double> tCandidates;

	public PatternsSimilarity(double tSimilar) {
		this.initialTSimilar = tSimilar;
		tCandidates = new ArrayList<Double>();
	}

	public Boolean getIsTSimilarSetted() {
		return isTSimilarSetted;
	}

	public void setIsTSimilarSetted(Boolean isTSimilarSetted) {
		this.isTSimilarSetted = isTSimilarSetted;
	}

	public double getInitialTSimilar() {
		return initialTSimilar;
	}

	public void setInitialTSimilar(double initialTSimilar) {
		this.initialTSimilar = initialTSimilar;
	}

	public double gettSimilar() {
		return tSimilar;
	}

	public void settSimilar(double tSimilar) {
		this.tSimilar = tSimilar;
	}

	public void addCandidate(double tcandi) {
		tCandidates.add(tcandi);
	}

	public ArrayList<Double> gettCandidates() {
		return tCandidates;
	}

	public void settCandidates(ArrayList<Double> tCandidates) {
		this.tCandidates = tCandidates;
	}

	public void clear() {
		tCandidates.clear();
	}
}
