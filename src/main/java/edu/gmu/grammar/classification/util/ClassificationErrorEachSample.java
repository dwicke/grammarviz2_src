package edu.gmu.grammar.classification.util;

public class ClassificationErrorEachSample {
	private double allError;
	private double[] errorPerClass;

	public ClassificationErrorEachSample(double allError, double[] errorPerClass) {
		this.allError = allError;
		this.errorPerClass = errorPerClass;
	}

	public double getAllError() {
		return allError;
	}

	public void setAllError(double allError) {
		this.allError = allError;
	}

	public double[] getErrorPerClass() {
		return errorPerClass;
	}

	public void setErrorPerClass(double[] errorPerClass) {
		this.errorPerClass = errorPerClass;
	}
}
