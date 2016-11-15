package edu.gmu.grammar.classification.util;

import java.util.ArrayList;

public class TopKBestPatterns {
	private ArrayList<BestCombination> bestKComb;

	public TopKBestPatterns(){
		this.bestKComb = new ArrayList<BestCombination>();
	}
	
	public ArrayList<BestCombination> getBestKComb() {
		return bestKComb;
	}

	public void setBestKComb(ArrayList<BestCombination> bestKComb) {
		this.bestKComb = bestKComb;
	}

	
}
