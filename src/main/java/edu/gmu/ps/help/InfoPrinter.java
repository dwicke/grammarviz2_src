package edu.gmu.ps.help;

import ch.qos.logback.classic.Logger;
import edu.gmu.grammar.patterns.BestSelectedPatterns;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class InfoPrinter {
	private static final String CR = "\n";

	public static void printBestParams(BestSelectedPatterns[] bsps) {
		for (int i = 0; i < bsps.length; i++) {
			int[] bsp = bsps[i].getBestParams();
			System.out.print("Class: " + String.valueOf(i + 1));
			System.out.print(" Window Length: " + String.valueOf(bsp[0]));
			System.out.print(" PAA Size: " + String.valueOf(bsp[1]));
			System.out.println(" Alphabet Size: " + String.valueOf(bsp[2]));
		}
	}

	public static void printDataInfo(Logger consoleLogger,
			Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData) {
		consoleLogger
				.debug("trainData classes: "
						+ trainData.size()
						+ ", series length: "
						+ trainData.entrySet().iterator().next().getValue()
								.get(0).length);
		for (Entry<String, List<double[]>> e : trainData.entrySet()) {
			consoleLogger.debug(" training class: " + e.getKey() + " series: "
					+ e.getValue().size());
		}

		consoleLogger
				.debug("testData classes: "
						+ testData.size()
						+ ", series length: "
						+ testData.entrySet().iterator().next().getValue()
								.get(0).length);
		for (Entry<String, List<double[]>> e : testData.entrySet()) {
			consoleLogger.debug(" test class: " + e.getKey() + " series: "
					+ e.getValue().size());
		}
	}

	/**
	 * DataName, trainFile, testFile, minWindowSize, maxWindowSize, minPAA,
	 * maxPAA, minAlphabetSize, maxAlphabetSize, NumerosityReductionStrategy,
	 * isSecondRefine
	 * 
	 * @return
	 */
	public static String printHelp() {
		StringBuffer sb = new StringBuffer();
		sb.append(
				"RepresentativePatternSelection parameters optimization sampler ")
				.append(CR);
		sb.append("Expects 3 or 11 parameters:").append(CR);
		sb.append(" [1 ] Data Name").append(CR);
		sb.append(" [2 ] training dataset filename").append(CR);
		sb.append(" [3 ] test dataset filename").append(CR);
		sb.append(" [4 ] minimal sliding window size (Optional)").append(CR);
		sb.append(" [5 ] maximal sliding window size (Optional)").append(CR);
		sb.append(" [6 ] minimal PAA size (Optional)").append(CR);
		sb.append(" [7 ] maximal PAA size (Optional)").append(CR);
		sb.append(" [8 ] minimal Alphabet size (Optional)").append(CR);
		sb.append(" [9 ] maximal Alphabet size (Optional)").append(CR);
		sb.append(" [10] Numerosity Reduction Strategy (Optional)").append(CR);
		sb.append(" [11] If using second optimization (Optional)").append(CR);
		sb.append("An execution example: $java -jar \"representativePattern.jar\"");
		sb.append(
				" CBF data/cbf/CBF_TRAIN data/cbf/CBF_TEST 25 40 3 20 3 20 EXACT 1")
				.append(CR);
		sb.append("Another execution example: $java -jar \"representativePattern.jar\"");
		sb.append(" CBF data/cbf/CBF_TRAIN data/cbf/CBF_TEST").append(CR);
		return sb.toString();
	}

}
