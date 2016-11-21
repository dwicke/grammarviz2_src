package edu.gmu.grammar.classification.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeSeriesTest {
	private String trueLable;
	private String assignedLabel = null;
	private double[] values;
	private double distToNN;
	private double minError;
	Boolean assignedByClass = false;
	private ArrayList<String> labelByOther = new ArrayList<String>();
	private Map<String, Integer> assignedLabels = new HashMap<String, Integer>();
	private Map<Integer, Double> matchPs = new HashMap<Integer, Double>();
	private double lenNN;

	public TimeSeriesTest(String trueLable, double[] values) {
		this.trueLable = trueLable;
		this.values = values;
	}

	public double getMinError() {
		return minError;
	}

	public void setMinError(double minError) {
		this.minError = minError;
	}

	public String getTrueLable() {
		return trueLable;
	}

	public void setTrueLable(String trueLable) {
		this.trueLable = trueLable;
	}

	public String getAssignedLabel() {
		return assignedLabel;
	}

	public void setAssignedLabel(String assignedLabel) {
		this.assignedLabel = assignedLabel;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public double getDistToNN() {
		return distToNN;
	}

	public void setDistToNN(double distToNN) {
		this.distToNN = distToNN;
	}

	public double getLenNN() {
		return lenNN;
	}

	public void setLenNN(double lenNN) {
		this.lenNN = lenNN;
	}

	public Boolean getAssignedByClass() {
		return assignedByClass;
	}

	public void setAssignedByClass(Boolean assignedByClass) {
		this.assignedByClass = assignedByClass;
	}

	public ArrayList<String> getLabelByOther() {
		return labelByOther;
	}

	public void setLabelByOther(ArrayList<String> labelByOther) {
		this.labelByOther = labelByOther;
	}

	public Map<Integer, Double> getMatchPs() {
		return matchPs;
	}

	public void setMatchPs(Map<Integer, Double> matchPs) {
		this.matchPs = matchPs;
	}

	public Map<String, Integer> getAssignedLabels() {
		return assignedLabels;
	}

	public void setAssignedLabels(Map<String, Integer> assignedLabels) {
		this.assignedLabels = assignedLabels;
	}


}
