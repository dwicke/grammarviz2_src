package edu.gmu.grammar.classification.util.TSTesting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TimeSeriesTestShifted {
	private String trueLable;
	private String assignedLabel = null;
	private double[] values;
	private double distToNN;
	Boolean assignedByClass = false;
	private ArrayList<String> labelByOther = new ArrayList<String>();
	private Map<String, Integer> assignedLabels = new HashMap<String, Integer>();
	private double[] rebuildedValues;

	public TimeSeriesTestShifted(String trueLable, double[] values, double[] rebuildedValues) {
		this.trueLable = trueLable;
		this.values = values;
		this.rebuildedValues = rebuildedValues;
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

	public double[] getRebuildedValues() {
		return rebuildedValues;
	}

	public void setRebuildedValues(double[] rebuildedValues) {
		this.rebuildedValues = rebuildedValues;
	}

	public double getDistToNN() {
		return distToNN;
	}

	public void setDistToNN(double distToNN) {
		this.distToNN = distToNN;
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

	public Map<String, Integer> getAssignedLabels() {
		return assignedLabels;
	}

	public void setAssignedLabels(Map<String, Integer> assignedLabels) {
		this.assignedLabels = assignedLabels;
	}

}
