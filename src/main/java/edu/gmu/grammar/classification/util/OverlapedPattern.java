package edu.gmu.grammar.classification.util;

public class OverlapedPattern {
	private int[] overlapedName;
	private int[] overlapedPosition;

	public OverlapedPattern(int[] overlapName, int[] overLap){
		this.overlapedName = overlapName;
		this.overlapedPosition = overLap;
	}
	
	public int[] getOverlapedName() {
		return overlapedName;
	}

	public void setOverlapedName(int[] overlapedName) {
		this.overlapedName = overlapedName;
	}

	public int[] getOverlapedPosition() {
		return overlapedPosition;
	}

	public void setOverlapedPosition(int[] overlapedPosition) {
		this.overlapedPosition = overlapedPosition;
	}
}
