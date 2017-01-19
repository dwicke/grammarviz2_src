package edu.gmu.grammar.classification.util.TSTesting;

public class TimeSeriesTrain {
	private String trueLable;
	private double[] values;
	private int idx;
	private double[] transformedTS;

	public TimeSeriesTrain(String trueLable, double[] values, int idx) {
		this.trueLable = trueLable;
		this.values = values;
		this.idx = idx;
	}

	public String getTrueLable() {
		return trueLable;
	}

	public void setTrueLable(String trueLable) {
		this.trueLable = trueLable;
	}

	public double[] getValues() {
		return values;
	}

	public void setValues(double[] values) {
		this.values = values;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public double[] getTransformedTS() {
		return transformedTS;
	}

	public void setTransformedTS(double[] transformedTS) {
		this.transformedTS = transformedTS;
	}

}
