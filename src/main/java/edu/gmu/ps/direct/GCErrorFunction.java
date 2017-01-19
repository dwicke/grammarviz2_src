package edu.gmu.ps.direct;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.gmu.connectGI.GrammarIndcutionMethod;
import edu.gmu.grammar.classification.util.TSTesting.DataProcessor;
import edu.gmu.grammar.classification.util.TSTesting.GCProcess;
import edu.gmu.grammar.classification.util.*;
import edu.gmu.grammar.classification.util.TSTesting.TimeSeriesTrain;
import edu.gmu.grammar.patterns.TSPattern;
import edu.gmu.grammar.patterns.TSPatterns;
import edu.gmu.grammar.transform.PatternsAndTransformedData;
import net.seninp.jmotif.direct.Point;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.jmotif.sax.alphabet.Alphabet;
import net.seninp.jmotif.sax.alphabet.NormalAlphabet;
import net.seninp.util.StackTrace;
import org.slf4j.LoggerFactory;
import weka.classifiers.Evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Map.Entry;

public class GCErrorFunction {

	public static final Character DELIMITER = '~';
	final static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

	/** The latin alphabet, lower case letters a-z. */
	private static final char[] ALPHABET = { 'a', 'b', 'c', 'd', 'e', 'f', 'g',
			'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
			'u', 'v', 'w', 'x', 'y', 'z' };
	private Alphabet a = new NormalAlphabet();

	// the default normalization threshold
	private static final double NORMALIZATION_THRESHOLD = 0.05D;
	private static boolean timing = false;

	public HashMap<String, TSPatterns> thisRPatterns;

	public TSPattern[] selectedRepresentativePatterns;

	// the default numerosity strategy
	private NumerosityReductionStrategy numerosityReductionStrategy;

	// the data
	// private Map<String, double[]> tsDataTrain;
	// private Map<String, List<double[]>> trainData;
	// private Map<String, List<double[]>> forRNNData;
	// private Map<String, double[]> tsDataValidate;
	// private Map<String, List<double[]>> validateData;

	private ArrayList<TimeSeriesTrain> trainData;
	private Map<String, List<TimeSeriesTrain>> trainDataPerClass;

	private GrammarIndcutionMethod giMethod;
	private boolean gcParams[];

	// the hold out sample size
	private int holdOutSampleSize;

	private static final Logger consoleLogger;
	private static final Level LOGGING_LEVEL = Level.INFO;
	static {
		consoleLogger = (Logger) LoggerFactory.getLogger(GCErrorFunction.class);
		consoleLogger.setLevel(LOGGING_LEVEL);
	}

	// /**
	// * Constructor.
	// *
	// * @param data
	// * @param holdOutSampleSize
	// */
	// public GCErrorFunction(Map<String, List<double[]>> trainData,
	// Map<String, List<double[]>> validateData,
	// Map<String, List<double[]>> forRNNData, int holdOutSampleSize,
	// SAXNumerosityReductionStrategy strategy,
	// GrammarIndcutionMethod giMethod, boolean gcParams[]) {
	//
	// this.tsDataTrain = new HashMap<String, double[]>();
	// this.trainData = trainData;
	// this.forRNNData = forRNNData;
	// this.validateData = validateData;
	//
	// for (Entry<String, List<double[]>> e : trainData.entrySet()) {
	// String classLabel = e.getKey();
	// int classCounter = 0;
	// for (double[] series : e.getValue()) {
	// this.tsDataTrain.put(classLabel + DELIMITER + classCounter,
	// series);
	// classCounter++;
	// }
	// }
	//
	// this.tsDataValidate = new HashMap<String, double[]>();
	//
	// for (Entry<String, List<double[]>> e : validateData.entrySet()) {
	// String classLabel = e.getKey();
	// int classCounter = 0;
	// for (double[] series : e.getValue()) {
	// this.tsDataValidate.put(classLabel + DELIMITER + classCounter,
	// series);
	// classCounter++;
	// }
	// }
	//
	// this.holdOutSampleSize = holdOutSampleSize;
	// this.numerosityReductionStrategy = strategy;
	// this.giMethod = giMethod;
	// this.gcParams = gcParams;
	// }

	/**
	 * Constructor.
	 * 
	 * @param data
	 * @param holdOutSampleSize
	 */
	public GCErrorFunction(Map<String, List<double[]>> inputTrainData,
			NumerosityReductionStrategy strategy,
			GrammarIndcutionMethod giMethod, boolean gcParams[]) {
		this.trainData = new ArrayList<TimeSeriesTrain>();
		this.trainDataPerClass = new HashMap<String, List<TimeSeriesTrain>>();
		for (Entry<String, List<double[]>> e : inputTrainData.entrySet()) {
			String label = e.getKey();
			List<double[]> tsesInClass = e.getValue();

			List<TimeSeriesTrain> tses = new ArrayList<TimeSeriesTrain>();

			int idx = 1;
			for (double[] ts : tsesInClass) {
				TimeSeriesTrain tsTrain = new TimeSeriesTrain(label, ts, idx);
				trainData.add(tsTrain);
				tses.add(tsTrain);
				idx++;
			}

			this.trainDataPerClass.put(label, tses);
		}

		this.numerosityReductionStrategy = strategy;
		this.giMethod = giMethod;
		this.gcParams = gcParams;
	}

	/**
	 * Computes the value at point.
	 * 
	 * @param point
	 * @return
	 */
	public ClassificationErrorEachSample valueAt2(Point point) {

		// point is in fact a aset of parameters - window, paa, and the alphabet
		//
		double[] coords = point.toArray();
		int windowSize = Long.valueOf(Math.round(coords[0])).intValue();
		int paaSize = Long.valueOf(Math.round(coords[1])).intValue();
		int alphabetSize = Long.valueOf(Math.round(coords[2])).intValue();

		// if we stepped above window length with PAA size - for some reason -
		// return the max possible
		// error value
		if (paaSize > windowSize) {
			return null;
		}

		// the whole thing begins here
		//
		try {
			// make a parameters vector
			int[][] params = new int[1][4];
			params[0][0] = windowSize;
			params[0][1] = paaSize;
			params[0][2] = alphabetSize;
			params[0][3] = this.numerosityReductionStrategy.index();
			// consoleLogger.debug("parameters: " + windowSize + ", " + paaSize
			// + ", " + alphabetSize + ", "
			// + this.numerosityReductionStrategy.toString());

			HashMap<String, int[]> allStartPositions = new HashMap<String, int[]>();

			// Concatenate training time series
			HashMap<String, double[]> concatenateData = DataProcessor
					.concatenateTrainInTrain(trainDataPerClass,
							allStartPositions);

			// TODO: write concatenated data.
			// writeConcatenatedData(concatenateData);

			// int tsLen = trainData.get(0).getValues().length;

			// For timing
			long startTimeSequitur = System.currentTimeMillis();
			// Get representative patterns
			HashMap<String, TSPatterns> allPatterns = DataProcessor
					.getPatternsFromSequitur(concatenateData, params, giMethod,
							allStartPositions);
			concatenateData.clear();
			if (timing) {
				// For timing
				long endTimeSequitur = System.currentTimeMillis();
				long totalTimeSequitur = endTimeSequitur - startTimeSequitur;
				consoleLogger.debug("Get patterns from sequitur time: "
						+ DataProcessor.millisToShortDHMS(totalTimeSequitur));
			}
			if (allPatterns == null) {
				return null;
			}

			double patternRate = 0.5;
			HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
					.selectTopFrequentPatterns(allPatterns, patternRate);
			// HashMap<String, TSPatterns> topFrequentPatterns = allPatterns;

			// allPatterns.clear();
			if (topFrequentPatterns != null) {
				int clsNum = topFrequentPatterns.size();
				int[] missclassifiedSamplesPerClass = new int[clsNum];
				int[] correctNumPerClass = new int[clsNum];
				Arrays.fill(missclassifiedSamplesPerClass, 0);
				Arrays.fill(correctNumPerClass, 0);

				GCProcess gcp = new GCProcess(gcParams[0], gcParams[1],
						gcParams[2], gcParams[3], gcParams[4]);

				// For timing
				long startTimeRepresentative = System.currentTimeMillis();

				// HashMap<String, TSPatterns> representativePatterns = gcp
				// .selectBestFromRNNTrain(topFrequentPatterns, 3,
				// trainDataPerClass);

				HashMap<String, TSPatterns> representativePatterns = gcp
						.selectBestByTransforming(topFrequentPatterns,
								trainDataPerClass);

				topFrequentPatterns.clear();
				if (timing) {
					// For timing
					long endTimeRepresentative = System.currentTimeMillis();
					long totalTimeRepresentative = endTimeRepresentative
							- startTimeRepresentative;
					consoleLogger
							.info("Find Representative pattern time: "
									+ DataProcessor
											.millisToShortDHMS(totalTimeRepresentative));
				}

				thisRPatterns = (HashMap<String, TSPatterns>) representativePatterns
						.clone();

				int[] inTrainNum = new int[clsNum];
				for (TimeSeriesTrain tsTrainTest : trainData) {

					String trueClassLabel = tsTrainTest.getTrueLable();

					int res = classifyTrain(trueClassLabel, tsTrainTest,
							representativePatterns);

					if (res == -10) {
						// This time series contributed a pattern in
						// representative patterns.
						int clsLabel = Integer.parseInt(trueClassLabel) - 1;
						inTrainNum[clsLabel] += 1;
					} else {
						if (-1 == res) {
							int clsLabel = Integer.parseInt(trueClassLabel) - 1;
							correctNumPerClass[clsLabel] += 1;
						} else {
							// missclassifiedSamplesPerClass[clsLabel] += 1;
							missclassifiedSamplesPerClass[res] += 1;
						}
					}
				}
				representativePatterns.clear();
				double[] error = new double[clsNum];

				int allCorrectNum = 0;
				int allIncorrNum = 0;
				int allTSNum = 0;
				for (Entry<String, List<TimeSeriesTrain>> entry : trainDataPerClass
						.entrySet()) {
					String label = entry.getKey();
					List<TimeSeriesTrain> tses = entry.getValue();

					int l = Integer.parseInt(label) - 1;
					int tsNumHere = tses.size() - inTrainNum[l];

					if (tsNumHere <= 0)
						System.err.println("All Contributed!");

					error[l] = DataProcessor.computeErrorF1(
							correctNumPerClass[l],
							missclassifiedSamplesPerClass[l], tsNumHere);
					allCorrectNum += correctNumPerClass[l];
					allIncorrNum += missclassifiedSamplesPerClass[l];
					allTSNum += tsNumHere;

					// error[l] = DataProcessor.computeAccuracy(
					// correctNumPerClass[l], tsNumHere);
				}

				double allError = DataProcessor.computeErrorF1(allCorrectNum,
						allIncorrNum, allTSNum);

				ClassificationErrorEachSample clssifyError = new ClassificationErrorEachSample(
						allError, error);
				// consoleLogger.debug("## " + Arrays.toString(params[0]) + ", "
				// + error);
				return clssifyError;
			} else {
				return null;
			}

		} catch (Exception e) {
			System.err.println("Exception caught: " + StackTrace.toString(e));
			return null;
		}

	}

	/**
	 * Computes the value at point.
	 * 
	 * @param point
	 * @return
	 */
	public ClassificationErrorEachSample valueAtTransformMultiClass(Point point) {

		// point is in fact a aset of parameters - window, paa, and the alphabet
		//
		double[] coords = point.toArray();
		int windowSize = Long.valueOf(Math.round(coords[0])).intValue();
		int paaSize = Long.valueOf(Math.round(coords[1])).intValue();
		int alphabetSize = Long.valueOf(Math.round(coords[2])).intValue();

		// if we stepped above window length with PAA size - for some reason -
		// return the max possible
		// error value
		if (paaSize > windowSize) {
			return null;
		}

		// the whole thing begins here
		//
		try {
			// make a parameters vector
			int[][] params = new int[1][4];
			params[0][0] = windowSize;
			params[0][1] = paaSize;
			params[0][2] = alphabetSize;
			params[0][3] = this.numerosityReductionStrategy.index();
			// consoleLogger.debug("parameters: " + windowSize + ", " + paaSize
			// + ", " + alphabetSize + ", "
			// + this.numerosityReductionStrategy.toString());

			HashMap<String, int[]> allStartPositions = new HashMap<String, int[]>();

			// Concatenate training time series
			HashMap<String, double[]> concatenateData = DataProcessor
					.concatenateTrainInTrain(trainDataPerClass,
							allStartPositions);

			// TODO: write concatenated data.
			// writeConcatenatedData(concatenateData);

			// int tsLen = trainData.get(0).getValues().length;

			// For timing
			// long startTimeSequitur = System.currentTimeMillis();

			// Get representative patterns
			HashMap<String, TSPatterns> allPatterns = DataProcessor
					.getPatternsFromSequitur(concatenateData, params, giMethod,
							allStartPositions);
			concatenateData.clear();

			if (allPatterns == null) {
				return null;
			}

			double patternRate = 0.5;
			HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
					.selectTopFrequentPatterns(allPatterns, patternRate);
			// HashMap<String, TSPatterns> topFrequentPatterns = allPatterns;
			// allPatterns.clear();
			int clsNum = topFrequentPatterns.size();
			// // Number of incorrectly classified instances per class.
			// int[] missclassifiedSamplesPerClass = new int[clsNum];
			// // Number of correctly classified instances per class.
			// int[] correctNumPerClass = new int[clsNum];
			// // Initialized as zero.
			// Arrays.fill(missclassifiedSamplesPerClass, 0);
			// Arrays.fill(correctNumPerClass, 0);

			GCProcess gcp = new GCProcess(gcParams[0], gcParams[1],
					gcParams[2], gcParams[3], gcParams[4]);

			// For timing
			// long startTimeRepresentative = System.currentTimeMillis();

			// HashMap<String, TSPatterns> representativePatterns = gcp
			// .selectBestFromRNNTrain(topFrequentPatterns, 3,
			// trainDataPerClass);

			// Transform the original time series into the features space of
			// distance to selected patterns.
			PatternsAndTransformedData pTransformTS = gcp.transformTS(
					topFrequentPatterns, trainDataPerClass);
			// The transformed time series. The last column is the label of
			// class.
			double[][] transformedTS = pTransformTS.getTransformedTS();
			// Put patterns from each class together into an array.
			TSPattern[] allPatternsTogether = pTransformTS.getAllPatterns();

			// Apply feature selection method on the transformed data.
			int[] selectedIndices = gcp.featureSelection(transformedTS);
			// Selected patterns from the result of feature selection
			TSPattern[] selectedPatterns = new TSPattern[selectedIndices.length - 1];
			double[][] newTransformedTS = new double[transformedTS.length][selectedIndices.length];
			for (int i = 0; i < selectedIndices.length; i++) {
				if (i != selectedIndices.length - 1)
					selectedPatterns[i] = allPatternsTogether[selectedIndices[i]];
				for (int j = 0; j < transformedTS.length; j++) {
					newTransformedTS[j][i] = transformedTS[j][selectedIndices[i]];
				}
			}

			selectedRepresentativePatterns = selectedPatterns.clone();

			// Classify train with the transformed data to get error rate.
			Evaluation evaluation = gcp.cvEvaluationAllCls(transformedTS);
			double allError = evaluation.errorRate();
			double[] error = new double[clsNum];
			for (int i = 0; i < clsNum; i++) {
				error[i] = 1 - evaluation.fMeasure(i);
			}

			ClassificationErrorEachSample clssifyError = new ClassificationErrorEachSample(
					allError, error);
			return clssifyError;

		} catch (Exception e) {
			System.err.println("Exception caught: " + StackTrace.toString(e));
			return null;
		}

	}

	/**
	 * Computes the value at point.
	 * 
	 * @param point
	 * @return
	 */
	public double valueAtTransform(Point point) {

		// point is in fact a aset of parameters - window, paa, and the alphabet
		//
		double[] coords = point.toArray();
		int windowSize = Long.valueOf(Math.round(coords[0])).intValue();
		int paaSize = Long.valueOf(Math.round(coords[1])).intValue();
		int alphabetSize = Long.valueOf(Math.round(coords[2])).intValue();

		// if we stepped above window length with PAA size - for some reason -
		// return the max possible
		// error value
		if (paaSize > windowSize) {
			return 1;
		}

		// the whole thing begins here
		//
		try {
			// make a parameters vector
			int[][] params = new int[1][4];
			params[0][0] = windowSize;
			params[0][1] = paaSize;
			params[0][2] = alphabetSize;
			params[0][3] = this.numerosityReductionStrategy.index();
			// consoleLogger.debug("parameters: " + windowSize + ", " + paaSize
			// + ", " + alphabetSize + ", "
			// + this.numerosityReductionStrategy.toString());

			HashMap<String, int[]> allStartPositions = new HashMap<String, int[]>();

			// Concatenate training time series
			HashMap<String, double[]> concatenateData = DataProcessor
					.concatenateTrainInTrain(trainDataPerClass,
							allStartPositions);

			// TODO: write concatenated data.
			// writeConcatenatedData(concatenateData);

			// int tsLen = trainData.get(0).getValues().length;

			// For timing
			// long startTimeSequitur = System.currentTimeMillis();

			// Get representative patterns
			HashMap<String, TSPatterns> allPatterns = DataProcessor
					.getPatternsFromSequitur(concatenateData, params, giMethod,
							allStartPositions);
			concatenateData.clear();

			if (allPatterns == null) {
				return 1;
			}

			double patternRate = 0.5;
			HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
					.selectTopFrequentPatterns(allPatterns, patternRate);
			// HashMap<String, TSPatterns> topFrequentPatterns = allPatterns;
			// allPatterns.clear();
			int clsNum = topFrequentPatterns.size();
			// // Number of incorrectly classified instances per class.
			// int[] missclassifiedSamplesPerClass = new int[clsNum];
			// // Number of correctly classified instances per class.
			// int[] correctNumPerClass = new int[clsNum];
			// // Initialized as zero.
			// Arrays.fill(missclassifiedSamplesPerClass, 0);
			// Arrays.fill(correctNumPerClass, 0);

			GCProcess gcp = new GCProcess(gcParams[0], gcParams[1],
					gcParams[2], gcParams[3], gcParams[4]);

			// For timing
			// long startTimeRepresentative = System.currentTimeMillis();

			// HashMap<String, TSPatterns> representativePatterns = gcp
			// .selectBestFromRNNTrain(topFrequentPatterns, 3,
			// trainDataPerClass);

			// Transform the original time series into the features space of
			// distance to selected patterns.
			PatternsAndTransformedData pTransformTS = gcp.transformTS(
					topFrequentPatterns, trainDataPerClass);
			// The transformed time series. The last column is the label of
			// class.
			double[][] transformedTS = pTransformTS.getTransformedTS();
			// Put patterns from each class together into an array.
			TSPattern[] allPatternsTogether = pTransformTS.getAllPatterns();

			// Apply feature selection method on the transformed data.
			int[] selectedIndices = gcp.featureSelection(transformedTS);
			// Selected patterns from the result of feature selection
			TSPattern[] selectedPatterns = new TSPattern[selectedIndices.length - 1];
			double[][] newTransformedTS = new double[transformedTS.length][selectedIndices.length];
			for (int i = 0; i < selectedIndices.length; i++) {
				if (i != selectedIndices.length - 1)
					selectedPatterns[i] = allPatternsTogether[selectedIndices[i]];
				for (int j = 0; j < transformedTS.length; j++) {
					newTransformedTS[j][i] = transformedTS[j][selectedIndices[i]];
				}
			}

			selectedRepresentativePatterns = selectedPatterns.clone();

			// Classify train with the transformed data to get error rate.
			double allError = gcp.cvEvaluation(transformedTS);

			return allError;

		} catch (Exception e) {
			System.err.println("Exception caught: " + StackTrace.toString(e));
			return 1;
		}

	}

	/**
	 * Computes the value at point.
	 * 
	 * @param point
	 * @return
	 */
	public double[] valueAt(Point point) {

		// point is in fact a aset of parameters - window, paa, and the alphabet
		//
		double[] coords = point.toArray();
		int windowSize = Long.valueOf(Math.round(coords[0])).intValue();
		int paaSize = Long.valueOf(Math.round(coords[1])).intValue();
		int alphabetSize = Long.valueOf(Math.round(coords[2])).intValue();

		// if we stepped above window length with PAA size - for some reason -
		// return the max possible
		// error value
		if (paaSize > windowSize) {
			return null;
		}

		// the whole thing begins here
		//
		try {
			// make a parameters vector
			int[][] params = new int[1][4];
			params[0][0] = windowSize;
			params[0][1] = paaSize;
			params[0][2] = alphabetSize;
			params[0][3] = this.numerosityReductionStrategy.index();
			// consoleLogger.debug("parameters: " + windowSize + ", " + paaSize
			// + ", " + alphabetSize + ", "
			// + this.numerosityReductionStrategy.toString());

			HashMap<String, int[]> allStartPositions = new HashMap<String, int[]>();

			// Concatenate training time series
			HashMap<String, double[]> concatenateData = DataProcessor
					.concatenateTrainInTrain(trainDataPerClass,
							allStartPositions);

			// TODO: write concatenated data.
			// writeConcatenatedData(concatenateData);

			// int tsLen = trainData.get(0).getValues().length;

			// For timing
			long startTimeSequitur = System.currentTimeMillis();
			// Get representative patterns
			HashMap<String, TSPatterns> allPatterns = DataProcessor
					.getPatternsFromSequitur(concatenateData, params, giMethod,
							allStartPositions);
			concatenateData.clear();
			if (timing) {
				// For timing
				long endTimeSequitur = System.currentTimeMillis();
				long totalTimeSequitur = endTimeSequitur - startTimeSequitur;
				consoleLogger.debug("Get patterns from sequitur time: "
						+ DataProcessor.millisToShortDHMS(totalTimeSequitur));
			}
			if (allPatterns == null) {
				return null;
			}

			double patternRate = 0.5;
			// HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
			// .selectTopFrequentPatterns(allPatterns, patternRate);
			HashMap<String, TSPatterns> topFrequentPatterns = allPatterns;
			// HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
			// .selectTopFrequentPatterns(allPatterns, patternRate);

			// allPatterns.clear();
			if (topFrequentPatterns != null) {
				int clsNum = topFrequentPatterns.size();
				int[] missclassifiedSamplesPerClass = new int[clsNum];
				int[] correctNumPerClass = new int[clsNum];
				Arrays.fill(missclassifiedSamplesPerClass, 0);
				Arrays.fill(correctNumPerClass, 0);

				GCProcess gcp = new GCProcess(gcParams[0], gcParams[1],
						gcParams[2], gcParams[3], gcParams[4]);

				// For timing
				long startTimeRepresentative = System.currentTimeMillis();

				// Select most representative patterns from repeated patterns.
				HashMap<String, TSPatterns> representativePatterns = gcp
						.selectBestFromRNNTrain(topFrequentPatterns, 3,
								trainDataPerClass);

				topFrequentPatterns.clear();
				if (timing) {
					// For timing
					long endTimeRepresentative = System.currentTimeMillis();
					long totalTimeRepresentative = endTimeRepresentative
							- startTimeRepresentative;
					consoleLogger
							.info("Find Representative pattern time: "
									+ DataProcessor
											.millisToShortDHMS(totalTimeRepresentative));
				}

				thisRPatterns = (HashMap<String, TSPatterns>) representativePatterns
						.clone();

				int[] inTrainNum = new int[clsNum];
				for (TimeSeriesTrain tsTrainTest : trainData) {

					String trueClassLabel = tsTrainTest.getTrueLable();

					int res = classifyTrain(trueClassLabel, tsTrainTest,
							representativePatterns);

					if (res == -10) {
						// This time series contributed a pattern in
						// representative patterns.
						int clsLabel = Integer.parseInt(trueClassLabel) - 1;
						inTrainNum[clsLabel] += 1;
					} else {
						if (-1 == res) {
							int clsLabel = Integer.parseInt(trueClassLabel) - 1;
							correctNumPerClass[clsLabel] += 1;
						} else {
							// missclassifiedSamplesPerClass[clsLabel] += 1;
							missclassifiedSamplesPerClass[res] += 1;
						}
					}
				}
				representativePatterns.clear();
				double[] error = new double[clsNum];

				int allCorrectNum = 0;
				int allIncorrNum = 0;
				int allTSNum = 0;
				for (Entry<String, List<TimeSeriesTrain>> entry : trainDataPerClass
						.entrySet()) {
					String label = entry.getKey();
					List<TimeSeriesTrain> tses = entry.getValue();

					int l = Integer.parseInt(label) - 1;
					int tsNumHere = tses.size() - inTrainNum[l];

					if (tsNumHere <= 0)
						System.err.println("All Contributed!");

					error[l] = DataProcessor.computeErrorF1(
							correctNumPerClass[l],
							missclassifiedSamplesPerClass[l], tsNumHere);
					allCorrectNum += correctNumPerClass[l];
					allIncorrNum += missclassifiedSamplesPerClass[l];
					allTSNum += tsNumHere;

					// error[l] = DataProcessor.computeAccuracy(
					// correctNumPerClass[l], tsNumHere);
				}

				// consoleLogger.debug("## " + Arrays.toString(params[0]) + ", "
				// + error);
				return error;
			} else {
				return null;
			}

		} catch (Exception e) {
			System.err.println("Exception caught: " + StackTrace.toString(e));
			return null;
		}

	}

	//
	// /**
	// * Computes the value at point. Dynamic
	// *
	// * @param point
	// * @return
	// */
	// public double[] valueAtDynamic(Point point) {
	//
	// // point is in fact a aset of parameters - window, paa, and the alphabet
	// //
	// double[] coords = point.toArray();
	// int windowSize = Long.valueOf(Math.round(coords[0])).intValue();
	// int paaSize = Long.valueOf(Math.round(coords[1])).intValue();
	// int alphabetSize = Long.valueOf(Math.round(coords[2])).intValue();
	//
	// // if we stepped above window length with PAA size - for some reason -
	// // return the max possible
	// // error value
	// if (paaSize > windowSize) {
	// return null;
	// }
	//
	// // the whole thing begins here
	// //
	// try {
	// // make a parameters vector
	// int[][] params = new int[1][4];
	// params[0][0] = windowSize;
	// params[0][1] = paaSize;
	// params[0][2] = alphabetSize;
	// params[0][3] = this.numerosityReductionStrategy.index();
	// consoleLogger.debug("parameters: " + windowSize + ", " + paaSize
	// + ", " + alphabetSize + ", "
	// + this.numerosityReductionStrategy.toString());
	//
	// HashMap<String, int[]> allStartPositions = new HashMap<String, int[]>();
	// // Concatenate training time series
	// HashMap<String, double[]> concatenateData = DataProcessor
	// .concatenateTrainInTrain(trainDataPerClass,
	// allStartPositions);
	//
	// // TODO: write concatenated data.
	// // writeConcatenatedData(concatenateData);
	//
	// int tsLen = trainData.get(0).getValues().length;
	//
	// // For timing
	// long startTimeSequitur = System.currentTimeMillis();
	// // Get representative patterns
	// HashMap<String, TSPatterns> allPatterns = DataProcessor
	// .getPatternsFromSequitur(concatenateData, params, tsLen,
	// giMethod, allStartPositions);
	//
	// if (timing) {
	// // For timing
	// long endTimeSequitur = System.currentTimeMillis();
	// long totalTimeSequitur = endTimeSequitur - startTimeSequitur;
	// consoleLogger.debug("Get patterns from sequitur time: "
	// + DataProcessor.millisToShortDHMS(totalTimeSequitur));
	// }
	// if (allPatterns == null) {
	// return null;
	// }
	//
	// double patternRate = 0.5;
	// // HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
	// // .selectTopFrequentPatterns(allPatterns, patternRate);
	// HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
	// .selectTopFrequentPatterns(allPatterns, patternRate);
	// if (topFrequentPatterns != null) {
	//
	// int clsNum = topFrequentPatterns.size();
	// int[] missclassifiedSamplesPerClass = new int[clsNum];
	// int[] correctNumPerClass = new int[clsNum];
	// Arrays.fill(missclassifiedSamplesPerClass, 0);
	// Arrays.fill(correctNumPerClass, 0);
	//
	// GCProcess gcp = new GCProcess(gcParams[0], gcParams[1],
	// gcParams[2], gcParams[3], gcParams[4]);
	//
	// // For timing
	// long startTimeRepresentative = System.currentTimeMillis();
	//
	// ArrayList<TSPattern> allPatternsList =
	// putPatternInArrayList(topFrequentPatterns);
	// DistMatrixElement[][] distMatrixE = gcp.buildDistMatrix(
	// allPatternsList, trainData);
	//
	// HashMap<String, TSPatterns> representativePatterns = new HashMap<String,
	// TSPatterns>();
	//
	// double[] error = gcp.calcRepresentativePatternsAndError(
	// allPatternsList, trainData, trainDataPerClass,
	// representativePatterns, distMatrixE);
	//
	// if (timing) {
	// // For timing
	// long endTimeRepresentative = System.currentTimeMillis();
	// long totalTimeRepresentative = endTimeRepresentative
	// - startTimeRepresentative;
	// consoleLogger
	// .info("Find Representative pattern time: "
	// + DataProcessor
	// .millisToShortDHMS(totalTimeRepresentative));
	// }
	//
	// thisRPatterns = (HashMap<String, TSPatterns>) representativePatterns
	// .clone();
	//
	// return error;
	// } else {
	// return null;
	// }
	//
	// } catch (Exception e) {
	// System.err.println("Exception caught: " + StackTrace.toString(e));
	// return null;
	// }
	//
	// }

	public ArrayList<TSPattern> putPatternInArrayList(
			HashMap<String, TSPatterns> topFrequentPatterns) {
		// put all pattern in a array
		ArrayList<TSPattern> allPatterns = new ArrayList<TSPattern>();
		for (Entry<String, TSPatterns> patterns : topFrequentPatterns
				.entrySet()) {
			String patternLabel = patterns.getKey();
			TSPatterns patternsThisClass = patterns.getValue();

			// pattern index in a class.
			for (int patternIdx = 0; patternIdx < patternsThisClass
					.getPatterns().size(); patternIdx++) {
				TSPattern pattern = patternsThisClass.getPatterns().get(
						patternIdx);

				allPatterns.add(pattern);
			}
		}
		return allPatterns;
	}

	public double computeError(int correctNum, int misClassifiedNum,
			int tsNumHere) {
		double c = correctNum - misClassifiedNum;

		return 1 - (c / tsNumHere);
	}

	//
	// /**
	// * Computes the value at point.
	// *
	// * @param point
	// * @return
	// */
	// public double[] valueAt(Point point, double[] previousError) {
	//
	// // point is in fact a aset of parameters - window, paa, and the alphabet
	// //
	// double[] coords = point.toArray();
	// int windowSize = Long.valueOf(Math.round(coords[0])).intValue();
	// int paaSize = Long.valueOf(Math.round(coords[1])).intValue();
	// int alphabetSize = Long.valueOf(Math.round(coords[2])).intValue();
	//
	// // if we stepped above window length with PAA size - for some reason -
	// // return the max possible
	// // error value
	// if (paaSize > windowSize) {
	// return null;
	// }
	//
	// // the whole thing begins here
	// //
	// try {
	// // make a parameters vector
	// int[][] params = new int[1][4];
	// params[0][0] = windowSize;
	// params[0][1] = paaSize;
	// params[0][2] = alphabetSize;
	// params[0][3] = this.numerosityReductionStrategy.index();
	// consoleLogger.debug("parameters: " + windowSize + ", " + paaSize
	// + ", " + alphabetSize + ", "
	// + this.numerosityReductionStrategy.toString());
	//
	// // Concatenate training time series
	// HashMap<String, double[]> concatenateData = DataProcessor
	// .concatenateTrain(trainData);
	//
	// // TODO: write concatenated data.
	// // writeConcatenatedData(concatenateData);
	//
	// int tsLen = tsDataTrain.entrySet().iterator().next().getValue().length;
	// // Get representative patterns
	// HashMap<String, TSPatterns> allPatterns = DataProcessor
	// .getPatternsFromSequitur(concatenateData, params, tsLen,
	// previousError, giMethod);
	//
	// if (allPatterns == null) {
	// return null;
	// }
	//
	// double patternRate = 0.5;
	// HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
	// .selectTopFrequentPatterns(allPatterns, patternRate);
	// if (topFrequentPatterns != null) {
	// int clsNum = previousError.length;
	// int[] missclassifiedSamplesPerClass = new int[clsNum];
	// int[] correctNumPerClass = new int[clsNum];
	// Arrays.fill(missclassifiedSamplesPerClass, 0);
	// Arrays.fill(correctNumPerClass, 0);
	//
	// GCProcess gcp = new GCProcess(gcParams[0], gcParams[1],
	// gcParams[2], gcParams[3]);
	// HashMap<String, TSPatterns> representativePatterns = gcp
	// .selectBestFromRNN(topFrequentPatterns, 3, forRNNData);
	//
	// // while something is in the stack
	// // while (!samples2go.isEmpty()) {
	// for (Entry<String, double[]> tsTrainTest : tsDataValidate
	// .entrySet()) {
	//
	// // consoleLogger.debug("cross valiadtion iteration, in stack "
	// // + samples2go.size() + " series");
	//
	// // extracting validation samples batch and building to
	// // remove
	// // collection
	// //
	// List<String> currentValidationSample = new ArrayList<String>();
	// // for (int i = 0; i < this.holdOutSampleSize
	// // && !samples2go.isEmpty(); i++) {
	// //
	// // String seriesKey = samples2go.pop();
	//
	// String seriesKey = tsTrainTest.getKey();
	//
	// String trueClassLabel = seriesKey.substring(0,
	// seriesKey.indexOf(DELIMITER));
	// currentValidationSample.add(seriesKey);
	//
	// int res = classifyTrain(trueClassLabel,
	// tsTrainTest.getValue(), representativePatterns);
	// if (-1 == res) {
	// int clsLabel = Integer.parseInt(trueClassLabel) - 1;
	// correctNumPerClass[clsLabel] += 1;
	// } else {
	// // missclassifiedSamplesPerClass[clsLabel] += 1;
	// missclassifiedSamplesPerClass[res] += 1;
	// }
	// }
	//
	// double[] error = new double[clsNum];
	// for (Entry<String, List<double[]>> entry : validateData
	// .entrySet()) {
	// String label = entry.getKey();
	// List<double[]> tses = entry.getValue();
	//
	// int tsNumHere = tses.size();
	// int l = Integer.parseInt(label) - 1;
	//
	// if (previousError[l] == 0) {
	// error[l] = 0;
	// System.out.println("hi");
	// continue;
	// }
	//
	// double precision = 1;
	// if (correctNumPerClass[l]
	// + missclassifiedSamplesPerClass[l] != 0) {
	// precision = ((double) correctNumPerClass[l])
	// / ((double) correctNumPerClass[l] + (double)
	// missclassifiedSamplesPerClass[l]);
	// }
	// double recall = ((double) correctNumPerClass[l])
	// / ((double) tsNumHere);
	//
	// double f1Score = 0;
	// if (precision + recall != 0) {
	// f1Score = (2 * precision * recall)
	// / (precision + recall);
	// }
	// error[l] = 1 - f1Score;
	// }
	//
	// // consoleLogger.debug("## " + Arrays.toString(params[0]) + ", "
	// // + error);
	// return error;
	// } else {
	// return null;
	// }
	//
	// } catch (Exception e) {
	// System.err.println("Exception caught: " + StackTrace.toString(e));
	// return null;
	// }
	//
	// }

	private void writeConcatenatedData(HashMap<String, double[]> concatenateData) {

		String dirPath = "Result/concatenatedFiles/";
		for (Entry<String, double[]> entry : concatenateData.entrySet()) {
			String label = entry.getKey();
			double[] ts = entry.getValue();

			String fileName = "concatenated" + label;
			String fullPath = dirPath + fileName;

			try {
				File file = new File(fullPath);
				File dirFile = new File(dirPath);
				if (!(dirFile.isDirectory())) {
					dirFile.mkdirs();
				}

				if (!(file.exists())) {
					file.createNewFile();
				}

				StringBuffer sb = new StringBuffer();
				for (double d : ts) {
					sb.append(d);
					sb.append("\n");
				}

				BufferedWriter output = new BufferedWriter(new FileWriter(file));
				output.write(sb.toString());
				output.close();
				System.out.println("\nWritten to file: "
						+ file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private HashMap<String, double[]> adjustTSs(Map<String, double[]> allTSs,
			HashMap<String, double[]> tsToRemove) {

		HashMap<String, double[]> res = new HashMap<String, double[]>();
		for (Entry<String, double[]> e : allTSs.entrySet()) {
			res.put(e.getKey(), e.getValue().clone());
		}

		for (Entry<String, double[]> e : tsToRemove.entrySet()) {
			String classKey = e.getKey();

			res.remove(classKey);
		}

		return res;
	}

	private int classifyTrain(String trueClassKey, double[] oneSampleTS,
			HashMap<String, TSPatterns> representativePatterns) {
		int k = 1;
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2],
				gcParams[3], gcParams[4]);
		String assignedLabel = gcp.knnClassify(oneSampleTS,
				representativePatterns, k);

		if (assignedLabel.equalsIgnoreCase(trueClassKey)) {
			return -1;
		}
		// return 0;
		return Integer.parseInt(assignedLabel) - 1;
	}

	private int classifyTrain(String trueClassKey, TimeSeriesTrain oneSampleTS,
			HashMap<String, TSPatterns> representativePatterns) {
		int k = 1;
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2],
				gcParams[3], gcParams[4]);
		String assignedLabel = gcp.knnClassifyTrain(oneSampleTS,
				representativePatterns, k);
		// String assignedLabel = gcp.knnClassifyTrainMinOverlap(oneSampleTS,
		// representativePatterns, k);

		if (assignedLabel.equalsIgnoreCase("PatternFromThisTS")) {
			return -10;
		}

		if (assignedLabel.equalsIgnoreCase(trueClassKey)) {
			return -1;
		}
		// return 0;
		return Integer.parseInt(assignedLabel) - 1;
	}

	//
	// private int classify(String trueClassKey, double[] oneSampleTS,
	// HashMap<String, double[]> concatenateData,
	// NumerosityReductionStrategy strategy, int[][] params,
	// int originalLen, GrammarIndcutionMethod giMethod,
	// HashMap<String, int[]> allStartPositions) {
	//
	// HashMap<String, TSPatterns> allPatterns = DataProcessor
	// .getPatternsFromSequitur(concatenateData, params,
	// giMethod, allStartPositions);
	//
	// // Selected the top k most frequent subsequences for each class.
	// HashMap<String, TSPatterns> refinedPatterns =
	// refinePatterns(allPatterns);
	// if (refinedPatterns == null)
	// return 0;
	//
	// // TODO: Find the reversed nearest neighbor;
	//
	// double minDist = -1.0d;
	// String className = "";
	// for (Entry<String, TSPatterns> e : refinedPatterns.entrySet()) {
	// // Calculate the distance between input time series and patterns.
	// double dist = calculateDist(oneSampleTS, e.getValue());
	//
	// if (dist > minDist) {
	// className = e.getKey();
	// minDist = dist;
	// }
	// }
	//
	// if (className.equalsIgnoreCase(trueClassKey)) {
	// return 1;
	// }
	// return 0;
	// }

	/**
	 * Distance between time series and a bunch of subsequences
	 * 
	 * @param wholeTS
	 * @param subsequences
	 * @return
	 */
	private double calculateDist(double[] wholeTS, TSPatterns subsequences) {

		double dist = 0;
		int idx = 0;
		for (TSPattern tsp : subsequences.getPatterns()) {
			double[] subsequence = tsp.getPatternTS();
			idx++;
			dist += calculateDistTS(wholeTS, subsequence);
		}

		double meanDist = -1d;
		if (idx != 0)
			meanDist = dist / idx;

		return meanDist;
	}

	/**
	 * Calculate distance between time series and a subsequence.
	 * 
	 * @param wholeTS
	 * @param subsequence
	 * @return
	 */
	private double calculateDistTS(double[] wholeTS, double[] subsequence) {

		double dist = 1000000;

		int subsequenceLen = subsequence.length;
		int tsLen = wholeTS.length;

		for (int i = 0; i < tsLen - subsequenceLen - 1; i++) {
			double[] subTS = Arrays.copyOfRange(wholeTS, i, subsequenceLen + i);
			Double sum = 0D;
			for (int j = 0; j < subTS.length; j++) {
				sum = sum + (subTS[j] - subsequence[j])
						* (subTS[j] - subsequence[j]);
			}
			sum = Math.sqrt(sum);
			if (dist > sum) {
				dist = sum;
			}
		}

		return dist;
	}

	private HashMap<String, TSPatterns> refinePatterns(
			HashMap<String, TSPatterns> allPatterns) {
		HashMap<String, TSPatterns> refinedPatterns = new HashMap<String, TSPatterns>();

		int k = 3;

		for (Entry<String, TSPatterns> e : allPatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();
			ArrayList<TSPattern> tempTSPatterns = (ArrayList<TSPattern>) patternsInClass
					.getPatterns().clone();

			if (tempTSPatterns.size() < k) {
				return null;
			}

			Collections.sort(tempTSPatterns, Collections.reverseOrder());

			TSPatterns mostFrequentPatterns = new TSPatterns(classLabel);
			for (int i = 0; i < k; i++) {
				TSPattern pattern = tempTSPatterns.get(i);
				mostFrequentPatterns.addPattern(pattern);
			}
			refinedPatterns.put(classLabel, mostFrequentPatterns);
		}

		return refinedPatterns;
	}

	//
	// private HashMap<String, TSPatterns> getPatterns(
	// HashMap<String, double[]> concatenateData, int[][] params,
	// int originalLen) {
	//
	// HashMap<String, TSPatterns> allPatterns = new HashMap<String,
	// TSPatterns>();
	//
	// int windowSize = params[0][0];
	// int paaSize = params[0][1];
	// int alphabetSize = params[0][2];
	// int strategy = params[0][3];
	// String nRStrategy = "nr_exact";
	// if (strategy == SAXNumerosityReductionStrategy.NOREDUCTION.index()) {
	// nRStrategy = "nr_off";
	// } else if (strategy == SAXNumerosityReductionStrategy.CLASSIC.index()) {
	// nRStrategy = "nr_mindist";
	// }
	//
	// int classNum = 1;
	// for (Entry<String, double[]> entry : concatenateData.entrySet()) {
	// String classLabel = entry.getKey();
	// double[] concatenatedTS = entry.getValue();
	//
	// // String folderName =
	// //
	// "C:\\Users\\user.2012ETF\\Dropbox\\work\\Research\\grammar\\data\\temp\\";
	// // String fileName = folderName + "concatenated" + classLabel +
	// // ".txt";
	// // wirteFile(concatenatedTS, fileName);
	//
	// Sequitur s = new Sequitur();
	//
	// // Concatenate training time series
	// // long startTime = System.currentTimeMillis();
	// ArrayList<int[]> patternsLocation = s.getGrammars(
	// String.valueOf(windowSize), String.valueOf(paaSize),
	// String.valueOf(alphabetSize), nRStrategy,
	// Integer.parseInt(classLabel), concatenatedTS, originalLen);
	// // long endTime = System.currentTimeMillis();
	// // long totalTime = endTime - startTime;
	// // System.out.println(DataProcessor.millisToShortDHMS(totalTime));
	//
	// TSPatterns patterns = new TSPatterns(classLabel);
	// // String patternFileName = folderName + "patterns_" + classLabel;
	//
	// readPatterns(concatenatedTS, patternsLocation, patterns);
	//
	// // System.out.println("Class: " + classLabel + " has "
	// // + patterns.getPatterns().size() + " patterns");
	// allPatterns.put(classLabel, patterns);
	// }
	//
	// return allPatterns;
	// }

	private void readPatterns(double[] concatenatedTS,
			ArrayList<int[]> patternsLocation, TSPatterns patterns) {
		for (int[] location : patternsLocation) {
			int startPosition = location[0];
			if (startPosition < 1) {
				continue;
			}

			int patternLength = location[1];
			int frequency = location[2];

			double[] patternTS = Arrays.copyOfRange(concatenatedTS,
					startPosition - 1, startPosition + patternLength);

			TSPattern tp = new TSPattern(frequency, patternTS,
					patterns.getLabel(), startPosition);
			patterns.addPattern(tp);
		}

	}

	// private void readPatterns(double[] concatenatedTS, String
	// patternFileName,
	// TSPatterns patterns) {
	// ArrayList<Double> data = new ArrayList<Double>();
	// Path path = Paths.get(patternFileName);
	// try {
	// BufferedReader reader = Files.newBufferedReader(path,
	// DEFAULT_CHARSET);
	// // read by the line in the loop from reader
	// String line = null;
	// while ((line = reader.readLine()) != null) {
	// String[] lineSplit = line.trim().split("\\s+");
	// // we read only first column
	// // for (int i = 0; i < lineSplit.length; i++) {
	// int startPosition = Integer.parseInt(lineSplit[0]);
	// if (startPosition < 1) {
	// continue;
	// }
	//
	// int patternLength = Integer.parseInt(lineSplit[1]);
	// int frequency = Integer.parseInt(lineSplit[2]);
	//
	// double[] patternTS = Arrays.copyOfRange(concatenatedTS,
	// startPosition - 1, startPosition + patternLength);
	//
	// TSPattern tp = new TSPattern(frequency, patternTS);
	// patterns.addPattern(tp);
	// }
	// reader.close();
	// } catch (Exception e) {
	// }
	//
	// }

	private void wirteFile(double[] concatenatedTS, String fileName) {

		try {
			File file = new File(fileName);
			if (file.exists()) {
				file.delete();
			}
			FileWriter fwriter = new FileWriter(fileName);
			BufferedWriter bwriter = new BufferedWriter(fwriter);

			for (int idx = 0; idx < concatenatedTS.length; idx++) {

				double tsValue = concatenatedTS[idx];
				bwriter.write(Double.toString(tsValue));
				bwriter.write("\n");
			}

			// System.out.println("Write to file: " + fileName);
			bwriter.close();
		} catch (Exception exception) {
		}
	}
}
