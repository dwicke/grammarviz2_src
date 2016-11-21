package edu.gmu.grammar.classification.util;

import net.seninp.gi.logic.RuleInterval;

import java.util.ArrayList;

public class RepeatedPattern implements Cloneable, Comparable<RepeatedPattern> {

	// private int frequency;
	private ArrayList<RuleInterval> sequences;
	private int length;
	private int frequency;
	int[] startingPositions;

	public RepeatedPattern(int length, RuleInterval r, int[] startingPositions) {
		// this.startPositionOriginal = startPosition;
		this.length = length;
		// this.frequency = frequency;
		sequences = new ArrayList<RuleInterval>();
		sequences.add(r);
		this.startingPositions = startingPositions;
	}

	public ArrayList<RuleInterval> getSequences() {
		return sequences;
	}

	public void setSequences(ArrayList<RuleInterval> sequences) {
		this.sequences = sequences;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getFrequency() {
		calculationFrequency(startingPositions);
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	private int calculationFrequency(int[] startingPositions) {
		int f = 0;
		ArrayList<RuleInterval> arrPosThis = new ArrayList<RuleInterval>();
		for (RuleInterval ri : sequences) {
			if (PatternsProcess.isFromDifferenTS(arrPosThis, ri,
					startingPositions)) {
				f++;
			}
			arrPosThis.add(ri);
		}
		frequency = f;
		return f;
	}

	@Override
	public int compareTo(RepeatedPattern arg0) {
		Integer p1 = arg0.getFrequency();
		Integer fHere = this.getFrequency();

		if (fHere > p1) {
			return 1;
		} else if (fHere < p1) {
			return -1;
		} else
			return 0;
	}

	// @Override
	// public int compareTo(RepeatedPattern arg0) {
	// Integer p1 = arg0.getSequences().size();
	// Integer fHere = this.getSequences().size();
	//
	// if (fHere > p1) {
	// return 1;
	// } else if (fHere < p1) {
	// return -1;
	// } else
	// return 0;
	// }

}
