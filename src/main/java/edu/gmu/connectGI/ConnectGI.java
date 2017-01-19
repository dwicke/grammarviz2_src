package edu.gmu.connectGI;

import edu.gmu.grammar.patterns.PatternsSimilarity;
import edu.gmu.grammar.patterns.TSPattern;
import edu.gmu.grammar.patterns.TSPatterns;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

public class ConnectGI {

	/**
	 * Get patterns from concatenated data with Sequitur.
	 * 
	 * @param concatenateData
	 * @param params
	 * @return
	 */
	public HashMap<String, TSPatterns> getPatternsFromSequitur(
			HashMap<String, double[]> concatenateData, int[][] params,
			GrammarIndcutionMethod giMethod,
			HashMap<String, int[]> allStartPositions, double rpFrequencyTPer,
			int maxRPNum, double overlapTPer, Boolean isCoverageFre,
			PatternsSimilarity pSimilarity) {

		HashMap<String, TSPatterns> allPatterns = new HashMap<String, TSPatterns>();

		int windowSize = params[0][0];
		int paaSize = params[0][1];
		int alphabetSize = params[0][2];
		int strategy = params[0][3];
		NumerosityReductionStrategy nRStrategy = NumerosityReductionStrategy
				.fromValue(strategy);
		// DataProcessor.writeConcatenatedData(concatenateData);
		for (Entry<String, double[]> entry : concatenateData.entrySet()) {
			String classLabel = entry.getKey();
			double[] concatenatedTS = entry.getValue();
			int[] startPositions = allStartPositions.get(classLabel);

			GetRulesFromGI gi = new GetRulesFromGI();
			ArrayList<int[]> patternsLocation = gi.getGrammars(windowSize,
					paaSize, alphabetSize, nRStrategy, concatenatedTS,
					giMethod, startPositions, rpFrequencyTPer, maxRPNum,
					overlapTPer, isCoverageFre, pSimilarity);

			if (patternsLocation == null) {
				return null;
			}

			TSPatterns patterns = new TSPatterns(classLabel);

			readPatterns(concatenatedTS, patternsLocation, patterns,
					startPositions);

			allPatterns.put(classLabel, patterns);
		}

		return allPatterns;
	}

	/**
	 * Read subsequences according to the location of patterns in concatenated
	 * time series.
	 * 
	 * @param concatenatedTS
	 * @param patternsLocation
	 * @param patterns
	 */
	public static void readPatterns(double[] concatenatedTS,
			ArrayList<int[]> patternsLocation, TSPatterns patterns,
			int[] startingPositions) {
		// int[] startingPositions = getIntervals(originalLen,
		// concatenatedTS.length);

		// Start place, length, frequency.
		for (int[] location : patternsLocation) {
			int startPosition = location[0];
			if (startPosition < 0) {
				continue;
			}

			int patternLength = location[1];
			int frequency = location[2];

			double[] patternTS = Arrays.copyOfRange(concatenatedTS,
					startPosition, startPosition + patternLength);

			TSPattern tp = new TSPattern(frequency, patternTS,
					patterns.getLabel(), startPosition);
			int sp = findIdx(startingPositions, startPosition);
			tp.setFromTS(sp);
			patterns.addPattern(tp);
		}

	}

	public static int findIdx(int[] startingPositions, int startPosition) {
		int idx = 1;
		for (int sp : startingPositions) {
			if (sp >= startPosition)
				break;
			idx++;
		}

		return idx;
	}

}
