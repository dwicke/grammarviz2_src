package edu.gmu.grammar.patterns;

import java.util.ArrayList;

public class TSPatterns implements Cloneable {

	private ArrayList<TSPattern> patterns;
	private String label;
	private boolean changed = true;

	public TSPatterns(String bagLabel) {
		super();
		this.label = bagLabel.substring(0);
		this.patterns = new ArrayList<TSPattern>();
	}

	/**
	 * Add the pattern into the Patterns.
	 * 
	 * @param pattern
	 *            The pattern to add.
	 */
	public synchronized void addPattern(TSPattern pattern) {
		this.changed = true;
		this.patterns.add(pattern);
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ArrayList<TSPattern> getPatterns() {
		return patterns;
	}

	public void setPatterns(ArrayList<TSPattern> patterns) {
		this.patterns = patterns;
	}

	public int getAllLen() {
		int allLen = 0;
		for (TSPattern p : patterns) {
			allLen += p.getPatternTS().length;
		}

		return allLen;
	}

}
