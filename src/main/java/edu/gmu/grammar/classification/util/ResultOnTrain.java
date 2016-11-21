package edu.gmu.grammar.classification.util;

import java.util.HashMap;
import java.util.Map;

public class ResultOnTrain {
	private double error;
	private Map<String, Integer> missclassifiedAs = new HashMap<String, Integer>();
	private int maxIncorrectNum;

	public double getError() {
		return error;
	}

	public void setError(double error) {
		this.error = error;
	}

	public Map<String, Integer> getMissclassifiedAs() {
		return missclassifiedAs;
	}

	public void setMissclassifiedAs(Map<String, Integer> missclassifiedAs) {
		this.missclassifiedAs = missclassifiedAs;
	}

	public int getMaxIncorrectNum() {
		return maxIncorrectNum;
	}

	public void setMaxIncorrectNum(int maxIncorrectNum) {
		this.maxIncorrectNum = maxIncorrectNum;
	}

}
