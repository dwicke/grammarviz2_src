package edu.gmu.grammar.classification;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.gmu.connectGI.GrammarIndcutionMethod;
import edu.gmu.grammar.classification.util.*;
import edu.gmu.grammar.patterns.BestSelectedPatterns;
import edu.gmu.grammar.patterns.TSPattern;
import edu.gmu.grammar.patterns.TSPatterns;
import edu.gmu.grammar.transform.PatternsAndTransformedData;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.jmotif.sax.TSProcessor;
import net.seninp.jmotif.sax.alphabet.Alphabet;
import net.seninp.jmotif.sax.alphabet.NormalAlphabet;
import net.seninp.util.StackTrace;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.LoggerFactory;
import weka.attributeSelection.*;
import weka.classifiers.*;
import weka.classifiers.bayes.*;
import weka.classifiers.functions.*;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.classifiers.lazy.IBk;
//import weka.classifiers.meta.*;
//import weka.classifiers.mi.*;
import weka.classifiers.trees.*;
import weka.core.*;
import weka.filters.AllFilter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;
import java.util.Map.Entry;

// import libsvm.*;

public class GCProcessBackup {

	private static final double INF = 10000000000000000000f;
	private static final Logger consoleLogger;
	private static final Level LOGGING_LEVEL = Level.INFO;
	static {
		consoleLogger = (Logger) LoggerFactory.getLogger(GCProcessBackup.class);
		consoleLogger.setLevel(LOGGING_LEVEL);
	}

	private boolean usingMaxDist;
	private boolean usingMinDix;
	private boolean isDTW;
	private boolean isNormalize;
	private boolean isAssignOther;
	private static final String CR = "\n";

	private static final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(
			Locale.US);
	private static DecimalFormat fmt = new DecimalFormat("0.00###",
			otherSymbols);

	public GCProcessBackup(boolean usingMaxDist, boolean isDTW, boolean isNormalize,
			boolean usingMinDix, boolean isAssignOther) {
		this.usingMaxDist = usingMaxDist;
		this.isDTW = isDTW;
		this.isNormalize = isNormalize;
		this.usingMinDix = usingMinDix;
		this.isAssignOther = isAssignOther;
	}

	public static void main(String[] args) {
		// try {
		// if (5 == args.length) {
		// // Get parameters
		// TRAINING_DATA = args[0];
		// TEST_DATA = args[1];
		//
		// int windowSize = Integer.valueOf(args[2]).intValue();
		// int paaSize = Integer.valueOf(args[3]).intValue();
		// int alphabetSize = Integer.valueOf(args[4]).intValue();
		// int[][] params = new int[1][4];
		// params[0][0] = windowSize;
		// params[0][1] = paaSize;
		// params[0][2] = alphabetSize;
		// SAXNumerosityReductionStrategy strategy =
		// SAXNumerosityReductionStrategy.EXACT;
		// params[0][3] = strategy.index();
		//
		// // Load data into program
		// Map<String, List<double[]>> tData = UCRUtils
		// .readUCRData(TRAINING_DATA);
		//
		// trainData = new HashMap<String, List<double[]>>();
		// trainDataTest = new HashMap<String, List<double[]>>();
		// for (Entry<String, List<double[]>> e : tData.entrySet()) {
		// String classLabel = e.getKey();
		// List<double[]> tses = e.getValue();
		// int tsNum = tses.size();
		// int halfTSNum = tsNum / 2;
		//
		// List<double[]> train = tses.subList(0, halfTSNum);
		// trainData.put(classLabel, train);
		//
		// List<double[]> test = tses.subList(halfTSNum, tsNum);
		// trainDataTest.put(classLabel, test);
		// }
		//
		// consoleLogger.info("trainData classes: "
		// + trainData.size()
		// + ", series length: "
		// + trainData.entrySet().iterator().next().getValue()
		// .get(0).length);
		// for (Entry<String, List<double[]>> e : trainData.entrySet()) {
		// consoleLogger.info(" training class: " + e.getKey()
		// + " series: " + e.getValue().size());
		// }
		// testData = UCRUtils.readUCRData(TEST_DATA);
		// consoleLogger.info("testData classes: "
		// + testData.size()
		// + ", series length: "
		// + testData.entrySet().iterator().next().getValue()
		// .get(0).length);
		// for (Entry<String, List<double[]>> e : testData.entrySet()) {
		// consoleLogger.info(" test class: " + e.getKey()
		// + " series: " + e.getValue().size());
		// }
		//
		// // 1. Concatenating training time series.
		// HashMap<String, double[]> concatenatedData = DataProcessor
		// .concatenateTrain(trainData);
		//
		// // 2. Get patterns from concatenated time series.
		// int tsLen = trainData.entrySet().iterator().next().getValue()
		// .get(0).length;
		// HashMap<String, TSPatterns> allPatterns = DataProcessor
		// .getPatternsFromSequitur(concatenatedData, params,
		// tsLen);
		//
		// System.out.println("Selecting patterns...");
		// double patternRate = 0.5;
		// // 3. Train representative pattern for each class.
		// HashMap<String, TSPatterns> topFrequentPatterns =
		// selectTopFrequentPatterns(
		// allPatterns, patternRate);
		// if (topFrequentPatterns != null) {
		// HashMap<String, TSPatterns> representativePatterns =
		// selectBestFromRNN(
		// topFrequentPatterns, 3, trainDataTest);
		// System.out.println("Selecting patterns done!");
		//
		// // Do Classification.
		// System.out.println("Classification testing data...");
		// classifyTestData(representativePatterns, testData);
		// } else {
		// System.err.println("No enough patterns...");
		// System.exit(-10);
		// }
		//
		// System.out.println("All done!");
		// } else {
		// System.out.print(printHelp());
		// System.exit(-10);
		// }
		// } catch (Exception e) {
		// System.err.println("There was parameters error....");
		// System.err.println(StackTrace.toString(e));
		// System.out.print(printHelp());
		// System.exit(-10);
		// }

	}

	//
	// public void doClassify(int[][] sequiturParams,
	// Map<String, List<double[]>> trainData,
	// Map<String, List<double[]>> testData,
	// Map<String, List<double[]>> validateData,
	// GrammarIndcutionMethod giMethod) {
	//
	// try {
	//
	// consoleLogger
	// .info("trainData classes: "
	// + trainData.size()
	// + ", series length: "
	// + trainData.entrySet().iterator().next().getValue()
	// .get(0).length);
	// for (Entry<String, List<double[]>> e : trainData.entrySet()) {
	// consoleLogger.info(" training class: " + e.getKey()
	// + " series: " + e.getValue().size());
	// }
	// // testData = UCRUtils.readUCRData(TEST_DATA);
	// consoleLogger
	// .info("testData classes: "
	// + testData.size()
	// + ", series length: "
	// + testData.entrySet().iterator().next().getValue()
	// .get(0).length);
	// for (Entry<String, List<double[]>> e : testData.entrySet()) {
	// consoleLogger.info(" test class: " + e.getKey() + " series: "
	// + e.getValue().size());
	// }
	//
	// HashMap<String, TSPatterns> allPatterns = new HashMap<String,
	// TSPatterns>();
	//
	// // 1. Concatenating training time series.
	// HashMap<String, double[]> concatenatedData = DataProcessor
	// .concatenateTrain(trainData);
	//
	// for (int i = 0; i < sequiturParams.length; i++) {
	// int[] paramClsI = sequiturParams[i];
	//
	// int windowSize = paramClsI[0];
	// int paaSize = paramClsI[1];
	// int alphabetSize = paramClsI[2];
	// int[][] params = new int[1][4];
	// params[0][0] = windowSize;
	// params[0][1] = paaSize;
	// params[0][2] = alphabetSize;
	// params[0][3] = paramClsI[3];
	//
	// // 2. Get patterns from concatenated time series.
	// int tsLen = trainData.entrySet().iterator().next().getValue()
	// .get(0).length;
	// TSPatterns patternsForI = DataProcessor
	// .getPatternsFromSequiturForClassI(concatenatedData,
	// params, tsLen, i, giMethod);
	//
	// String classLabel = String.valueOf(i + 1);
	// allPatterns.put(classLabel, patternsForI);
	// }
	//
	// System.out.println("Selecting patterns...");
	// double patternRate = 0.5;
	// // 3. Train representative pattern for each class.
	// HashMap<String, TSPatterns> topFrequentPatterns =
	// selectTopFrequentPatterns(
	// allPatterns, patternRate);
	// if (topFrequentPatterns != null) {
	// HashMap<String, TSPatterns> representativePatterns = selectBestFromRNN(
	// topFrequentPatterns, 3, validateData);
	// System.out.println("Selecting patterns done!");
	//
	// // Do Classification.
	// System.out.println("Classification testing data...");
	// classifyTestData(representativePatterns, testData);
	// } else {
	// System.err.println("No enough patterns...");
	// System.exit(-10);
	// }
	//
	// System.out.println("Classification finished!");
	//
	// } catch (Exception e) {
	// System.err.println("There was parameters error....");
	// System.err.println(StackTrace.toString(e));
	// System.out.print(printHelp());
	// System.exit(-10);
	// }
	//
	// }
	//
	// public void doClassifyNewMethodAllPatterns(int[][] sequiturParams,
	// Map<String, List<double[]>> trainData,
	// Map<String, List<double[]>> testData,
	// Map<String, List<double[]>> validateData,
	// Map<String, List<double[]>> forRNNData,
	// GrammarIndcutionMethod giMethod) {
	//
	// try {
	// consoleLogger
	// .info("trainData classes: "
	// + trainData.size()
	// + ", series length: "
	// + trainData.entrySet().iterator().next().getValue()
	// .get(0).length);
	// for (Entry<String, List<double[]>> e : trainData.entrySet()) {
	// consoleLogger.info(" training class: " + e.getKey()
	// + " series: " + e.getValue().size());
	// }
	// consoleLogger
	// .info("testData classes: "
	// + testData.size()
	// + ", series length: "
	// + testData.entrySet().iterator().next().getValue()
	// .get(0).length);
	// for (Entry<String, List<double[]>> e : testData.entrySet()) {
	// consoleLogger.info(" test class: " + e.getKey() + " series: "
	// + e.getValue().size());
	// }
	//
	// // 1. Concatenating training time series.
	// HashMap<String, double[]> concatenatedData = DataProcessor
	// .concatenateTrain(trainData);
	//
	// // DataProcessor.writeConcatenatedData(concatenatedData);
	//
	// ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);
	//
	// int clsNum = sequiturParams.length;
	// for (int i = 0; i < clsNum; i++) {
	//
	// consoleLogger.info("Classifying class " + (i + 1));
	// int[] paramClsI = sequiturParams[i];
	//
	// int windowSize = paramClsI[0];
	// int paaSize = paramClsI[1];
	// int alphabetSize = paramClsI[2];
	// int[][] params = new int[1][4];
	// params[0][0] = windowSize;
	// params[0][1] = paaSize;
	// params[0][2] = alphabetSize;
	// params[0][3] = paramClsI[3];
	//
	// String classLabel = String.valueOf(i + 1);
	//
	// // 2. Get patterns from concatenated time series.
	// int tsLen = trainData.entrySet().iterator().next().getValue()
	// .get(0).length;
	// HashMap<String, TSPatterns> allPatterns = DataProcessor
	// .getAllPatternsFromSequitur(concatenatedData, params,
	// tsLen, giMethod);
	//
	// // System.out.println("Selecting patterns...");
	// double patternRate = 0.5;
	// // 3. Train representative pattern for each class.
	// HashMap<String, TSPatterns> topFrequentPatterns =
	// selectTopFrequentPatterns(
	// allPatterns, patternRate);
	// if (topFrequentPatterns != null) {
	//
	// selectTop3Each(topFrequentPatterns);
	//
	// HashMap<String, TSPatterns> representativePatterns = selectBestFromRNN(
	// topFrequentPatterns, 3, forRNNData);
	// // System.out.println("Selecting patterns done!");
	//
	// // Do Classification.
	// // System.out.println("Classification testing data...");
	// classifyTestDataByClassAllPatterns(representativePatterns,
	// allTestData, classLabel);
	//
	// } else {
	// System.err.println("No enough patterns...");
	// System.exit(-10);
	// }
	//
	// }
	//
	// // classifyUnlabeledData(allTestData, clsNum);
	//
	// displayAccuracy(allTestData, testData);
	// System.out.println("Classification finished!");
	//
	// } catch (Exception e) {
	// System.err.println("There was parameters error....");
	// System.err.println(StackTrace.toString(e));
	// System.out.print(printHelp());
	// System.exit(-10);
	// }
	//
	// }

	public void selectTop3Each(
			HashMap<String, TSPatterns> representativePatterns) {
		for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
			TSPatterns tps = e.getValue();

			for (TSPattern tp : tps.getPatterns()) {
				ArrayList<double[]> newTSes = new ArrayList<double[]>();
				ArrayList<double[]> tses = tp.getPatternsInClass();
				int count = 3;
				while (count > 0) {
					int idx = (int) (Math.random() * tses.size() - 1);

					double[] ts = tses.get(idx);
					newTSes.add(ts.clone());
					count--;
				}
				tp.setPatternsInClass(newTSes);
			}
		}
	}

	public void doClassifyNewMethod(int[][] sequiturParams,
			Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData,
			Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData,
			GrammarIndcutionMethod giMethod) {
		//
		// try {
		// consoleLogger
		// .info("trainData classes: "
		// + trainData.size()
		// + ", series length: "
		// + trainData.entrySet().iterator().next().getValue()
		// .get(0).length);
		// for (Entry<String, List<double[]>> e : trainData.entrySet()) {
		// consoleLogger.info(" training class: " + e.getKey()
		// + " series: " + e.getValue().size());
		// }
		// consoleLogger
		// .info("testData classes: "
		// + testData.size()
		// + ", series length: "
		// + testData.entrySet().iterator().next().getValue()
		// .get(0).length);
		// for (Entry<String, List<double[]>> e : testData.entrySet()) {
		// consoleLogger.info(" test class: " + e.getKey() + " series: "
		// + e.getValue().size());
		// }
		//
		// // 1. Concatenating training time series.
		// HashMap<String, double[]> concatenatedData = DataProcessor
		// .concatenateTrain(trainData);
		//
		// // DataProcessor.writeConcatenatedData(concatenatedData);
		//
		// ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);
		//
		// int clsNum = sequiturParams.length;
		// for (int i = 0; i < clsNum; i++) {
		//
		// consoleLogger.info("Classifying class " + (i + 1));
		// int[] paramClsI = sequiturParams[i];
		//
		// int windowSize = paramClsI[0];
		// int paaSize = paramClsI[1];
		// int alphabetSize = paramClsI[2];
		// int[][] params = new int[1][4];
		// params[0][0] = windowSize;
		// params[0][1] = paaSize;
		// params[0][2] = alphabetSize;
		// params[0][3] = paramClsI[3];
		//
		// String classLabel = String.valueOf(i + 1);
		//
		// // 2. Get patterns from concatenated time series.
		// int tsLen = trainData.entrySet().iterator().next().getValue()
		// .get(0).length;
		// HashMap<String, TSPatterns> allPatterns = DataProcessor
		// .getPatternsFromSequitur(concatenatedData, params,
		// tsLen, giMethod);
		//
		// // System.out.println("Selecting patterns...");
		// double patternRate = 0.5;
		// // 3. Train representative pattern for each class.
		// HashMap<String, TSPatterns> topFrequentPatterns =
		// selectTopFrequentPatterns(
		// allPatterns, patternRate);
		// if (topFrequentPatterns != null) {
		// HashMap<String, TSPatterns> representativePatterns =
		// selectBestFromRNN(
		// topFrequentPatterns, 3, forRNNData);
		//
		// // TODO:
		// // DataProcessor.writeTopPatterns(representativePatterns, i,
		// // "FaceAll");
		// // System.out.println("Selecting patterns done!");
		//
		// // Do Classification.
		// // System.out.println("Classification testing data...");
		// classifyTestDataByClass(representativePatterns,
		// allTestData, classLabel);
		//
		// } else {
		// System.err.println("No enough patterns...");
		// System.exit(-10);
		// }
		//
		// }
		//
		// // classifyUnlabeledData(allTestData, clsNum);
		//
		// displayAccuracy(allTestData, testData);
		// System.out.println("Classification finished!");
		//
		// } catch (Exception e) {
		// System.err.println("There was parameters error....");
		// System.err.println(StackTrace.toString(e));
		// System.out.print(printHelp());
		// System.exit(-10);
		// }

	}

	//
	// public ArrayList<TimeSeriesTest> doClassifyNewMethodBest10(
	// ResultParams[] best10Params, String dataName,
	// Map<String, List<double[]>> trainData,
	// Map<String, List<double[]>> testData,
	// Map<String, List<double[]>> validateData,
	// Map<String, List<double[]>> forRNNData,
	// GrammarIndcutionMethod giMethod) {
	//
	// try {
	// consoleLogger
	// .info("trainData classes: "
	// + trainData.size()
	// + ", series length: "
	// + trainData.entrySet().iterator().next().getValue()
	// .get(0).length);
	// for (Entry<String, List<double[]>> e : trainData.entrySet()) {
	// consoleLogger.info(" training class: " + e.getKey()
	// + " series: " + e.getValue().size());
	// }
	// consoleLogger
	// .info("testData classes: "
	// + testData.size()
	// + ", series length: "
	// + testData.entrySet().iterator().next().getValue()
	// .get(0).length);
	// for (Entry<String, List<double[]>> e : testData.entrySet()) {
	// consoleLogger.info(" test class: " + e.getKey() + " series: "
	// + e.getValue().size());
	// }
	//
	// // 1. Concatenating training time series.
	// HashMap<String, double[]> concatenatedData = DataProcessor
	// .concatenateTrain(trainData);
	//
	// // DataProcessor.writeConcatenatedData(concatenatedData);
	//
	// ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);
	// // ArrayList<TimeSeriesTest> allTrainData =
	// // buildTestData(trainData);
	//
	// int clsNum = best10Params.length;
	// int tsLen = trainData.entrySet().iterator().next().getValue()
	// .get(0).length;
	//
	// // double[][] distsTrain = new double[clsNum][];
	// // double[][] distsTest = new double[clsNum][];
	//
	// for (int i = 0; i < clsNum; i++) {
	// consoleLogger.info("Classifying class " + (i + 1));
	// String classLabel = String.valueOf(i + 1);
	// // Parameters for each class, when class i have the best F1
	// // score.
	// ResultParams top10PramsClsI = best10Params[i];
	//
	// int pIdx = 0;
	// for (int[] paramClsI : top10PramsClsI.getPramList()) {
	//
	// int windowSize = paramClsI[0];
	// int paaSize = paramClsI[1];
	// int alphabetSize = paramClsI[2];
	// int[][] params = new int[1][4];
	// params[0][0] = windowSize;
	// params[0][1] = paaSize;
	// params[0][2] = alphabetSize;
	// params[0][3] = paramClsI[3];
	//
	// // 2. Get patterns from concatenated time series.
	// HashMap<String, TSPatterns> allPatterns = DataProcessor
	// .getPatternsFromSequitur(concatenatedData, params,
	// tsLen, giMethod);
	//
	// // System.out.println("Selecting patterns...");
	// double patternRate = 0.5;
	// // 3. Train representative pattern for each class.
	// HashMap<String, TSPatterns> topFrequentPatterns =
	// selectTopFrequentPatterns(
	// allPatterns, patternRate);
	//
	// if (topFrequentPatterns != null) {
	//
	// HashMap<String, List<TimeSeriesTrain>> trainDataPerClass = new
	// HashMap<String, List<TimeSeriesTrain>>();
	// for (Entry<String, List<double[]>> e : forRNNData
	// .entrySet()) {
	// String label = e.getKey();
	// List<double[]> tsesInClass = e.getValue();
	//
	// List<TimeSeriesTrain> tses = new ArrayList<TimeSeriesTrain>();
	//
	// int idx = 1;
	// for (double[] ts : tsesInClass) {
	// TimeSeriesTrain tsTrain = new TimeSeriesTrain(
	// label, ts, idx);
	// tses.add(tsTrain);
	// idx++;
	// }
	//
	// trainDataPerClass.put(label, tses);
	// }
	//
	// HashMap<String, TSPatterns> representativePatterns =
	// selectBestFromRNNTrain(
	// topFrequentPatterns, 3, trainDataPerClass);
	//
	// // TODO:
	// // Write patterns to file.
	// DataProcessor.writeTopPatternsBest10(
	// representativePatterns, i, pIdx, dataName);
	// pIdx++;
	// // System.out.println("Selecting patterns done!");
	//
	// // Do Classification.
	// // System.out.println("Classification testing data...");
	//
	// // double[] distToPITrain = transferData(
	// // representativePatterns, allTrainData,
	// // classLabel);
	// // distsTrain[i] = distToPITrain;
	// // double[] distToPITest = transferData(
	// // representativePatterns, allTestData, classLabel);
	// // distsTest[i] = distToPITest;
	//
	// classifyTestDataByClassBest10(representativePatterns,
	// allTestData, classLabel);
	//
	// } else {
	// System.err.println("No enough patterns...");
	// System.exit(-10);
	// }
	//
	// }
	// }
	//
	// // DataProcessor.writeTransferedData(distsTrain, "Train");
	// // DataProcessor.writeTransferedData(distsTest, "Test");
	//
	// // classifyUnlabeledData(allTestData, clsNum);
	//
	// displayAccuracyBest10(allTestData, testData);
	// // System.out.println("Classification finished!");
	//
	// return allTestData;
	//
	// } catch (Exception e) {
	// System.err.println("There was parameters error....");
	// System.err.println(StackTrace.toString(e));
	// System.out.print(printHelp());
	// System.exit(-10);
	// }
	// return null;
	//
	// }

	public ArrayList<TimeSeriesTest> doClassifyNewMethodBest10NotRead(
			TopKBestPatterns[] best10Params, String dataName,
			Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData,
			Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData,
			GrammarIndcutionMethod giMethod) {

		try {
			ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);

			int clsNum = best10Params.length;

			for (int i = 0; i < clsNum; i++) {
				consoleLogger.debug("Classifying Training, class " + (i + 1));
				String classLabel = String.valueOf(i + 1);
				// Parameters for each class, when class i have the best F1
				// score.
				TopKBestPatterns top10PramsClsI = best10Params[i];

				int pIdx = 0;
				for (BestCombination paramClsI : top10PramsClsI.getBestKComb()) {

					HashMap<String, TSPatterns> representativePatterns = paramClsI
							.getThisRPatterns();

					// if(representativePatterns==null){
					// continue;
					// }

					// TODO:
					// Write patterns to file.
					// DataProcessor.writeTopPatternsBest10(
					// representativePatterns, i, pIdx, dataName);
					// pIdx++;
					classifyTestDataByClassBest10(paramClsI,
							representativePatterns, allTestData, classLabel);

				}
			}

			consoleLogger.info("Classification Accuracy of Training data:");
			displayAccuracyBest10(allTestData, testData);

			return allTestData;

		} catch (Exception e) {
			System.err.println("There was parameters error....");
			System.err.println(StackTrace.toString(e));
			System.out.print(printHelp());
			System.exit(-10);
		}
		return null;

	}

	//
	// public ArrayList<TimeSeriesTest> doClassifyNewMethodBest10Second(
	// ResultParams[] best10Params, String dataName,
	// Map<String, List<double[]>> trainData,
	// Map<String, List<double[]>> testData,
	// Map<String, List<double[]>> validateData,
	// Map<String, List<double[]>> forRNNData,
	// GrammarIndcutionMethod giMethod,
	// ArrayList<ArrayList<String>> secondCLasses,
	// ArrayList<ResultParams[]> secondBests) {
	//
	// try {
	// consoleLogger.info("Classify second time");
	//
	// // 1. Concatenating training time series.
	// HashMap<String, double[]> concatenatedData = DataProcessor
	// .concatenateTrain(trainData);
	//
	// ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);
	// // ArrayList<TimeSeriesTest> allTrainData =
	// // buildTestData(trainData);
	//
	// int clsNum = best10Params.length;
	// int tsLen = trainData.entrySet().iterator().next().getValue()
	// .get(0).length;
	//
	// // double[][] distsTrain = new double[clsNum][];
	// // double[][] distsTest = new double[clsNum][];
	//
	// for (int i = 0; i < clsNum; i++) {
	// consoleLogger.info("Classifying class " + (i + 1));
	// String classLabel = String.valueOf(i + 1);
	// // Parameters for each class, when class i have the best F1
	// // score.
	// ResultParams top10PramsClsI = best10Params[i];
	//
	// int pIdx = 0;
	// for (int[] paramClsI : top10PramsClsI.getPramList()) {
	//
	// int windowSize = paramClsI[0];
	// int paaSize = paramClsI[1];
	// int alphabetSize = paramClsI[2];
	// int[][] params = new int[1][4];
	// params[0][0] = windowSize;
	// params[0][1] = paaSize;
	// params[0][2] = alphabetSize;
	// params[0][3] = paramClsI[3];
	//
	// // 2. Get patterns from concatenated time series.
	// HashMap<String, TSPatterns> allPatterns = DataProcessor
	// .getPatternsFromSequitur(concatenatedData, params,
	// tsLen, giMethod);
	//
	// // System.out.println("Selecting patterns...");
	// double patternRate = 0.5;
	// // 3. Train representative pattern for each class.
	// HashMap<String, TSPatterns> topFrequentPatterns =
	// selectTopFrequentPatterns(
	// allPatterns, patternRate);
	//
	// if (topFrequentPatterns != null) {
	//
	// HashMap<String, List<TimeSeriesTrain>> trainDataPerClass = new
	// HashMap<String, List<TimeSeriesTrain>>();
	// for (Entry<String, List<double[]>> e : forRNNData
	// .entrySet()) {
	// String label = e.getKey();
	// List<double[]> tsesInClass = e.getValue();
	//
	// List<TimeSeriesTrain> tses = new ArrayList<TimeSeriesTrain>();
	//
	// int idx = 1;
	// for (double[] ts : tsesInClass) {
	// TimeSeriesTrain tsTrain = new TimeSeriesTrain(
	// label, ts, idx);
	// tses.add(tsTrain);
	// idx++;
	// }
	//
	// trainDataPerClass.put(label, tses);
	// }
	//
	// HashMap<String, TSPatterns> representativePatterns =
	// selectBestFromRNNTrain(
	// topFrequentPatterns, 3, trainDataPerClass);
	//
	// // TODO:
	// // Write patterns to file.
	// DataProcessor.writeTopPatternsBest10(
	// representativePatterns, i, pIdx, dataName);
	// pIdx++;
	// classifyTestDataByClassBest10(representativePatterns,
	// allTestData, classLabel);
	//
	// } else {
	// System.err.println("No enough patterns...");
	// System.exit(-10);
	// }
	//
	// }
	// }
	//
	// displayAccuracyBest10(allTestData, testData);
	//
	// for (int i = 0; i < secondCLasses.size(); i++) {
	// ArrayList<String> secondCs = secondCLasses.get(i);
	// ResultParams[] sBest = secondBests.get(i);
	//
	// Map<String, List<double[]>> trainDataSecond = new HashMap<String,
	// List<double[]>>();
	//
	// Map<String, String> clsLabelConvertTable = new HashMap<String, String>();
	// Map<String, String> reverseClsLabelConvertTable = new HashMap<String,
	// String>();
	//
	// System.out.print("{");
	// for (int idxc = 1; idxc <= secondCs.size(); idxc++) {
	// String c = secondCs.get(idxc - 1);
	// System.out.print(c + ", ");
	// trainDataSecond.put(String.valueOf(idxc), trainData.get(c));
	// clsLabelConvertTable.put(String.valueOf(idxc), c);
	// reverseClsLabelConvertTable.put(c, String.valueOf(idxc));
	// }
	// System.out.println("}");
	//
	// Map<String, List<TimeSeriesTest>> testDataSecond = reverseBuildTestData(
	// allTestData, secondCs, reverseClsLabelConvertTable);
	//
	// ArrayList<TimeSeriesTest> tempRlt =
	// doClassifyNewMethodBest10SecondClassify(
	// sBest, trainDataSecond, testDataSecond,
	// trainDataSecond, giMethod);
	//
	// int c1 = 0;
	// int c2 = 0;
	// int c3 = 0;
	//
	// for (TimeSeriesTest tst : tempRlt) {
	// String tempLabel = tst.getAssignedLabel();
	// String assignedLabel = tempLabel;
	//
	// if (assignedLabel.equals("1"))
	// c1++;
	// if (assignedLabel.equals("2"))
	// c2++;
	// if (assignedLabel.equals("3"))
	// c3++;
	//
	// if (clsLabelConvertTable.containsKey(tempLabel)) {
	// assignedLabel = clsLabelConvertTable.get(tempLabel);
	// }
	//
	// // String tempTrueLabel = tst.getTrueLable();
	// // String trueLabel = tempTrueLabel;
	// // if (clsLabelConvertTable.containsKey(tempTrueLabel)) {
	// // trueLabel = clsLabelConvertTable.get(tempTrueLabel);
	// // }
	//
	// tst.setAssignedLabel(assignedLabel);
	// // tst.setTrueLable(trueLabel);
	// }
	//
	// allTestData = updateClassificationRlt(allTestData, tempRlt,
	// clsNum, secondCs);
	//
	// }
	//
	// // DataProcessor.writeTransferedData(distsTrain, "Train");
	// // DataProcessor.writeTransferedData(distsTest, "Test");
	//
	// // classifyUnlabeledData(allTestData, clsNum);
	//
	// displayAccuracyBest10(allTestData, testData);
	// // System.out.println("Classification finished!");
	//
	// return allTestData;
	//
	// } catch (Exception e) {
	// System.err.println("There was parameters error....");
	// System.err.println(StackTrace.toString(e));
	// System.out.print(printHelp());
	// System.exit(-10);
	// }
	// return null;
	//
	// }

	public ArrayList<TimeSeriesTest> doClassifyNewMethodBest10SecondNotRead(
			TopKBestPatterns[] best10Params, String dataName,
			Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData,
			Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData,
			GrammarIndcutionMethod giMethod,
			ArrayList<ArrayList<String>> secondCLasses,
			ArrayList<TopKBestPatterns[]> secondBests) {

		try {
			consoleLogger.debug("Classify second time");

			ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);
			int clsNum = best10Params.length;

			for (int i = 0; i < clsNum; i++) {
				consoleLogger.debug("Classifying class " + (i + 1));
				String classLabel = String.valueOf(i + 1);
				// Parameters for each class, when class i have the best F1
				// score.
				TopKBestPatterns top10PramsClsI = best10Params[i];

				int pIdx = 0;
				for (BestCombination paramClsI : top10PramsClsI.getBestKComb()) {

					HashMap<String, TSPatterns> representativePatterns = paramClsI
							.getThisRPatterns();

					// TODO:
					// Write patterns to file.
					DataProcessor.writeTopPatternsBest10(
							representativePatterns, i, pIdx, dataName);
					pIdx++;
					classifyTestDataByClassBest10(paramClsI,
							representativePatterns, allTestData, classLabel);

				}
			}

			consoleLogger
					.info("Classification Accuracy without second optimization:");
			displayAccuracyBest10(allTestData, testData);

			if (secondCLasses.size() > 0) {

				for (int i = 0; i < secondCLasses.size(); i++) {
					ArrayList<String> secondCs = secondCLasses.get(i);
					TopKBestPatterns[] sBest = secondBests.get(i);

					Map<String, List<double[]>> trainDataSecond = new HashMap<String, List<double[]>>();

					Map<String, String> clsLabelConvertTable = new HashMap<String, String>();
					Map<String, String> reverseClsLabelConvertTable = new HashMap<String, String>();

					System.out.print("{");
					for (int idxc = 1; idxc <= secondCs.size(); idxc++) {
						String c = secondCs.get(idxc - 1);
						System.out.print(c + ", ");
						trainDataSecond.put(String.valueOf(idxc),
								trainData.get(c));
						clsLabelConvertTable.put(String.valueOf(idxc), c);
						reverseClsLabelConvertTable
								.put(c, String.valueOf(idxc));
					}
					System.out.println("}");

					Map<String, List<TimeSeriesTest>> testDataSecond = reverseBuildTestData(
							allTestData, secondCs, reverseClsLabelConvertTable);

					ArrayList<TimeSeriesTest> tempRlt = doClassifyNewMethodBest10SecondClassifyNotRead(
							sBest, trainDataSecond, testDataSecond,
							trainDataSecond, giMethod);

					for (TimeSeriesTest tst : tempRlt) {
						String tempLabel = tst.getAssignedLabel();
						String assignedLabel = tempLabel;

						if (clsLabelConvertTable.containsKey(tempLabel)) {
							assignedLabel = clsLabelConvertTable.get(tempLabel);
						}

						tst.setAssignedLabel(assignedLabel);
					}

					allTestData = updateClassificationRlt(allTestData, tempRlt,
							clsNum, secondCs);

				}

				// DataProcessor.writeTransferedData(distsTrain, "Train");
				// DataProcessor.writeTransferedData(distsTest, "Test");

				// classifyUnlabeledData(allTestData, clsNum);

				consoleLogger
						.info("Classification Accuracy of second optimization:");
				displayAccuracyBest10(allTestData, testData);
				// System.out.println("Classification finished!");
			}
			return allTestData;

		} catch (Exception e) {
			System.err.println("There was parameters error....");
			System.err.println(StackTrace.toString(e));
			System.out.print(printHelp());
			System.exit(-10);
		}
		return null;

	}

	public void doClassifyTest(Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData) {

		consoleLogger.debug("Classify second time");

		// ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);

		double[][] transformedTrainTS = transformTSWithPatternsTestTTT(trainData);

		double[][] transformedTestTS = transformTSWithPatternsTestTTT(testData);

		double error = classifyTransformedData(transformedTrainTS,
				transformedTestTS);

		consoleLogger.info("Classification Accuracy: " + String.valueOf(error));

	}

	public double[][] transformTSWithPatternsTestTTT(
			Map<String, List<double[]>> dataset) {
		int tsNum = 0;
		for (Entry<String, List<double[]>> edata : dataset.entrySet()) {
			tsNum += edata.getValue().size();
		}

		double[][] transformedTS = new double[tsNum][];

		int idxTs = 0;
		for (Entry<String, List<double[]>> eData : dataset.entrySet()) {
			String clsLabel = eData.getKey();
			for (double[] tsInstance : eData.getValue()) {

				double[] tsWithLabel = new double[tsInstance.length + 1];
				System.arraycopy(tsInstance, 0, tsWithLabel, 0,
						tsInstance.length);
				tsWithLabel[tsInstance.length] = Integer.parseInt(clsLabel);
				transformedTS[idxTs] = tsWithLabel;
				idxTs++;
			}
		}
		return transformedTS;
	}

	public void doClassifyTransformedMultiCls(
			BestSelectedPatterns[] bestSelectedPatternsAllCls, String dataName,
			Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData,
			GrammarIndcutionMethod giMethod) {

		consoleLogger.debug("Classify second time");

		// ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);
		TSPattern[] finalPatterns = combinePatterns(bestSelectedPatternsAllCls);

		double[][] transformedTrainTS = transformTSWithPatternsTest(
				finalPatterns, trainData);

		double[][] transformedTestTS = transformTSWithPatternsTest(
				finalPatterns, testData);

		double error = classifyTransformedData(transformedTrainTS,
				transformedTestTS);

		consoleLogger.info("Classification Accuracy: " + String.valueOf(error));

	}

	public TSPattern[] combinePatterns(
			BestSelectedPatterns[] bestSelectedPatternsAllCls) {

		ArrayList<TSPattern> finalPatterns = new ArrayList<TSPattern>();
		ArrayList<int[]> existParams = new ArrayList<int[]>();

		for (int i = 0; i < bestSelectedPatternsAllCls.length; i++) {
			BestSelectedPatterns bsp = bestSelectedPatternsAllCls[i];
			int[] params = bsp.getBestParams();
			TSPattern[] patterns = bsp.getBestSelectedPatterns();

			if (existParams.size() < 1) {
				Collections.addAll(finalPatterns, patterns);
				existParams.add(params);
			} else {
				boolean exist = false;
				for (int[] eparams : existParams) {
					if (Arrays.equals(params, eparams)) {
						exist = true;
					}
				}
				if (!exist) {
					Collections.addAll(finalPatterns, patterns);
					existParams.add(params);
				}

			}
		}
		TSPattern[] rlt = new TSPattern[finalPatterns.size()];
		for (int i = 0; i < finalPatterns.size(); i++) {
			rlt[i] = finalPatterns.get(i);
		}
		return rlt;
	}

	public void doClassifyTransformed(
			BestSelectedPatterns bestSelectedPatterns, String dataName,
			Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData,
			Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData,
			GrammarIndcutionMethod giMethod) {

		consoleLogger.debug("Classify second time");

		// ArrayList<TimeSeriesTest> allTestData = buildTestData(testData);

		double[][] transformedTrainTS = transformTSWithPatternsTest(
				bestSelectedPatterns.getBestSelectedPatterns(), trainData);

		double[][] transformedTestTS = transformTSWithPatternsTest(
				bestSelectedPatterns.getBestSelectedPatterns(), testData);

		double error = classifyTransformedData(transformedTrainTS,
				transformedTestTS);

		consoleLogger.info("Classification Accuracy: " + String.valueOf(error));

	}

	private ArrayList<TimeSeriesTest> updateClassificationRlt(
			ArrayList<TimeSeriesTest> allTestData,
			ArrayList<TimeSeriesTest> tempRlt, int clsNum,
			ArrayList<String> secondCs) {

		Map<String, ArrayList<TimeSeriesTest>> allTestDataSecond = new HashMap<String, ArrayList<TimeSeriesTest>>();

		for (int idxc = 1; idxc <= clsNum; idxc++) {
			allTestDataSecond.put(String.valueOf(idxc),
					new ArrayList<TimeSeriesTest>());
		}

		for (TimeSeriesTest tst : allTestData) {
			String label = tst.getAssignedLabel();

			if (allTestDataSecond.containsKey(label)) {
				allTestDataSecond.get(label).add(tst);
			}
		}

		for (String c : secondCs) {
			allTestDataSecond.remove(c);
		}

		ArrayList<TimeSeriesTest> newAllTestData = new ArrayList<TimeSeriesTest>();

		for (Entry<String, ArrayList<TimeSeriesTest>> e : allTestDataSecond
				.entrySet()) {
			for (TimeSeriesTest tst : e.getValue()) {
				newAllTestData.add(tst);
			}
		}
		newAllTestData.addAll(tempRlt);

		return newAllTestData;
	}

	private Map<String, List<TimeSeriesTest>> reverseBuildTestData(
			ArrayList<TimeSeriesTest> allTestData, ArrayList<String> secondCs,
			Map<String, String> reverseClsLabelConvertTable) {

		Map<String, List<TimeSeriesTest>> testDataSecond = new HashMap<String, List<TimeSeriesTest>>();

		for (int idxc = 1; idxc <= secondCs.size(); idxc++) {
			testDataSecond.put(String.valueOf(idxc),
					new ArrayList<TimeSeriesTest>());
		}

		for (TimeSeriesTest tst : allTestData) {
			String label = reverseClsLabelConvertTable.get(tst
					.getAssignedLabel());

			if (testDataSecond.containsKey(label)) {
				tst.setAssignedByClass(false);
				tst.setAssignedLabel(null);
				testDataSecond.get(label).add(tst);
			}
		}

		return testDataSecond;
	}

	//
	// public ArrayList<TimeSeriesTest> doClassifyNewMethodBest10SecondClassify(
	// ResultParams[] best10Params, Map<String, List<double[]>> trainData,
	// Map<String, List<TimeSeriesTest>> testData,
	// Map<String, List<double[]>> forRNNData,
	// GrammarIndcutionMethod giMethod) {
	//
	// try {
	// // 1. Concatenating training time series.
	// HashMap<String, double[]> concatenatedData = DataProcessor
	// .concatenateTrain(trainData);
	//
	// ArrayList<TimeSeriesTest> allTestData = buildTestDataSecond(testData);
	//
	// int clsNum = best10Params.length;
	// int tsLen = trainData.entrySet().iterator().next().getValue()
	// .get(0).length;
	//
	// for (int i = 0; i < clsNum; i++) {
	// consoleLogger.info("Classifying class " + (i + 1));
	// String classLabel = String.valueOf(i + 1);
	// // Parameters for each class, when class i have the best F1
	// // score.
	// ResultParams top10PramsClsI = best10Params[i];
	//
	// int pIdx = 0;
	// for (int[] paramClsI : top10PramsClsI.getPramList()) {
	//
	// int windowSize = paramClsI[0];
	// int paaSize = paramClsI[1];
	// int alphabetSize = paramClsI[2];
	// int[][] params = new int[1][4];
	// params[0][0] = windowSize;
	// params[0][1] = paaSize;
	// params[0][2] = alphabetSize;
	// params[0][3] = paramClsI[3];
	//
	// // 2. Get patterns from concatenated time series.
	// HashMap<String, TSPatterns> allPatterns = DataProcessor
	// .getPatternsFromSequitur(concatenatedData, params,
	// tsLen, giMethod);
	//
	// // System.out.println("Selecting patterns...");
	// double patternRate = 0.5;
	// // 3. Train representative pattern for each class.
	// HashMap<String, TSPatterns> topFrequentPatterns =
	// selectTopFrequentPatterns(
	// allPatterns, patternRate);
	//
	// if (topFrequentPatterns != null) {
	//
	// HashMap<String, List<TimeSeriesTrain>> trainDataPerClass = new
	// HashMap<String, List<TimeSeriesTrain>>();
	// for (Entry<String, List<double[]>> e : forRNNData
	// .entrySet()) {
	// String label = e.getKey();
	// List<double[]> tsesInClass = e.getValue();
	//
	// List<TimeSeriesTrain> tses = new ArrayList<TimeSeriesTrain>();
	//
	// int idx = 1;
	// for (double[] ts : tsesInClass) {
	// TimeSeriesTrain tsTrain = new TimeSeriesTrain(
	// label, ts, idx);
	// tses.add(tsTrain);
	// idx++;
	// }
	//
	// trainDataPerClass.put(label, tses);
	// }
	//
	// HashMap<String, TSPatterns> representativePatterns =
	// selectBestFromRNNTrain(
	// topFrequentPatterns, 3, trainDataPerClass);
	//
	// pIdx++;
	// classifyTestDataByClassBest10(representativePatterns,
	// allTestData, classLabel);
	//
	// } else {
	// System.err.println("No enough patterns...");
	// System.exit(-10);
	// }
	//
	// }
	// }
	//
	// return allTestData;
	//
	// } catch (Exception e) {
	// System.err.println("There was parameters error....");
	// System.err.println(StackTrace.toString(e));
	// System.out.print(printHelp());
	// System.exit(-10);
	// }
	// return null;
	//
	// }

	public ArrayList<TimeSeriesTest> doClassifyNewMethodBest10SecondClassifyNotRead(
			TopKBestPatterns[] best10Params,
			Map<String, List<double[]>> trainData,
			Map<String, List<TimeSeriesTest>> testData,
			Map<String, List<double[]>> forRNNData,
			GrammarIndcutionMethod giMethod) {

		try {

			ArrayList<TimeSeriesTest> allTestData = buildTestDataSecond(testData);

			int clsNum = best10Params.length;
			for (int i = 0; i < clsNum; i++) {
				consoleLogger.debug("Classifying class " + (i + 1));
				String classLabel = String.valueOf(i + 1);
				// Parameters for each class, when class i have the best F1
				// score.
				TopKBestPatterns top10PramsClsI = best10Params[i];

				for (BestCombination paramClsI : top10PramsClsI.getBestKComb()) {

					HashMap<String, TSPatterns> representativePatterns = paramClsI
							.getThisRPatterns();

					classifyTestDataByClassBest10(paramClsI,
							representativePatterns, allTestData, classLabel);

				}
			}

			return allTestData;

		} catch (Exception e) {
			System.err.println("There was parameters error....");
			System.err.println(StackTrace.toString(e));
			System.out.print(printHelp());
			System.exit(-10);
		}
		return null;

	}

	public void doClassifyNewMethodBest10Shifted(ResultParams[] best10Params,
			String dataName, Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData,
			Map<String, List<double[]>> shiftedData,
			GrammarIndcutionMethod giMethod) {

		try {
			consoleLogger
					.info("trainData classes: "
							+ trainData.size()
							+ ", series length: "
							+ trainData.entrySet().iterator().next().getValue()
									.get(0).length);
			for (Entry<String, List<double[]>> e : trainData.entrySet()) {
				consoleLogger.debug(" training class: " + e.getKey()
						+ " series: " + e.getValue().size());
			}
			consoleLogger.debug("shiftedData classes: "
					+ shiftedData.size()
					+ ", series length: "
					+ shiftedData.entrySet().iterator().next().getValue()
							.get(0).length);
			for (Entry<String, List<double[]>> e : shiftedData.entrySet()) {
				consoleLogger.debug(" shiftedData class: " + e.getKey()
						+ " series: " + e.getValue().size());
			}

			HashMap<String, int[]> allStartPositions = new HashMap<String, int[]>();
			// 1. Concatenating training time series.
			HashMap<String, double[]> concatenatedData = DataProcessor
					.concatenateTrain(trainData, allStartPositions);

			ArrayList<TimeSeriesTestShifted> allShiftedTestData = buildTestDataShifted(shiftedData);

			int clsNum = best10Params.length;
			int tsLen = trainData.entrySet().iterator().next().getValue()
					.get(0).length;

			for (int i = 0; i < clsNum; i++) {
				consoleLogger.debug("Classifying class " + (i + 1));
				String classLabel = String.valueOf(i + 1);
				ResultParams top10PramsClsI = best10Params[i];

				int pIdx = 0;
				for (int[] paramClsI : top10PramsClsI.getPramList()) {

					int windowSize = paramClsI[0];
					int paaSize = paramClsI[1];
					int alphabetSize = paramClsI[2];
					int[][] params = new int[1][4];
					params[0][0] = windowSize;
					params[0][1] = paaSize;
					params[0][2] = alphabetSize;
					params[0][3] = paramClsI[3];

					// 2. Get patterns from concatenated time series.
					HashMap<String, TSPatterns> allPatterns = DataProcessor
							.getPatternsFromSequitur(concatenatedData, params,
									giMethod, allStartPositions);

					// System.out.println("Selecting patterns...");
					double patternRate = 0.5;
					// 3. Train representative pattern for each class.
					HashMap<String, TSPatterns> topFrequentPatterns = selectTopFrequentPatterns(
							allPatterns, patternRate);

					if (topFrequentPatterns != null) {

						HashMap<String, List<TimeSeriesTrain>> trainDataPerClass = new HashMap<String, List<TimeSeriesTrain>>();
						for (Entry<String, List<double[]>> e : forRNNData
								.entrySet()) {
							String label = e.getKey();
							List<double[]> tsesInClass = e.getValue();

							List<TimeSeriesTrain> tses = new ArrayList<TimeSeriesTrain>();

							int idx = 1;
							for (double[] ts : tsesInClass) {
								TimeSeriesTrain tsTrain = new TimeSeriesTrain(
										label, ts, idx);
								tses.add(tsTrain);
								idx++;
							}

							trainDataPerClass.put(label, tses);
						}

						HashMap<String, TSPatterns> representativePatterns = selectBestFromRNNTrain(
								topFrequentPatterns, 3, trainDataPerClass);

						// TODO:
						// DataProcessor.writeTopPatternsBest10(
						// representativePatterns, i, pIdx, dataName);
						pIdx++;

						// Do Classification.
						// System.out.println("Classification testing data...");
						classifyTestDataByClassBest10Shifted(
								representativePatterns, allShiftedTestData,
								classLabel);

					} else {
						System.err.println("No enough patterns...");
						System.exit(-10);
					}

				}

			}

			displayAccuracyBest10Shifted(allShiftedTestData, shiftedData);
			System.out.println("Classification finished!");

		} catch (Exception e) {
			System.err.println("There was parameters error....");
			System.err.println(StackTrace.toString(e));
			System.out.print(printHelp());
			System.exit(-10);
		}

	}

	public void doClassifyNewMethodShifted(int[][] sequiturParams,
			Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData,
			Map<String, List<double[]>> shiftedData,
			GrammarIndcutionMethod giMethod) {

		// try {
		// consoleLogger
		// .info("trainData classes: "
		// + trainData.size()
		// + ", series length: "
		// + trainData.entrySet().iterator().next().getValue()
		// .get(0).length);
		// for (Entry<String, List<double[]>> e : trainData.entrySet()) {
		// consoleLogger.info(" training class: " + e.getKey()
		// + " series: " + e.getValue().size());
		// }
		// consoleLogger.info("shiftedData classes: "
		// + shiftedData.size()
		// + ", series length: "
		// + shiftedData.entrySet().iterator().next().getValue()
		// .get(0).length);
		// for (Entry<String, List<double[]>> e : shiftedData.entrySet()) {
		// consoleLogger.info(" shiftedData class: " + e.getKey()
		// + " series: " + e.getValue().size());
		// }
		//
		// // 1. Concatenating training time series.
		// HashMap<String, double[]> concatenatedData = DataProcessor
		// .concatenateTrain(trainData);
		//
		// ArrayList<TimeSeriesTestShifted> allShiftedTestData =
		// buildTestDataShifted(shiftedData);
		//
		// for (int i = 0; i < sequiturParams.length; i++) {
		// int[] paramClsI = sequiturParams[i];
		//
		// int windowSize = paramClsI[0];
		// int paaSize = paramClsI[1];
		// int alphabetSize = paramClsI[2];
		// int[][] params = new int[1][4];
		// params[0][0] = windowSize;
		// params[0][1] = paaSize;
		// params[0][2] = alphabetSize;
		// params[0][3] = paramClsI[3];
		//
		// String classLabel = String.valueOf(i + 1);
		//
		// consoleLogger.info("Classifying class " + classLabel);
		//
		// // 2. Get patterns from concatenated time series.
		// int tsLen = trainData.entrySet().iterator().next().getValue()
		// .get(0).length;
		// HashMap<String, TSPatterns> allPatterns = DataProcessor
		// .getPatternsFromSequitur(concatenatedData, params,
		// tsLen, giMethod);
		//
		// double patternRate = 0.5;
		// // 3. Train representative pattern for each class.
		// HashMap<String, TSPatterns> topFrequentPatterns =
		// selectTopFrequentPatterns(
		// allPatterns, patternRate);
		// if (topFrequentPatterns != null) {
		// HashMap<String, TSPatterns> representativePatterns =
		// selectBestFromRNN(
		// topFrequentPatterns, 3, forRNNData);
		//
		// // Do Classification.
		// classifyTestDataByClassShifted(representativePatterns,
		// allShiftedTestData, classLabel);
		//
		// } else {
		// System.err.println("No enough patterns...");
		// System.exit(-10);
		// }
		//
		// }
		//
		// displayAccuracyShifted(allShiftedTestData, shiftedData);
		// System.out.println("Classification finished!");
		//
		// } catch (Exception e) {
		// System.err.println("There was parameters error....");
		// System.err.println(StackTrace.toString(e));
		// System.out.print(printHelp());
		// System.exit(-10);
		// }

	}

	private void classifyUnlabeledData(ArrayList<TimeSeriesTest> allTestData,
			int clsNum) {

		int[] count = new int[clsNum];
		for (TimeSeriesTest testTS : allTestData) {
			if (!testTS.getAssignedByClass()) {
				for (String label : testTS.getLabelByOther()) {
					int l = Integer.parseInt(label) - 1;
					count[l] += 1;
				}

				int topNum = 0;
				int idx = 0;
				for (int i = 0; i < clsNum; i++) {
					int c = count[i];
					if (topNum < c) {
						topNum = c;
						idx = i;
					}
				}
				testTS.setAssignedLabel(String.valueOf(idx + 1));
			}
		}

	}

	private void classifyTestDataByClassShifted(
			HashMap<String, TSPatterns> representativePatterns,
			ArrayList<TimeSeriesTestShifted> allTestData, String forClass) {

		int k = 1;
		for (TimeSeriesTestShifted testTS : allTestData) {
			knnClassifyShifted(testTS, representativePatterns, k, forClass);
		}

	}

	public void knnClassifyShifted(TimeSeriesTestShifted tst,
			HashMap<String, TSPatterns> representativePatterns, int k,
			String forClass) {

		double[] ts = tst.getValues();
		double[] ts2 = tst.getRebuildedValues();

		String assignedLabel = "";
		double minDist = INF;

		for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			int patternSize = patternsInClass.getPatterns().size();

			double totalDist = 0;
			double totalDist2 = 0;
			for (TSPattern pattern : patternsInClass.getPatterns()) {
				double dist = calcDist(ts, pattern.getPatternTS());
				double dist2 = calcDist(ts2, pattern.getPatternTS());

				totalDist += dist;
				totalDist2 += dist2;

				// if(minDist > dist){
				// minDist = dist;
				// assignedLabel = classLabel;
				// }
			}

			double meanDist = totalDist / patternSize;
			double meanDist2 = totalDist2 / patternSize;

			double minOfTwo = meanDist;
			if (minOfTwo > meanDist2) {
				minOfTwo = meanDist2;
			}

			if (minOfTwo < minDist) {
				assignedLabel = classLabel;
				minDist = minOfTwo;
			}
		}

		if (assignedLabel.equalsIgnoreCase(forClass)) {
			if (tst.getAssignedByClass()) {
				if (tst.getDistToNN() > minDist) {
					// if(tst.getLenNN() < meanLenNN){
					tst.setAssignedLabel(assignedLabel);
					tst.setDistToNN(minDist);
					tst.setAssignedByClass(true);
				}
			} else {
				tst.setAssignedLabel(assignedLabel);
				tst.setDistToNN(minDist);
				tst.setAssignedByClass(true);
			}
		} else {
			if (!tst.getAssignedByClass()) {

				// tst.getLabelByOther().add(assignedLabel);

				if (tst.getAssignedLabel() == null) {
					tst.setAssignedLabel(assignedLabel);
					tst.setDistToNN(minDist);
				} else {
					if (tst.getDistToNN() > minDist) {
						tst.setAssignedLabel(assignedLabel);
						tst.setDistToNN(minDist);
					}
				}
			}
		}
	}

	private ArrayList<TimeSeriesTestShifted> buildTestDataShifted(
			Map<String, List<double[]>> testData) {

		ArrayList<TimeSeriesTestShifted> allTestData = new ArrayList<TimeSeriesTestShifted>();

		for (Entry<String, List<double[]>> e : testData.entrySet()) {
			String classLabel = e.getKey();
			List<double[]> testingTSses = e.getValue();

			for (double[] values : testingTSses) {
				int tsLen = values.length;
				int midPoint = tsLen / 2;
				double[] firstPart = Arrays
						.copyOfRange(values, midPoint, tsLen);
				double[] secondPart = Arrays.copyOfRange(values, 0, midPoint);
				double[] rebuildedValues = ArrayUtils.addAll(firstPart,
						secondPart);
				TimeSeriesTestShifted ts = new TimeSeriesTestShifted(
						classLabel, values, rebuildedValues);
				allTestData.add(ts);
			}

		}

		return allTestData;
	}

	private void displayAccuracyShifted(
			ArrayList<TimeSeriesTestShifted> allTestData,
			Map<String, List<double[]>> testData) {

		DecimalFormat df = new DecimalFormat("#.####");

		int totalCNum = 0;
		int totalTSNum = 0;

		Map<String, Integer> assignedResultNum = new HashMap<String, Integer>();
		int clsNum = testData.entrySet().size();
		int[][] missClassifiedNum = new int[clsNum][clsNum];

		Map<String, Integer> correctNumPerClass = new HashMap<String, Integer>();
		for (TimeSeriesTestShifted testTS : allTestData) {
			String trueLabel = testTS.getTrueLable();
			String assignedLabel = testTS.getAssignedLabel();

			if (assignedResultNum.containsKey(assignedLabel)) {
				assignedResultNum.put(assignedLabel,
						assignedResultNum.get(assignedLabel) + 1);
			} else {
				assignedResultNum.put(assignedLabel, 1);
			}

			if (trueLabel.equalsIgnoreCase(assignedLabel)) {
				if (correctNumPerClass.containsKey(trueLabel)) {
					correctNumPerClass.put(trueLabel,
							correctNumPerClass.get(trueLabel) + 1);
				} else {
					correctNumPerClass.put(trueLabel, 1);
				}
			} else {
				int trueCls = Integer.parseInt(trueLabel) - 1;
				if (assignedLabel == null) {
					missClassifiedNum[trueCls][trueCls] += 1;
				} else {
					int assginedCls = Integer.parseInt(assignedLabel) - 1;
					missClassifiedNum[trueCls][assginedCls] += 1;
				}
			}
		}

		int notClsNum = 0;
		for (Entry<String, Integer> correctInC : correctNumPerClass.entrySet()) {
			String classLabel = correctInC.getKey();

			int tsNum = testData.get(classLabel).size();
			int correctNum = correctInC.getValue();
			double acc = (double) correctNum / (double) tsNum;

			System.out.println("For class " + classLabel
					+ " , the classification accuracy is: " + correctNum + "/"
					+ tsNum + ", " + df.format(acc * 100) + "%");
			System.out.println(assignedResultNum.get(classLabel)
					+ " time series are classified as class " + classLabel);

			int clsL = Integer.parseInt(classLabel) - 1;
			int[] missNum = missClassifiedNum[clsL];
			for (int i = 0; i < missNum.length; i++) {
				if (missNum[i] > 0) {
					if (clsL == i) {
						System.out.println(" " + missNum[i]
								+ " time series in class " + classLabel
								+ " are not classified");
						notClsNum += missNum[i];
					} else {
						System.out.println(" " + missNum[i]
								+ " time series in class " + classLabel
								+ " are misclassified as class " + (i + 1));
					}

				}
			}

			totalCNum += correctNum;
			totalTSNum += tsNum;
		}

		System.out.println(notClsNum + " are not classified");

		double totalAcc = (double) totalCNum / (double) totalTSNum;
		System.out.println("Total classification accuracy is: " + totalCNum
				+ "/" + totalTSNum + ", " + df.format(totalAcc * 100) + "%");
		System.out.println("Total error rate is: " + df.format(1 - totalAcc));
	}

	private void displayAccuracy(ArrayList<TimeSeriesTest> allTestData,
			Map<String, List<double[]>> testData) {

		DecimalFormat df = new DecimalFormat("#.####");

		int totalCNum = 0;
		int totalTSNum = 0;

		Map<String, Integer> assignedResultNum = new HashMap<String, Integer>();
		int clsNum = testData.entrySet().size();
		int[][] missClassifiedNum = new int[clsNum][clsNum];

		Map<String, Integer> correctNumPerClass = new HashMap<String, Integer>();
		for (TimeSeriesTest testTS : allTestData) {
			String trueLabel = testTS.getTrueLable();
			String assignedLabel = testTS.getAssignedLabel();

			if (assignedResultNum.containsKey(assignedLabel)) {
				assignedResultNum.put(assignedLabel,
						assignedResultNum.get(assignedLabel) + 1);
			} else {
				assignedResultNum.put(assignedLabel, 1);
			}

			if (trueLabel.equalsIgnoreCase(assignedLabel)) {
				if (correctNumPerClass.containsKey(trueLabel)) {
					correctNumPerClass.put(trueLabel,
							correctNumPerClass.get(trueLabel) + 1);
				} else {
					correctNumPerClass.put(trueLabel, 1);
				}
			} else {
				int trueCls = Integer.parseInt(trueLabel) - 1;
				if (assignedLabel == null) {
					missClassifiedNum[trueCls][trueCls] += 1;
				} else {
					int assginedCls = Integer.parseInt(assignedLabel) - 1;
					missClassifiedNum[trueCls][assginedCls] += 1;
				}
			}
		}

		int notClsNum = 0;
		for (int j = 0; j < clsNum; j++) {
			String classLabel = String.valueOf(j + 1);
			int tsNum = testData.get(classLabel).size();
			int correctNum = 0;
			if (correctNumPerClass.containsKey(classLabel)) {
				correctNum = correctNumPerClass.get(classLabel);
			}

			double acc = (double) correctNum / (double) tsNum;

			System.out.println("For class " + classLabel
					+ " , the classification accuracy is: " + correctNum + "/"
					+ tsNum + ", " + df.format(acc * 100) + "%");
			System.out.println(assignedResultNum.get(classLabel)
					+ " time series are classified as class " + classLabel);

			int clsL = Integer.parseInt(classLabel) - 1;
			int[] missNum = missClassifiedNum[clsL];
			for (int i = 0; i < missNum.length; i++) {
				if (missNum[i] > 0) {
					if (clsL == i) {
						System.out.println(" " + missNum[i]
								+ " time series in class " + classLabel
								+ " are not classified");
						notClsNum += missNum[i];
					} else {
						System.out.println(" " + missNum[i]
								+ " time series in class " + classLabel
								+ " are misclassified as class " + (i + 1));
					}

				}
			}

			totalCNum += correctNum;
			totalTSNum += tsNum;
		}

		System.out.println(notClsNum + " are not classified");

		double totalAcc = (double) totalCNum / (double) totalTSNum;
		System.out.println("Total classification accuracy is: " + totalCNum
				+ "/" + totalTSNum + ", " + df.format(totalAcc * 100) + "%");
		System.out.println("Total error rate is: " + df.format(1 - totalAcc));
	}

	private void displayAccuracyBest10(ArrayList<TimeSeriesTest> allTestData,
			Map<String, List<double[]>> testData) {

		DecimalFormat df = new DecimalFormat("#.####");

		int totalCNum = 0;
		int totalTSNum = 0;
		int notClsNum = 0;

		Map<String, Integer> assignedResultNum = new HashMap<String, Integer>();
		int clsNum = testData.entrySet().size();
		int[][] missClassifiedNum = new int[clsNum][clsNum];

		Map<String, Integer> correctNumPerClass = new HashMap<String, Integer>();
		for (TimeSeriesTest testTS : allTestData) {
			String trueLabel = testTS.getTrueLable();
			String assignedLabel = testTS.getAssignedLabel();
			if (!testTS.getAssignedByClass()) {
				// assignedLabel = findMostFrequentLabel(testTS);
				notClsNum++;
			}

			if (assignedResultNum.containsKey(assignedLabel)) {
				assignedResultNum.put(assignedLabel,
						assignedResultNum.get(assignedLabel) + 1);
			} else {
				assignedResultNum.put(assignedLabel, 1);
			}

			if (trueLabel.equalsIgnoreCase(assignedLabel)) {
				if (correctNumPerClass.containsKey(trueLabel)) {
					correctNumPerClass.put(trueLabel,
							correctNumPerClass.get(trueLabel) + 1);
				} else {
					correctNumPerClass.put(trueLabel, 1);
				}
			} else {
				int trueCls = Integer.parseInt(trueLabel) - 1;
				if (assignedLabel == null) {
					missClassifiedNum[trueCls][trueCls] += 1;
				} else {
					int assginedCls = Integer.parseInt(assignedLabel) - 1;
					missClassifiedNum[trueCls][assginedCls] += 1;
				}
			}
		}

		for (int j = 0; j < clsNum; j++) {
			String classLabel = String.valueOf(j + 1);
			int tsNum = testData.get(classLabel).size();
			int correctNum = 0;
			if (correctNumPerClass.containsKey(classLabel)) {
				correctNum = correctNumPerClass.get(classLabel);
			}

			double acc = (double) correctNum / (double) tsNum;

			consoleLogger.debug("For class " + classLabel
					+ " , the classification accuracy is: " + correctNum + "/"
					+ tsNum + ", " + df.format(acc * 100) + "%");
			consoleLogger.debug(assignedResultNum.get(classLabel)
					+ " time series are classified as class " + classLabel);

			int clsL = Integer.parseInt(classLabel) - 1;
			int[] missNum = missClassifiedNum[clsL];
			for (int i = 0; i < missNum.length; i++) {
				if (missNum[i] > 0) {
					if (clsL == i) {
						consoleLogger.debug(" " + missNum[i]
								+ " time series in class " + classLabel
								+ " are not classified");
						notClsNum += missNum[i];
					} else {
						consoleLogger.debug(" " + missNum[i]
								+ " time series in class " + classLabel
								+ " are misclassified as class " + (i + 1));
					}

				}
			}

			totalCNum += correctNum;
			totalTSNum += tsNum;
		}
		consoleLogger.debug(notClsNum + " are not classified");

		double totalAcc = (double) totalCNum / (double) totalTSNum;
		consoleLogger.debug("Total classification accuracy is: " + totalCNum
				+ "/" + totalTSNum + ", " + df.format(totalAcc * 100) + "%. "
				+ "Total error rate is: " + df.format(1 - totalAcc));
		consoleLogger.info("Total classification accuracy is: " + totalCNum
				+ "/" + totalTSNum + ", " + df.format(totalAcc * 100) + "%. "
				+ "Total error rate is: " + df.format(1 - totalAcc));
		// consoleLogger.info("Total error rate is: " + df.format(1 -
		// totalAcc));
	}

	private void displayAccuracyBest10Shifted(
			ArrayList<TimeSeriesTestShifted> allShiftedTestData,
			Map<String, List<double[]>> testData) {

		DecimalFormat df = new DecimalFormat("#.####");

		int totalCNum = 0;
		int totalTSNum = 0;
		int notClsNum = 0;

		Map<String, Integer> assignedResultNum = new HashMap<String, Integer>();
		int clsNum = testData.entrySet().size();
		int[][] missClassifiedNum = new int[clsNum][clsNum];

		Map<String, Integer> correctNumPerClass = new HashMap<String, Integer>();
		for (TimeSeriesTestShifted testTS : allShiftedTestData) {
			String trueLabel = testTS.getTrueLable();
			String assignedLabel = testTS.getAssignedLabel();
			if (!testTS.getAssignedByClass()) {
				notClsNum++;
				// assignedLabel = findMostFrequentLabelShifted(testTS);
			}

			if (assignedResultNum.containsKey(assignedLabel)) {
				assignedResultNum.put(assignedLabel,
						assignedResultNum.get(assignedLabel) + 1);
			} else {
				assignedResultNum.put(assignedLabel, 1);
			}

			if (trueLabel.equalsIgnoreCase(assignedLabel)) {
				if (correctNumPerClass.containsKey(trueLabel)) {
					correctNumPerClass.put(trueLabel,
							correctNumPerClass.get(trueLabel) + 1);
				} else {
					correctNumPerClass.put(trueLabel, 1);
				}
			} else {
				int trueCls = Integer.parseInt(trueLabel) - 1;
				if (assignedLabel == null) {
					missClassifiedNum[trueCls][trueCls] += 1;
				} else {
					int assginedCls = Integer.parseInt(assignedLabel) - 1;
					missClassifiedNum[trueCls][assginedCls] += 1;
				}
			}
		}

		for (int j = 0; j < clsNum; j++) {
			String classLabel = String.valueOf(j + 1);
			int tsNum = testData.get(classLabel).size();
			int correctNum = 0;
			if (correctNumPerClass.containsKey(classLabel)) {
				correctNum = correctNumPerClass.get(classLabel);
			}

			double acc = (double) correctNum / (double) tsNum;

			System.out.println("For class " + classLabel
					+ " , the classification accuracy is: " + correctNum + "/"
					+ tsNum + ", " + df.format(acc * 100) + "%");
			System.out.println(assignedResultNum.get(classLabel)
					+ " time series are classified as class " + classLabel);

			int clsL = Integer.parseInt(classLabel) - 1;
			int[] missNum = missClassifiedNum[clsL];
			for (int i = 0; i < missNum.length; i++) {
				if (missNum[i] > 0) {
					if (clsL == i) {
						System.out.println(" " + missNum[i]
								+ " time series in class " + classLabel
								+ " are not classified");
						notClsNum += missNum[i];
					} else {
						System.out.println(" " + missNum[i]
								+ " time series in class " + classLabel
								+ " are misclassified as class " + (i + 1));
					}

				}
			}

			totalCNum += correctNum;
			totalTSNum += tsNum;
		}

		System.out.println(notClsNum + " are not classified");

		double totalAcc = (double) totalCNum / (double) totalTSNum;
		System.out.println("Total classification accuracy is: " + totalCNum
				+ "/" + totalTSNum + ", " + df.format(totalAcc * 100) + "%");
		System.out.println("Total error rate is: " + df.format(1 - totalAcc));
	}

	private String findMostFrequentLabel(TimeSeriesTest testTS) {
		HashMap<String, Integer> assignedLabels = (HashMap<String, Integer>) testTS
				.getAssignedLabels();

		String label = "";
		int max = 0;
		for (Entry<String, Integer> e : assignedLabels.entrySet()) {
			int count = e.getValue();
			if (max < count) {
				max = count;
				label = e.getKey();
			}
		}
		return label;
	}

	private String findMostFrequentLabelShifted(TimeSeriesTestShifted testTS) {
		HashMap<String, Integer> assignedLabels = (HashMap<String, Integer>) testTS
				.getAssignedLabels();

		String label = "";
		int max = 0;
		for (Entry<String, Integer> e : assignedLabels.entrySet()) {
			int count = e.getValue();
			if (max < count) {
				max = count;
				label = e.getKey();
			}
		}
		return label;
	}

	private ArrayList<TimeSeriesTest> buildTestData(
			Map<String, List<double[]>> testData) {

		ArrayList<TimeSeriesTest> allTestData = new ArrayList<TimeSeriesTest>();

		for (Entry<String, List<double[]>> e : testData.entrySet()) {
			String classLabel = e.getKey();
			List<double[]> testingTSses = e.getValue();

			for (double[] values : testingTSses) {
				TimeSeriesTest ts = new TimeSeriesTest(classLabel, values);
				allTestData.add(ts);
			}

		}

		return allTestData;
	}

	private ArrayList<TimeSeriesTest> buildTestDataSecond(
			Map<String, List<TimeSeriesTest>> testData) {

		ArrayList<TimeSeriesTest> allTestData = new ArrayList<TimeSeriesTest>();

		for (Entry<String, List<TimeSeriesTest>> e : testData.entrySet()) {
			String classLabel = e.getKey();
			List<TimeSeriesTest> testingTSses = e.getValue();

			for (TimeSeriesTest values : testingTSses) {
				allTestData.add(values);
			}

		}

		return allTestData;
	}

	private void classifyTestDataByClassBest10(BestCombination paramClsI,
			HashMap<String, TSPatterns> representativePatterns,
			ArrayList<TimeSeriesTest> allTestData, String forClass) {

		int k = 1;
		// int count = 1;
		for (TimeSeriesTest testTS : allTestData) {
			// knnClassifyMinOverlap(testTS, representativePatterns, k,
			// forClass);
			// System.out.println(count++);
			knnClassify(paramClsI, testTS, representativePatterns, k, forClass);
		}
		//
		// int count = 0;
		// for (TimeSeriesTest testTS : allTestData) {
		// if (testTS.getAssignedLabel() != null) {
		// if (testTS.getAssignedLabel().equals(testTS.getTrueLable())
		// && testTS.getAssignedLabel().equals("1"))
		// count++;
		// }
		// }
		// System.out.println(count);
	}

	private double[] transferData(
			HashMap<String, TSPatterns> representativePatterns,
			ArrayList<TimeSeriesTest> allTestData, String forClass) {

		double[] distToPatternI = new double[allTestData.size()];

		int k = 1;
		for (int i = 0; i < allTestData.size(); i++) {
			TimeSeriesTest testTS = allTestData.get(i);
			// knnClassifyMinOverlap(testTS, representativePatterns, k,
			// forClass);
			double distToPatterns = knnClassifyTransfer(testTS,
					representativePatterns, k, forClass);

			distToPatternI[i] = distToPatterns;
		}
		return distToPatternI;

	}

	private void classifyTestDataByClassBest10Shifted(
			HashMap<String, TSPatterns> representativePatterns,
			ArrayList<TimeSeriesTestShifted> allShiftedTestData, String forClass) {

		int k = 1;
		for (TimeSeriesTestShifted testTS : allShiftedTestData) {
			knnClassifyBest10Shifted(testTS, representativePatterns, k,
					forClass);
		}

	}

	public void knnClassifyBest10Shifted(TimeSeriesTestShifted tst,
			HashMap<String, TSPatterns> representativePatterns, int k,
			String forClass) {

		double[] ts = tst.getValues();
		double[] ts2 = tst.getRebuildedValues();

		String assignedLabel = "";
		double minDist = INF;

		for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			int patternSize = patternsInClass.getPatterns().size();

			double totalDist = 0;
			double totalDist2 = 0;
			for (TSPattern pattern : patternsInClass.getPatterns()) {
				double dist = calcDist(ts, pattern.getPatternTS());
				double dist2 = calcDist(ts2, pattern.getPatternTS());

				totalDist += dist;
				totalDist2 += dist2;

				// if(minDist > dist){
				// minDist = dist;
				// assignedLabel = classLabel;
				// }
			}

			double meanDist = totalDist / patternSize;
			double meanDist2 = totalDist2 / patternSize;

			double minOfTwo = meanDist;
			if (minOfTwo > meanDist2) {
				minOfTwo = meanDist2;
			}

			if (minOfTwo < minDist) {
				assignedLabel = classLabel;
				minDist = minOfTwo;
			}
		}

		if (assignedLabel.equalsIgnoreCase(forClass)) {
			if (tst.getAssignedByClass()) {
				if (tst.getDistToNN() > minDist) {
					// if(tst.getLenNN() < meanLenNN){
					tst.setAssignedLabel(assignedLabel);
					tst.setDistToNN(minDist);
					tst.setAssignedByClass(true);
				}
			} else {
				tst.setAssignedLabel(assignedLabel);
				tst.setDistToNN(minDist);
				tst.setAssignedByClass(true);
			}
		} else {
			if (!tst.getAssignedByClass()) {
				// TSPatterns patternsInThisClass = representativePatterns
				// .get(forClass);
				// // tst.getLabelByOther().add(assignedLabel);
				// double totalDist = 0;
				// int patternSize = patternsInThisClass.getPatterns().size();
				// double maxDist = INF;
				// for (TSPattern pattern : patternsInThisClass.getPatterns()) {
				//
				// double dist = calcDist(ts, pattern.getPatternTS());
				//
				// if (maxDist > dist)
				// maxDist = dist;
				//
				// totalDist += dist;
				// }
				//
				// double meanDist = totalDist / patternSize;
				// if (usingMaxDist)
				// meanDist = maxDist;
				//
				// if (tst.getAssignedLabel() == null) {
				// tst.setAssignedLabel(forClass);
				// tst.setDistToNN(meanDist);
				//
				// } else {
				// if (meanDist < tst.getDistToNN()) {
				// tst.setAssignedLabel(forClass);
				// tst.setDistToNN(meanDist);
				// }
				// }

				if (tst.getAssignedLabel() == null) {
					tst.setAssignedLabel(assignedLabel);
					tst.setDistToNN(minDist);
				} else {
					if (tst.getDistToNN() > minDist) {
						tst.setAssignedLabel(assignedLabel);
						tst.setDistToNN(minDist);
					}
				}

				if (tst.getAssignedLabels().containsKey(assignedLabel)) {
					int count = tst.getAssignedLabels().get(assignedLabel) + 1;
					tst.getAssignedLabels().put(assignedLabel, count);
				} else {
					tst.getAssignedLabels().put(assignedLabel, 1);
				}
			}
		}
	}

	// private void classifyTestDataByClass(
	// HashMap<String, TSPatterns> representativePatterns,
	// ArrayList<TimeSeriesTest> allTestData, String forClass) {
	//
	// int k = 1;
	// for (TimeSeriesTest testTS : allTestData) {
	// knnClassify(testTS, representativePatterns, k, forClass);
	// }
	//
	// }

	private void classifyTestDataByClassAllPatterns(
			HashMap<String, TSPatterns> representativePatterns,
			ArrayList<TimeSeriesTest> allTestData, String forClass) {

		int k = 1;
		for (TimeSeriesTest testTS : allTestData) {
			knnClassifyAllPatterns(testTS, representativePatterns, k, forClass);
		}

	}

	private void classifyTestData(
			HashMap<String, TSPatterns> representativePatterns,
			Map<String, List<double[]>> testData) {

		DecimalFormat df = new DecimalFormat("#.####");
		int k = 1;

		int totalCNum = 0;
		int totalTSNum = 0;

		for (Entry<String, List<double[]>> e : testData.entrySet()) {
			String classLabel = e.getKey();
			List<double[]> testingTSses = e.getValue();

			int tsNum = testingTSses.size();
			int correctNum = 0;

			for (double[] ts : testingTSses) {
				String assignedLabel = knnClassify(ts, representativePatterns,
						k);
				if (assignedLabel.equals(classLabel)) {
					correctNum++;
				}
			}

			double acc = (double) correctNum / (double) tsNum;

			System.out.println("For class " + classLabel
					+ " , the classification accuracy is: " + correctNum + "/"
					+ tsNum + ", " + df.format(acc * 100) + "%");

			totalCNum += correctNum;
			totalTSNum += tsNum;
		}

		double totalAcc = (double) totalCNum / (double) totalTSNum;
		System.out.println("Total classification accuracy is: " + totalCNum
				+ "/" + totalTSNum + ", " + df.format(totalAcc * 100) + "%");
		System.out.println("Total error rate is: " + df.format(1 - totalAcc));
	}

	public String knnClassify(double[] ts,
			HashMap<String, TSPatterns> representativePatterns, int k) {

		String assignedLabel = "";
		double minDist = INF;

		for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			int patternSize = patternsInClass.getPatterns().size();

			double totalDist = 0;
			double maxDist = 0;
			double minDistOfPatterns = INF;
			for (TSPattern pattern : patternsInClass.getPatterns()) {
				double dist = calcDist(ts, pattern.getPatternTS());

				if (dist == 0) {
					patternSize--;
					continue;
				}

				if (maxDist < dist)
					maxDist = dist;
				if (minDistOfPatterns > dist)
					minDistOfPatterns = dist;
				totalDist += dist;

				// if(minDist > dist){
				// minDist = dist;
				// assignedLabel = classLabel;
				// }
			}

			// if (patternSize == 0)
			// patternSize = 1;
			double meanDist = totalDist / patternSize;
			if (usingMaxDist)
				meanDist = maxDist;
			if (usingMinDix)
				meanDist = minDistOfPatterns;

			if (meanDist < minDist) {
				assignedLabel = classLabel;
				minDist = meanDist;
			}
		}

		return assignedLabel;
	}

	public String knnClassifyTrain(TimeSeriesTrain ts,
			HashMap<String, TSPatterns> representativePatterns, int k) {

		String assignedLabel = "";
		double minDist = INF;

		for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			int patternSize = patternsInClass.getPatterns().size();

			double totalDist = 0;
			double maxDist = 0;
			double minDistOfPatterns = INF;
			for (TSPattern pattern : patternsInClass.getPatterns()) {

				if ((ts.getIdx() == pattern.getFromTS())
						&& (ts.getTrueLable() == pattern.getLabel())) {
					patternSize--;
					continue;
					// return "PatternFromThisTS";
				}

				double dist = calcDist(ts.getValues(), pattern.getPatternTS());

				if (maxDist < dist)
					maxDist = dist;
				if (minDistOfPatterns > dist)
					minDistOfPatterns = dist;
				totalDist += dist;

				// if(minDist > dist){
				// minDist = dist;
				// assignedLabel = classLabel;
				// }
			}

			double meanDist = INF;
			if (patternSize == 0) {
				if (!isAssignOther) {
					return "PatternFromThisTS";
				}
			} else {
				meanDist = totalDist / patternSize;
			}
			if (usingMaxDist)
				meanDist = maxDist;
			if (usingMinDix)
				meanDist = minDistOfPatterns;

			if (meanDist < minDist) {
				assignedLabel = classLabel;
				minDist = meanDist;
			}
		}

		return assignedLabel;
	}

	//
	// public String knnClassifyTrainMinOverlap(TimeSeriesTrain ts,
	// HashMap<String, TSPatterns> representativePatterns, int k) {
	//
	// String assignedLabel = "";
	// double minDist = INF;
	//
	// for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
	// String classLabel = e.getKey();
	// TSPatterns patternsInClass = e.getValue();
	//
	// int patternSize = patternsInClass.getPatterns().size();
	//
	// double totalDist = 0;
	// // {startP, endP}
	// ArrayList<ClosestMatch> matchIdxes = new ArrayList<ClosestMatch>();
	//
	// for (TSPattern pattern : patternsInClass.getPatterns()) {
	// if ((ts.getIdx() == pattern.getFromTS())
	// && (ts.getTrueLable() == pattern.getLabel())) {
	// patternSize--;
	// continue;
	// }
	//
	// // match position, closest distance, pattern value
	// ClosestMatch cm = findBestMatch(ts.getValues(),
	// pattern.getPatternTS());
	// matchIdxes.add(cm);
	// }
	//
	// // Who overlapped with who, at what positions
	// ArrayList<OverlapedPattern> overlapedPatterns = new
	// ArrayList<OverlapedPattern>();
	// outer: for (int i = 0; i < matchIdxes.size(); i++) {
	// ClosestMatch cmI = matchIdxes.get(i);
	//
	// for (int j = i + 1; j < matchIdxes.size(); j++) {
	// int[] poI = cmI.getPosition();
	// double distI = cmI.getDist();
	//
	// // outer pattern has been removed because of the overlap
	// if (distI < 0)
	// continue outer;
	//
	// ClosestMatch cmJ = matchIdxes.get(j);
	// int[] poJ = cmJ.getPosition();
	// double distJ = cmJ.getDist();
	// // outer pattern has been removed because of the overlap
	// if (distJ < 0)
	// continue;
	//
	// // The overlapped position of outer pattern and inner
	// // pattern
	// int[] overLap = findOverlap(poI, poJ);
	// if (overLap != null) {
	// int[] overlapName = { i, j };
	// OverlapedPattern op = new OverlapedPattern(overlapName,
	// overLap);
	// overlapedPatterns.add(op);
	//
	// removeOverlappedPart(overLap, cmI, cmJ, ts.getValues());
	// }
	// }
	// }
	//
	// for (ClosestMatch cm : matchIdxes) {
	// if (cm.getDist() > 0)
	// totalDist += cm.getDist();
	// else
	// patternSize--;
	// }
	//
	// double meanDist = INF;
	// if (patternSize == 0) {
	// if (!isAssignOther) {
	// return "PatternFromThisTS";
	// }
	// } else {
	// // meanDist = totalDist / patternSize;
	// meanDist = totalDist;
	// }
	//
	// if (meanDist < minDist) {
	// assignedLabel = classLabel;
	// minDist = meanDist;
	// }
	// }
	//
	// return assignedLabel;
	// }
	//
	// /**
	// * remove the part of the one whose distance is bigger.
	// *
	// * @param overLap
	// * @param cmI
	// * @param cmJ
	// * @param ts
	// */
	// private void removeOverlappedPart(int[] overLap, ClosestMatch cmI,
	// ClosestMatch cmJ, double[] ts) {
	//
	// int startO = overLap[0];
	// int endO = overLap[1];
	//
	// double distI = cmI.getDist();
	// double distJ = cmJ.getDist();
	// int[] poI = cmI.getPosition();
	// int[] poJ = cmJ.getPosition();
	//
	// if (distI <= distJ) {
	// // In this case, remove J.
	//
	// int startJ = poJ[0];
	// int endJ = poJ[1];
	//
	// // If I is a subsequence of J, remove I although I has a smaller
	// // distance.
	// if (startO > startJ && endO < endJ) {
	// modifyWorsePattern(cmI, startO, endO, poI, ts);
	// } else {
	// modifyWorsePattern(cmJ, startO, endO, poJ, ts);
	// }
	// } else {
	// // In this case, remove I.
	//
	// int startI = poI[0];
	// int endI = poI[1];
	// if (startO > startI && endO < endI) {
	// modifyWorsePattern(cmJ, startO, endO, poJ, ts);
	// } else {
	// modifyWorsePattern(cmI, startO, endO, poI, ts);
	// }
	// }
	// }
	//
	// private void modifyWorsePattern(ClosestMatch cmJ, int startO, int endO,
	// int[] poJ, double[] ts) {
	//
	// int startJ = poJ[0];
	// int endJ = poJ[1];
	// // ArrayList<ClosestMatch> newCM = new ArrayList<ClosestMatch>();
	//
	// if (startO <= startJ && endO >= endJ) {
	// cmJ.setDist(-1);
	// cmJ.setPosition(null);
	// cmJ.setPatternTS(null);
	// } else if (startJ < startO) {
	// int[] newPoJ = { startJ, startO - 1 };
	// double[] patternTS = cmJ.getPatternTS();
	// double[] newPatternTS = Arrays.copyOfRange(patternTS, 0, startO
	// - startJ);
	//
	// double newDist = calcDistNewPattern(ts, newPatternTS, newPoJ);
	// // ClosestMatch cm1 = new ClosestMatch(newPoJ, newDist);
	// // cm1.setPatternTS(newPatternTS);
	//
	// cmJ.setPosition(newPoJ);
	// cmJ.setDist(newDist);
	// cmJ.setPatternTS(newPatternTS);
	// // newCM.add(cm1);
	// } else if (endO < endJ) {
	// int[] newPoJ = { endO + 1, endJ };
	// double[] patternTS = cmJ.getPatternTS();
	// double[] newPatternTS = null;
	// double newDist = 0;
	// if (patternTS.length - (endJ - endO) < 0) {
	// System.out.print("hi");
	//
	// } else {
	// newPatternTS = Arrays.copyOfRange(patternTS, patternTS.length
	// - (endJ - endO), patternTS.length);
	// newDist = calcDistNewPattern(ts, newPatternTS, newPoJ);
	// }
	//
	// // ClosestMatch cm2 = new ClosestMatch(newPoJ, newDist);
	// // cm2.setPatternTS(newPatternTS);
	//
	// cmJ.setPosition(newPoJ);
	// cmJ.setDist(newDist);
	// cmJ.setPatternTS(newPatternTS);
	// // newCM.add(cm2);
	// }
	// }
	//
	// private double calcDistNewPattern(double[] ts, double[] pValue,
	// int[] position) {
	// int patternLen = pValue.length;
	//
	// double[] slidingWindow = new double[patternLen];
	// slidingWindow = Arrays.copyOfRange(ts, position[0], position[1] + 1);
	// // System.arraycopy(ts, position[0], slidingWindow, 0, patternLen);
	//
	// double tempDist = 0;
	// if (isNormalize) {
	// tempDist = DistMethods.eculideanDistNorm(pValue, slidingWindow);
	// } else {
	// tempDist = DistMethods.eculideanDist(pValue, slidingWindow);
	// }
	//
	// return tempDist;
	// }
	//
	// private int[] findOverlap(int[] previousIdx, int[] idx) {
	// int previousStartP = previousIdx[0];
	// int previousEndP = previousIdx[1];
	// int thisStartP = idx[0];
	// int thisEndP = idx[1];
	//
	// if (previousStartP > thisEndP || thisStartP > previousEndP) {
	// return null;
	// } else {
	// int overlapedStartP = previousStartP > thisStartP ? previousStartP
	// : thisStartP;
	// int overlapedEndP = previousEndP < thisEndP ? previousEndP
	// : thisEndP;
	// int[] result = { overlapedStartP, overlapedEndP };
	// return result;
	// }
	// }

	public void knnClassify(BestCombination paramClsI, TimeSeriesTest tst,
			HashMap<String, TSPatterns> representativePatterns, int k,
			String forClass) {

		double[] ts = tst.getValues();

		String assignedLabel = "";
		double minDist = INF;
		double lenNN = 0;
		double thisMinError = INF;

		for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			int patternSize = patternsInClass.getPatterns().size();

			double totalDist = 0;
			double thisLen = 0;

			Map<Integer, Double> matchPs = new HashMap<Integer, Double>();
			tst.setMatchPs(matchPs);

			double maxDist = 0;
			double minDistOfPatterns = INF;
			for (TSPattern pattern : patternsInClass.getPatterns()) {

				double dist = calcDist(ts, pattern.getPatternTS());
				// double dist = calcDistDTW(ts, pattern.getPatternTS());

				// if (dist == 0) {
				// patternSize--;
				// continue;
				// }

				if (maxDist < dist)
					maxDist = dist;
				if (minDistOfPatterns > dist)
					minDistOfPatterns = dist;

				totalDist += dist;
				// if(minDist > dist){
				// minDist = dist;
				// assignedLabel = classLabel;
				// }
				thisLen += pattern.getPatternTS().length;
			}

			double meanDist = totalDist / patternSize;
			double meanLen = thisLen / patternSize;
			if (usingMaxDist)
				meanDist = maxDist;
			if (usingMinDix)
				meanDist = minDistOfPatterns;

			if (meanDist < minDist) {
				assignedLabel = classLabel;
				minDist = meanDist;
				lenNN = meanLen;
				thisMinError = paramClsI.getMinimalError();
			}
		}

		if (assignedLabel.equalsIgnoreCase(forClass)) {
			if (tst.getAssignedByClass()) {
				// if (tst.getMinError() < thisMinError) {
				// tst.setAssignedLabel(assignedLabel);
				// tst.setDistToNN(minDist);
				// tst.setAssignedByClass(true);
				// } else if (tst.getMinError() == thisMinError) {

				if (tst.getDistToNN() > minDist) {
					// if(tst.getLenNN() < meanLenNN){
					tst.setAssignedLabel(assignedLabel);
					tst.setDistToNN(minDist);
					tst.setAssignedByClass(true);
				}
				// }
			} else {
				tst.setAssignedLabel(assignedLabel);
				tst.setDistToNN(minDist);
				tst.setAssignedByClass(true);
				tst.setMinError(thisMinError);
			}
		} else {
			if (!tst.getAssignedByClass()) {

				// TSPatterns patternsInThisClass = representativePatterns
				// .get(forClass);
				// // tst.getLabelByOther().add(assignedLabel);
				// double totalDist = 0;
				// int patternSize = patternsInThisClass.getPatterns().size();
				// double maxDist = 0;
				// for (TSPattern pattern : patternsInThisClass.getPatterns()) {
				//
				// double dist = calcDist(ts, pattern.getPatternTS());
				//
				// if (maxDist < dist)
				// maxDist = dist;
				//
				// totalDist += dist;
				// }
				//
				// double meanDist = totalDist / patternSize;
				// if (usingMaxDist)
				// meanDist = maxDist;
				//
				// if (tst.getAssignedLabel() == null) {
				// tst.setAssignedLabel(forClass);
				// tst.setDistToNN(meanDist);
				//
				// } else {
				// if (meanDist < tst.getDistToNN()) {
				// tst.setAssignedLabel(forClass);
				// tst.setDistToNN(meanDist);
				// }
				// }

				if (tst.getAssignedLabel() == null) {
					tst.setAssignedLabel(assignedLabel);
					tst.setDistToNN(minDist);
					tst.setLenNN(lenNN);
				} else {
					if (tst.getDistToNN() > minDist) {
						tst.setAssignedLabel(assignedLabel);
						tst.setDistToNN(minDist);
						tst.setLenNN(lenNN);

					}
				}
				if (tst.getAssignedLabels().containsKey(assignedLabel)) {
					int count = tst.getAssignedLabels().get(assignedLabel) + 1;
					tst.getAssignedLabels().put(assignedLabel, count);
				} else {
					tst.getAssignedLabels().put(assignedLabel, 1);
				}

			}
		}
	}

	public double knnClassifyTransfer(TimeSeriesTest tst,
			HashMap<String, TSPatterns> representativePatterns, int k,
			String forClass) {
		String trueLabel = tst.getTrueLable();
		double[] ts = tst.getValues();

		int clsNum = representativePatterns.entrySet().size();
		double distToPatterns = INF;

		for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
			String classLabel = e.getKey();

			if (!forClass.equals(classLabel))
				continue;

			int clsLabelInt = Integer.parseInt(classLabel);
			TSPatterns patternsInClass = e.getValue();

			double totalDist = 0;

			Map<Integer, Double> matchPs = new HashMap<Integer, Double>();
			tst.setMatchPs(matchPs);

			double maxDist = 0;
			double minDistOfPatterns = INF;
			for (TSPattern pattern : patternsInClass.getPatterns()) {

				double dist = calcDist(ts, pattern.getPatternTS());

				if (maxDist < dist)
					maxDist = dist;
				if (minDistOfPatterns > dist)
					minDistOfPatterns = dist;

				totalDist += dist;
			}

			distToPatterns = totalDist;
		}

		return distToPatterns;
	}

	//
	// public void knnClassifyMinOverlap(TimeSeriesTest tst,
	// HashMap<String, TSPatterns> representativePatterns, int k,
	// String forClass) {
	//
	// double[] ts = tst.getValues();
	//
	// String assignedLabel = "";
	// double minDist = INF;
	// double lenNN = 0;
	//
	// for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
	// String classLabel = e.getKey();
	// TSPatterns patternsInClass = e.getValue();
	//
	// int patternSize = patternsInClass.getPatterns().size();
	//
	// double totalDist = 0;
	//
	// Map<Integer, Double> matchPs = new HashMap<Integer, Double>();
	// tst.setMatchPs(matchPs);
	//
	// ArrayList<ClosestMatch> matchIdxes = new ArrayList<ClosestMatch>();
	//
	// for (TSPattern pattern : patternsInClass.getPatterns()) {
	// ClosestMatch cm = findBestMatch(ts, pattern.getPatternTS());
	// cm.setPatternTS(pattern.getPatternTS());
	// matchIdxes.add(cm);
	// }
	//
	// ArrayList<OverlapedPattern> overlapedPatterns = new
	// ArrayList<OverlapedPattern>();
	// outer: for (int i = 0; i < matchIdxes.size(); i++) {
	// ClosestMatch cmI = matchIdxes.get(i);
	//
	// for (int j = i + 1; j < matchIdxes.size(); j++) {
	// int[] poI = cmI.getPosition();
	// double distI = cmI.getDist();
	// if (distI < 0)
	// continue outer;
	//
	// ClosestMatch cmJ = matchIdxes.get(j);
	// int[] poJ = cmJ.getPosition();
	// double distJ = cmJ.getDist();
	// if (distJ < 0)
	// continue;
	//
	// int[] overLap = findOverlap(poI, poJ);
	//
	// if (overLap != null) {
	// int[] overlapName = { i, j };
	// OverlapedPattern op = new OverlapedPattern(overlapName,
	// overLap);
	// overlapedPatterns.add(op);
	//
	// removeOverlappedPart(overLap, cmI, cmJ, ts);
	//
	// }
	// }
	// }
	//
	// for (ClosestMatch cm : matchIdxes) {
	// if (cm.getDist() > 0)
	// totalDist += cm.getDist();
	// else
	// patternSize--;
	// }
	//
	// double meanDist = totalDist;
	//
	// if (meanDist < minDist) {
	// assignedLabel = classLabel;
	// minDist = meanDist;
	// }
	// }
	//
	// if (assignedLabel.equalsIgnoreCase(forClass)) {
	// if (tst.getAssignedByClass()) {
	// if (tst.getDistToNN() > minDist) {
	// // if(tst.getLenNN() < meanLenNN){
	// tst.setAssignedLabel(assignedLabel);
	// tst.setDistToNN(minDist);
	// tst.setAssignedByClass(true);
	// }
	// } else {
	// tst.setAssignedLabel(assignedLabel);
	// tst.setDistToNN(minDist);
	// tst.setAssignedByClass(true);
	// }
	// } else {
	// if (!tst.getAssignedByClass()) {
	//
	// if (tst.getAssignedLabel() == null) {
	// tst.setAssignedLabel(assignedLabel);
	// tst.setDistToNN(minDist);
	// tst.setLenNN(lenNN);
	// } else {
	// if (tst.getDistToNN() > minDist) {
	// tst.setAssignedLabel(assignedLabel);
	// tst.setDistToNN(minDist);
	// tst.setLenNN(lenNN);
	// }
	// }
	//
	// if (tst.getAssignedLabels().containsKey(assignedLabel)) {
	// int count = tst.getAssignedLabels().get(assignedLabel) + 1;
	// tst.getAssignedLabels().put(assignedLabel, count);
	// } else {
	// tst.getAssignedLabels().put(assignedLabel, 1);
	// }
	// }
	// }
	// }

	public void knnClassifyAllPatterns(TimeSeriesTest tst,
			HashMap<String, TSPatterns> representativePatterns, int k,
			String forClass) {

		double[] ts = tst.getValues();

		String assignedLabel = "";
		double minDist = INF;
		double lenNN = 0;

		for (Entry<String, TSPatterns> e : representativePatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			int patternSize = patternsInClass.getPatterns().size();

			double totalDist = 0;
			double thisLen = 0;

			Map<Integer, Double> matchPs = new HashMap<Integer, Double>();
			tst.setMatchPs(matchPs);

			double maxDist = 0;
			double minDistOfPatterns = INF;
			for (TSPattern pattern : patternsInClass.getPatterns()) {

				double dist = INF;
				for (double[] patterni : pattern.getPatternsInClass()) {
					double tempDist = calcDist(ts, patterni);
					if (dist > tempDist) {
						dist = tempDist;
					}
				}

				totalDist += dist;
				if (maxDist < dist)
					maxDist = dist;
				if (minDistOfPatterns > dist)
					minDistOfPatterns = dist;

				thisLen += pattern.getPatternTS().length;
			}

			double meanDist = totalDist / patternSize;
			double meanLen = thisLen / patternSize;
			if (usingMaxDist)
				meanDist = maxDist;
			if (usingMinDix)
				meanDist = minDistOfPatterns;

			if (meanDist < minDist) {
				assignedLabel = classLabel;
				minDist = meanDist;
				lenNN = meanLen;
			}
		}

		if (assignedLabel.equalsIgnoreCase(forClass)) {
			if (tst.getAssignedByClass()) {
				if (tst.getDistToNN() > minDist) {
					// if(tst.getLenNN() < meanLenNN){
					tst.setAssignedLabel(assignedLabel);
					tst.setDistToNN(minDist);
					tst.setAssignedByClass(true);
				}
			} else {
				tst.setAssignedLabel(assignedLabel);
				tst.setDistToNN(minDist);
				tst.setAssignedByClass(true);
			}
		} else {
			if (!tst.getAssignedByClass()) {

				// tst.getLabelByOther().add(assignedLabel);

				if (tst.getAssignedLabel() == null) {
					tst.setAssignedLabel(assignedLabel);
					tst.setDistToNN(minDist);
					tst.setLenNN(lenNN);
				} else {
					if (tst.getDistToNN() > minDist) {
						tst.setAssignedLabel(assignedLabel);
						tst.setDistToNN(minDist);
						tst.setLenNN(lenNN);
					}
				}
			}
		}
	}

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

		double dist = INF;

		int subsequenceLen = subsequence.length;
		int tsLen = wholeTS.length;

		for (int i = 0; i < tsLen - subsequenceLen + 1; i++) {
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

	/**
	 * Select the most frequent patterns from patterns for each class.
	 * 
	 * @param allPatterns
	 * @param patternRate
	 *            , the percentage of representative pattern number.
	 * @return
	 */
	public static HashMap<String, TSPatterns> selectTopFrequentPatterns(
			HashMap<String, TSPatterns> allPatterns, double patternRate) {
		HashMap<String, TSPatterns> topFrequentPatterns = new HashMap<String, TSPatterns>();

		for (Entry<String, TSPatterns> e : allPatterns.entrySet()) {
			int k = 3;

			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			// Patterns in each class.
			@SuppressWarnings("unchecked")
			ArrayList<TSPattern> tempTSPatterns = (ArrayList<TSPattern>) patternsInClass
					.getPatterns().clone();

			int pNum = (int) (patternRate * tempTSPatterns.size());
			if (pNum > k) {
				k = pNum;
				if (k > 10) {
					k = 10;
				}
			}

			if (tempTSPatterns.size() < k) {
				k = tempTSPatterns.size();
				if (k < 1) {
					continue;
				}
				// return null;
			}

			Collections.sort(tempTSPatterns, Collections.reverseOrder());

			TSPatterns mostFrequentPatterns = new TSPatterns(classLabel);
			for (int i = 0; i < k; i++) {
				TSPattern pattern = tempTSPatterns.get(i);
				mostFrequentPatterns.addPattern(pattern);
			}
			topFrequentPatterns.put(classLabel, mostFrequentPatterns);
		}

		return topFrequentPatterns;
	}

	//
	// private void anotherSetRNN(Map<String, List<double[]>> forRNNData,
	// HashMap<String, TSPatterns> topFrequentPatterns) {
	//
	// for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
	// String label = e.getKey();
	// ArrayList<TSPattern> patterns = e.getValue().getPatterns();
	//
	// for (TSPattern p : patterns) {
	// List<double[]> tsesSameClass = forRNNData.get(label);
	// ArrayList<DistToClass> nns = new ArrayList<DistToClass>();
	//
	// for (double[] ts : tsesSameClass) {
	// double d = calcDist(ts, p.getPatternTS());
	//
	// DistToClass nn = new DistToClass(d, label);
	// nns.add(nn);
	// }
	//
	// Collections.sort(nns, new Comparator<DistToClass>() {
	// public int compare(DistToClass o1, DistToClass o2) {
	// if (o1.getDist() == o2.getDist())
	// return 0;
	// return o1.getDist() < o2.getDist() ? -1 : 1;
	// }
	// });
	//
	// for (Entry<String, List<double[]>> trainTSes : forRNNData
	// .entrySet()) {
	// String trainLabel = trainTSes.getKey();
	// if (trainLabel.equalsIgnoreCase(label))
	// continue;
	//
	// List<double[]> tsesDiffClass = trainTSes.getValue();
	//
	// for (double[] tsDiff : tsesDiffClass) {
	// double d = calcDist(tsDiff, p.getPatternTS());
	// double worstExistD = nns.get(nns.size() - 1).getDist();
	//
	// if (d < worstExistD) {
	// DistToClass goodnn = new DistToClass(d, trainLabel);
	// nns.remove(nns.size() - 1);
	// nns.add(goodnn);
	// Collections.sort(nns,
	// new Comparator<DistToClass>() {
	// public int compare(DistToClass o1,
	// DistToClass o2) {
	// if (o1.getDist() == o2.getDist())
	// return 0;
	// return o1.getDist() < o2.getDist() ? -1
	// : 1;
	// }
	// });
	// }
	// }
	// }
	//
	// double totalNNDist = 0;
	// for (DistToClass nn : nns) {
	// String nnLabel = nn.getLabel();
	// totalNNDist += nn.getDist();
	// if (nnLabel.equalsIgnoreCase(label)) {
	// p.setCorrectNN(p.getCorrectNN() + 1);
	// } else {
	// p.setWrongNN(p.getWrongNN() + 1);
	// if (p.getWrongClass().containsKey(nnLabel)) {
	// p.getWrongClass().put(nnLabel,
	// p.getWrongClass().get(nnLabel) + 1);
	// } else {
	// p.getWrongClass().put(nnLabel, 1);
	// }
	// }
	// }
	//
	// // if (totalNNDist <= 0)
	// // System.out.println();
	//
	// p.setNnDistSum(totalNNDist);
	// }
	// }
	// }

	private void anotherSetRNNTrain(
			Map<String, List<TimeSeriesTrain>> trainDataPerClass,
			HashMap<String, TSPatterns> topFrequentPatterns) {

		for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
			String label = e.getKey();
			ArrayList<TSPattern> patterns = e.getValue().getPatterns();

			for (TSPattern p : patterns) {
				List<TimeSeriesTrain> tsesSameClass = trainDataPerClass
						.get(label);
				ArrayList<DistToClass> nns = new ArrayList<DistToClass>();

				for (TimeSeriesTrain ts : tsesSameClass) {
					if (!isAssignOther) {
						if ((ts.getIdx() == p.getFromTS())
								&& (ts.getTrueLable() == p.getLabel())) {
							continue;
						}
					}

					double d = calcDist(ts.getValues(), p.getPatternTS());

					DistToClass nn = new DistToClass(d, label);
					nns.add(nn);
				}

				Collections.sort(nns, new Comparator<DistToClass>() {
					public int compare(DistToClass o1, DistToClass o2) {
						if (o1.getDist() == o2.getDist())
							return 0;
						return o1.getDist() < o2.getDist() ? -1 : 1;
					}
				});

				for (Entry<String, List<TimeSeriesTrain>> trainTSes : trainDataPerClass
						.entrySet()) {
					String trainLabel = trainTSes.getKey();
					if (trainLabel.equalsIgnoreCase(label))
						continue;

					List<TimeSeriesTrain> tsesDiffClass = trainTSes.getValue();

					for (TimeSeriesTrain tsDiff : tsesDiffClass) {
						double d = calcDist(tsDiff.getValues(),
								p.getPatternTS());
						double worstExistD = nns.get(nns.size() - 1).getDist();

						if (d < worstExistD) {
							DistToClass goodnn = new DistToClass(d, trainLabel);
							nns.remove(nns.size() - 1);
							nns.add(goodnn);
							Collections.sort(nns,
									new Comparator<DistToClass>() {
										public int compare(DistToClass o1,
												DistToClass o2) {
											if (o1.getDist() == o2.getDist())
												return 0;
											return o1.getDist() < o2.getDist() ? -1
													: 1;
										}
									});
						}
					}
				}

				double totalNNDist = 0;
				for (DistToClass nn : nns) {
					String nnLabel = nn.getLabel();
					totalNNDist += nn.getDist();
					if (nnLabel.equalsIgnoreCase(label)) {
						p.setCorrectNN(p.getCorrectNN() + 1);
					} else {
						p.setWrongNN(p.getWrongNN() + 1);
						if (p.getWrongClass().containsKey(nnLabel)) {
							p.getWrongClass().put(nnLabel,
									p.getWrongClass().get(nnLabel) + 1);
						} else {
							p.getWrongClass().put(nnLabel, 1);
						}
					}
				}

				// if (totalNNDist <= 0)
				// System.out.println();

				p.setNnDistSum(totalNNDist);
			}
		}
	}

	/**
	 * select best top k based on the reversed nearest neighbors. Top k has the
	 * most reversed nearest neighbors.
	 * 
	 * @param topFrequentPatterns
	 * @param k
	 */
	public HashMap<String, TSPatterns> selectBestFromRNNTrain(
			HashMap<String, TSPatterns> topFrequentPatterns, int k,
			Map<String, List<TimeSeriesTrain>> trainDataPerClass) {

		k = 3;
		HashMap<String, TSPatterns> representativePatterns = new HashMap<String, TSPatterns>();

		anotherSetRNNTrain(trainDataPerClass, topFrequentPatterns);

		// for (Entry<String, List<TimeSeriesTrain>> e : trainDataPerClass
		// .entrySet()) {
		// String classLabel = e.getKey();
		// List<TimeSeriesTrain> tses = e.getValue();
		//
		// for (TimeSeriesTrain ts : tses) {
		// setRNN(ts, topFrequentPatterns, classLabel);
		// }
		// }

		// Using F1 score to give representative score to each pattern.
		for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			// int tsNum = trainDataPerClass.get(classLabel).size();

			for (TSPattern p : patternsInClass.getPatterns()) {
				int correctNum = p.getCorrectNN();
				int wrongNum = p.getWrongNN();

				int tsNum = correctNum + wrongNum;

				double error = DataProcessor.computeErrorF1(correctNum,
						wrongNum, tsNum);
				p.setError(error);
			}
		}

		for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();
			int tsLen = trainDataPerClass.get(classLabel).get(0).getValues().length;

			// Choosing top k with the smallest error.
			TSPatterns topKPatternsThisClass = selectTopKByF1score(classLabel,
					patternsInClass, k);
			// Choosing top k with the biggest correct - wrong number.
			// TSPatterns topKPatternsThisClass = selectTopKByCW(classLabel,
			// patternsInClass, k);

			representativePatterns.put(classLabel, topKPatternsThisClass);
		}

		return representativePatterns;
	}

	/**
	 * Transform time series in to new space, which has features as the distance
	 * to patterns.
	 * 
	 * @param selectedPatterns
	 * @param trainDataPerClass
	 */
	public PatternsAndTransformedData transformTS(
			HashMap<String, TSPatterns> selectedPatterns,
			Map<String, List<TimeSeriesTrain>> trainDataPerClass) {

		int tsNum = 0;
		int patternNum = 0;
		for (Entry<String, List<TimeSeriesTrain>> eTrain : trainDataPerClass
				.entrySet()) {
			String label = eTrain.getKey();
			tsNum += eTrain.getValue().size();

			TSPatterns tsps = selectedPatterns.get(label);
			patternNum += tsps.getPatterns().size();
		}

		TSPattern[] allPatterns = new TSPattern[patternNum];
		int idxPattern = 0;
		// Put all patterns together
		for (Entry<String, TSPatterns> ePattern : selectedPatterns.entrySet()) {
			TSPatterns tsps = ePattern.getValue();
			for (TSPattern tsp : tsps.getPatterns()) {
				allPatterns[idxPattern] = tsp;
				idxPattern++;
			}
		}

		double[][] transformedTS = transformTSWithPatterns(allPatterns,
				trainDataPerClass);

		PatternsAndTransformedData patternsAndTransformedTS = new PatternsAndTransformedData();

		patternsAndTransformedTS.setAllPatterns(allPatterns);
		patternsAndTransformedTS.setTransformedTS(transformedTS);
		return patternsAndTransformedTS;
	}

	public double[][] transformTSWithPatterns(TSPattern[] allPatterns,
			Map<String, List<TimeSeriesTrain>> dataset) {
		int tsNum = 0;
		int patternNum = allPatterns.length;
		for (Entry<String, List<TimeSeriesTrain>> eTrain : dataset.entrySet()) {
			String label = eTrain.getKey();
			tsNum += eTrain.getValue().size();
		}

		double[][] transformedTS = new double[tsNum][patternNum + 1];

		int idxTs = 0;
		for (Entry<String, List<TimeSeriesTrain>> eTrain : dataset.entrySet()) {
			String clsLabel = eTrain.getKey();
			for (TimeSeriesTrain tsTrain : eTrain.getValue()) {
				double[] tsInstance = tsTrain.getValues();

				int idxPattern = 0;
				for (int i = 0; i < patternNum; i++) {

					TSPattern tsp = allPatterns[i];
					transformedTS[idxTs][idxPattern] = calcDist(tsInstance,
							tsp.getPatternTS());
					idxPattern++;

				}
				transformedTS[idxTs][idxPattern] = Integer.parseInt(clsLabel);
				idxTs++;
			}
		}
		return transformedTS;
	}

	public double[][] transformTSWithPatternsTest(TSPattern[] allPatterns,
			Map<String, List<double[]>> dataset) {
		int tsNum = 0;
		int patternNum = allPatterns.length;
		for (Entry<String, List<double[]>> edata : dataset.entrySet()) {
			tsNum += edata.getValue().size();
		}

		double[][] transformedTS = new double[tsNum][patternNum + 1];

		int idxTs = 0;
		for (Entry<String, List<double[]>> eData : dataset.entrySet()) {
			String clsLabel = eData.getKey();
			for (double[] tsInstance : eData.getValue()) {

				int idxPattern = 0;
				for (int i = 0; i < patternNum; i++) {

					TSPattern tsp = allPatterns[i];
					transformedTS[idxTs][idxPattern] = calcDist(tsInstance,
							tsp.getPatternTS());
					idxPattern++;

				}
				transformedTS[idxTs][idxPattern] = Integer.parseInt(clsLabel);
				idxTs++;
			}
		}
		return transformedTS;
	}

	public static final int FILTER_NORMALIZE = 0;
	/** The filter to apply to the training data: Standardize */
	public static final int FILTER_STANDARDIZE = 1;
	/** The filter to apply to the training data: None */
	public static final int FILTER_NONE = 2;
	/** The filter to apply to the training data */
	public static final Tag[] TAGS_FILTER = {
			new Tag(FILTER_NORMALIZE, "Normalize training data"),
			new Tag(FILTER_STANDARDIZE, "Standardize training data"),
			new Tag(FILTER_NONE, "No normalization/standardization"), };

	public SMO getPolySvmClassifier(double svmComplexity,
			double polyKernelExponent) {
		SMO classifier = new SMO();

		//classifier.setBuildLogisticModels(false);
		classifier.setC(svmComplexity);
		classifier.setChecksTurnedOff(false);
		classifier.setDebug(false);
		classifier.setEpsilon(1.0E-12);
		classifier.setFilterType(new SelectedTag(FILTER_NONE, TAGS_FILTER));
		PolyKernel pk = new PolyKernel();
		pk.setCacheSize(250007);
		pk.setChecksTurnedOff(false);
		pk.setDebug(false);
		pk.setExponent(polyKernelExponent);
		pk.setUseLowerOrder(false);
		classifier.setKernel(pk);

		classifier.setNumFolds(-1);
		classifier.setRandomSeed(1);
		classifier.setToleranceParameter(0.001);

		return classifier;
	}

	public static SMO getRbfSvmClassifier(double svmComplexity, double gamma) {
		SMO classifier = new SMO();

		//classifier.setBuildLogisticModels(false);
		classifier.setC(svmComplexity);
		classifier.setChecksTurnedOff(false);
		classifier.setDebug(false);
		classifier.setEpsilon(1.0E-12);
		classifier.setFilterType(new SelectedTag(FILTER_NONE, TAGS_FILTER));

		RBFKernel rk = new RBFKernel();
		rk.setGamma(gamma);

		rk.setChecksTurnedOff(false);
		rk.setDebug(false);
		rk.setCacheSize(250007);

		classifier.setKernel(rk);

		classifier.setNumFolds(-1);
		classifier.setRandomSeed(1);
		classifier.setToleranceParameter(0.001);

		return classifier;
	}

	/*public Classifier chooseBestParamsForClassifier(Classifier cls,
			Instances data) {
		GridSearch gs = new GridSearch();
		gs.setFilter(new AllFilter());
		gs.setClassifier(cls);

		int requiredIndex = 6; // for accuracy
		SelectedTag st = new SelectedTag(requiredIndex,
				weka.classifiers.meta.GridSearch.TAGS_EVALUATION);
		gs.setEvaluation(st);

		gs.setXProperty("classifier.cost");
		gs.setXMin(1);
		gs.setXMax(1001);
		gs.setXStep(100);
		// gs.setXBase(10);
		gs.setXExpression("I");

		gs.setYProperty("classifier.gamma");
		gs.setYMin(-5);
		gs.setYMax(2);
		gs.setYStep(1);
		gs.setYBase(10);
		gs.setYExpression("pow(BASE,I)");

		try {
			gs.buildClassifier(data);
			System.out.println(Utils.joinOptions(gs.getOptions()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gs.getBestClassifier();
	}*/

	/**
	 * 
	 * @param 1, J48; 2, NaiveBayes; 3, BayesNet; 4, LibSVM;
	 * @return
	 */
	public Classifier chooseClassifier(Instances data) {
		int classfier = 4;

		Classifier cls;
		switch (classfier) {
		case 1:
			cls = new J48();
			break;
		case 2:
			cls = new NaiveBayes();
			break;
		case 3:
			cls = new BayesNet();
			break;
		case 4:
			cls = getPolySvmClassifier(1, 3);
			// cls = getRbfSvmClassifier(120, 0.5);
			// cls = new LibSVM();
			// try {
			// String[] options = { "-K", "2", "-S", "0", "-D", "3", "-R",
			// "0.0", "-N", "0.5", "-M", "100.0", "-G", "0.5", "-C",
			// "1000.0", "-E", "0.001", "-P", "0.1", "-Q" };
			// cls.setOptions(options);
			//
			// // CVParameterSelection cvClassifier = new
			// // CVParameterSelection();
			// // cvClassifier.setClassifier(cls);
			// // cvClassifier.addCVParameter("G 0.001 2 10");
			// // cvClassifier.addCVParameter("C 1 1100 10");
			// // cvClassifier.setNumFolds(5);
			// // cvClassifier.buildClassifier(data);
			// // cls.setOptions(cvClassifier.getBestClassifierOptions());
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			break;
		case 5:
			cls = new Logistic();
			break;
		case 6:
			cls = new RandomForest();
			break;
		case 7:
			cls = new LinearRegression();
			break;
		case 8:
			cls = new IBk();
			break;
		default:
			cls = new NaiveBayes();
			break;
		}

		return cls;
	}

	public Evaluation cvEvaluationAllCls(double[][] transformedTS) {
		Instances data = buildArff(transformedTS);

		// AttributeSelectedClassifier classifier = new
		// AttributeSelectedClassifier();
		// CfsSubsetEval eval = new CfsSubsetEval();
		// GreedyStepwise search = new GreedyStepwise();
		// search.setSearchBackwards(true);
		//
		// classifier.setClassifier(chooseClassifier());
		// classifier.setEvaluator(eval);
		// classifier.setSearch(search);
		// 10-fold cross-validation
		Evaluation evaluation;
		Classifier cls = chooseClassifier(data);

		try {
			evaluation = new Evaluation(data);
			evaluation.crossValidateModel(cls, data, 10, new Random(1));
			// System.out.println(evaluation.toSummaryString());

			// double allError = evaluation.errorRate();
			return evaluation;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public double cvEvaluation(double[][] transformedTS) {
		Instances data = buildArff(transformedTS);

		// AttributeSelectedClassifier classifier = new
		// AttributeSelectedClassifier();
		// CfsSubsetEval eval = new CfsSubsetEval();
		// GreedyStepwise search = new GreedyStepwise();
		// search.setSearchBackwards(true);
		//
		// classifier.setClassifier(chooseClassifier());
		// classifier.setEvaluator(eval);
		// classifier.setSearch(search);
		// 10-fold cross-validation
		Evaluation evaluation;
		Classifier cls = chooseClassifier(data);

		try {
			evaluation = new Evaluation(data);
			evaluation.crossValidateModel(cls, data, 10, new Random(1));
			// System.out.println(evaluation.toSummaryString());

			double allError = evaluation.errorRate();
			return allError;
		} catch (Exception e) {
			e.printStackTrace();
			return 1;
		}
	}

	public double classifyTransformedData(double[][] trainData,
			double[][] testData) {
		// return svmClassify(trainData, testData);

		Instances train = buildArff(trainData);
		Instances test = buildArff(testData);

		Classifier classifier = chooseClassifier(train);

		// Select attibutes again
		// AttributeSelectedClassifier classifier = new
		// AttributeSelectedClassifier();
		// CfsSubsetEval eval1 = new CfsSubsetEval();
		// GreedyStepwise search = new GreedyStepwise();
		// search.setSearchBackwards(true);
		// classifier.setClassifier(cls);
		// classifier.setEvaluator(eval1);
		// classifier.setSearch(search);

		try {
			classifier.buildClassifier(train);
			// evaluate classifier and print some statistics
			Evaluation eval = new Evaluation(train);
			eval.evaluateModel(classifier, test);
			String rltString = eval.toSummaryString("\n\n======\nResults: ",
					false);
			System.out.println(rltString);
			DataProcessor.writeClassificationRlt(rltString);
			return eval.errorRate();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return 1;

	}

	public double[][] arffToArray(Instances data) {
		double[][] reversed_rlt = new double[data.numAttributes()][];

		data.setClassIndex(data.numAttributes() - 1);

		for (int i = 0; i < data.numAttributes(); i++) {

			double[] values = data.attributeToDoubleArray(i);
			reversed_rlt[i] = values;
		}

		double[][] rlt = new double[data.numInstances()][data.numAttributes()];

		for (int i = 0; i < data.numInstances(); i++) {
			for (int j = 0; j < data.numAttributes(); j++) {
				rlt[i][j] = reversed_rlt[j][i];
				if (j == data.numAttributes() - 1)
					rlt[i][j]++;
			}
		}
		return rlt;
	}
//
//	public double svmClassify(double[][] trainData, double[][] testData) {
//
//		int numFeature = trainData[0].length;
//
//		svm_parameter param = new svm_parameter();
//		param.svm_type = svm_parameter.C_SVC;
//		param.kernel_type = svm_parameter.RBF;
//		param.degree = 3;
//		param.probability = 0;
//		param.gamma = 0.5;
//		param.coef0 = 0;
//		param.nu = 0.5;
//		param.C = 1;
//		param.cache_size = 100;
//		param.eps = 0.001;
//		// param.shrinking = 1;
//
//		svm_problem prob = new svm_problem();
//		int numTrain = trainData.length;
//		// Number of training data.
//		prob.l = numTrain;
//		// Initial label of traning data.
//		prob.y = new double[numTrain];
//		// Training data
//		prob.x = new svm_node[numTrain][];
//
//		for (int i = 0; i < numTrain; i++) {
//			double[] features = trainData[i];
//			prob.x[i] = new svm_node[features.length - 1];
//			for (int j = 0; j < features.length - 1; j++) {
//				svm_node node = new svm_node();
//				node.index = j;
//				node.value = features[j];
//				prob.x[i][j] = node;
//			}
//			// Assign value to label, training data.
//			prob.y[i] = features[numFeature - 1];
//		}
//
//		svm_model model = svm.svm_train(prob, param);
//		int[] x = model.label;
//		int numTest = testData.length;
//		int missClsNum = 0;
//		for (int i = 0; i < numTest; i++) {
//			double[] featuresTestData = testData[i];
//			missClsNum += evaluateLibSVM(featuresTestData, model, x.length);
//		}
//		double errorRate = (double) missClsNum / (double) numTest;
//		// System.out.println("LibSVM Error Rate: " + fmt.format(errorRate *
//		// 100)
//		// + "%, Incorrect Classified Number: " + missClsNum);
//
//		return errorRate;
//	}
//
//	public int evaluateLibSVM(double[] features, svm_model model,
//			int totalClasses) {
//		svm_node[] nodes = new svm_node[features.length - 1];
//		for (int i = 0; i < features.length - 1; i++) {
//			svm_node node = new svm_node();
//			node.index = i;
//			node.value = features[i];
//
//			nodes[i] = node;
//		}
//
//		int[] labels = new int[totalClasses];
//		svm.svm_get_labels(model, labels);
//
//		double[] prob_estimates = new double[totalClasses];
//		double v = svm.svm_predict_probability(model, nodes, prob_estimates);
//
//		// System.out.println("(Actual:" + features[features.length - 1]
//		// + " Prediction:" + v + ")");
//
//		if (features[features.length - 1] == v)
//			return 0;
//		else
//			return 1;
//	}

	/**
	 * Selected features from transformed time series.
	 * 
	 * @param transformedTS
	 * @return The indices of selected features.
	 */
	public int[] featureSelection(double[][] transformedTS) {
		int attrNum = transformedTS[0].length;

		Instances data = buildArff(transformedTS);
		AttributeSelection attsel = new AttributeSelection();

		CfsSubsetEval eval = new CfsSubsetEval();
		GreedyStepwise search = new GreedyStepwise();
		search.setSearchBackwards(true);

		// SVMAttributeEval eval = new SVMAttributeEval();
		// Ranker search = new Ranker();

		attsel.setEvaluator(eval);
		attsel.setSearch(search);
		try {
			attsel.SelectAttributes(data);
			// obtain the attribute indices that were selected
			int[] indices = attsel.selectedAttributes();
			// System.out.println(Utils.arrayToString(indices));

			// The label index must be in the selected indices.
			if (indices[indices.length - 1] != attrNum - 1) {
				int[] result = Arrays.copyOf(indices, indices.length + 1);
				result[indices.length] = attrNum - 1;
				return result;
			}

			return indices;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Instances buildArff(double[][] array) {
		FastVector atts = new FastVector();
		int attrNum = array[0].length;
		for (int i = 0; i < attrNum - 1; i++) {
			atts.addElement(new Attribute("C" + String.valueOf(i + 1)));
		}

		FastVector classVal = new FastVector();
		List<Integer> clsLabel = new ArrayList<Integer>();
		for (int i = 0; i < array.length; i++) {
			clsLabel.add((int) array[i][attrNum - 1]);
		}
		int clsNum = Collections.max(clsLabel);
		for (int i = 1; i <= clsNum; i++) {
			classVal.addElement(String.valueOf(i));
		}

		atts.addElement(new Attribute("@@class@@", classVal));
		// atts.add(new Attribute("@@class@@", classVal));

		// 2. create Instances object
		Instances test = new Instances("DistanceToPatterns", atts, 0);

		// 3. fill with data
		for (int tsI = 0; tsI < array.length; tsI = tsI + 1) {
			double vals[] = new double[test.numAttributes()];

			for (int attrI = 0; attrI < attrNum - 1; attrI++) {
				vals[attrI] = array[tsI][attrI];
			}
			vals[attrNum - 1] = array[tsI][attrNum - 1] - 1;
			test.add(new SparseInstance(1.0, vals));
		}
		test.setClassIndex(test.numAttributes() - 1);
		return (test);
	}

	public HashMap<String, TSPatterns> selectBestByTransforming(
			HashMap<String, TSPatterns> topFrequentPatterns,
			Map<String, List<TimeSeriesTrain>> trainDataPerClass) {

		HashMap<String, TSPatterns> representativePatterns = new HashMap<String, TSPatterns>();

		anotherSetRNNTrain(trainDataPerClass, topFrequentPatterns);

		// for (Entry<String, List<TimeSeriesTrain>> e : trainDataPerClass
		// .entrySet()) {
		// String classLabel = e.getKey();
		// List<TimeSeriesTrain> tses = e.getValue();
		//
		// for (TimeSeriesTrain ts : tses) {
		// setRNN(ts, topFrequentPatterns, classLabel);
		// }
		// }

		// Using F1 score to give representative score to each pattern.
		for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();

			// int tsNum = trainDataPerClass.get(classLabel).size();

			for (TSPattern p : patternsInClass.getPatterns()) {
				int correctNum = p.getCorrectNN();
				int wrongNum = p.getWrongNN();

				int tsNum = correctNum + wrongNum;

				double error = DataProcessor.computeErrorF1(correctNum,
						wrongNum, tsNum);
				p.setError(error);
			}
		}

		for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
			String classLabel = e.getKey();
			TSPatterns patternsInClass = e.getValue();
			int tsLen = trainDataPerClass.get(classLabel).get(0).getValues().length;

			// Choosing top k with the smallest error.
			// TSPatterns topKPatternsThisClass =
			// selectTopKByF1score(classLabel,
			// patternsInClass, k);
			// Choosing top k with the biggest correct - wrong number.
			// TSPatterns topKPatternsThisClass = selectTopKByCW(classLabel,
			// patternsInClass, k);

			// representativePatterns.put(classLabel, topKPatternsThisClass);
		}

		return representativePatterns;
	}

	/*
	 * public HashMap<String, TSPatterns> selectBestFromRNNTrain(
	 * HashMap<String, TSPatterns> topFrequentPatterns, int k, Map<String,
	 * List<TimeSeriesTrain>> trainDataPerClass) {
	 * 
	 * k = 3; HashMap<String, TSPatterns> representativePatterns = new
	 * HashMap<String, TSPatterns>();
	 * 
	 * anotherSetRNNTrain(trainDataPerClass, topFrequentPatterns);
	 * 
	 * // for (Entry<String, List<TimeSeriesTrain>> e : trainDataPerClass //
	 * .entrySet()) { // String classLabel = e.getKey(); //
	 * List<TimeSeriesTrain> tses = e.getValue(); // // for (TimeSeriesTrain ts
	 * : tses) { // setRNN(ts, topFrequentPatterns, classLabel); // } // }
	 * 
	 * // Using F1 score to give representative score to each pattern. for
	 * (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) { String
	 * classLabel = e.getKey(); TSPatterns patternsInClass = e.getValue();
	 * 
	 * // int tsNum = trainDataPerClass.get(classLabel).size();
	 * 
	 * for (TSPattern p : patternsInClass.getPatterns()) { int correctNum =
	 * p.getCorrectNN(); int wrongNum = p.getWrongNN();
	 * 
	 * int tsNum = correctNum + wrongNum;
	 * 
	 * // if (correctNum > 0) // correctNum -= 1;
	 * 
	 * double error = DataProcessor.computeErrorF1(correctNum, wrongNum, tsNum);
	 * p.setError(error); } }
	 * 
	 * for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
	 * String classLabel = e.getKey(); TSPatterns patternsInClass =
	 * e.getValue(); int tsLen =
	 * trainDataPerClass.get(classLabel).get(0).getValues().length;
	 * 
	 * // Choosing top k with the smallest error. TSPatterns
	 * topKPatternsThisClass = selectTopKByF1score(classLabel, patternsInClass,
	 * k, tsLen); // Choosing top k with the biggest correct - wrong number. //
	 * TSPatterns topKPatternsThisClass = selectTopKByCW(classLabel, //
	 * patternsInClass, k);
	 * 
	 * representativePatterns.put(classLabel, topKPatternsThisClass); }
	 * 
	 * return representativePatterns; }
	 */

	// /**
	// * select best top k based on the reversed nearest neighbors. Top k has
	// the
	// * most reversed nearest neighbors.
	// *
	// * @param topFrequentPatterns
	// * @param k
	// */
	// public HashMap<String, TSPatterns> selectBestFromRNN(
	// HashMap<String, TSPatterns> topFrequentPatterns, int k,
	// Map<String, List<double[]>> forRNNData) {
	//
	// k = 3;
	// HashMap<String, TSPatterns> representativePatterns = new HashMap<String,
	// TSPatterns>();
	//
	// anotherSetRNN(forRNNData, topFrequentPatterns);
	//
	// // for (Entry<String, List<double[]>> e : forRNNData.entrySet()) {
	// // String classLabel = e.getKey();
	// // List<double[]> tses = e.getValue();
	// //
	// // for (double[] ts : tses) {
	// // setRNN(ts, topFrequentPatterns, classLabel);
	// // }
	// // }
	//
	// // Using F1 score to give representative score to each pattern.
	// for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
	// String classLabel = e.getKey();
	// TSPatterns patternsInClass = e.getValue();
	//
	// int tsNum = forRNNData.get(classLabel).size();
	//
	// for (TSPattern p : patternsInClass.getPatterns()) {
	// int correctNum = p.getCorrectNN();
	// int wrongNum = p.getWrongNN();
	//
	// // if (correctNum > 0)
	// // correctNum -= 1;
	//
	// double error = DataProcessor.computeErrorF1(correctNum,
	// wrongNum, tsNum);
	// p.setError(error);
	// }
	// }
	//
	// for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
	// String classLabel = e.getKey();
	// TSPatterns patternsInClass = e.getValue();
	//
	// // Choosing top k with the smallest error.
	// TSPatterns topKPatternsThisClass = selectTopKByF1score(classLabel,
	// patternsInClass, k);
	// // Choosing top k with the biggest correct - wrong number.
	// // TSPatterns topKPatternsThisClass = selectTopKByCW(classLabel,
	// // patternsInClass, k);
	//
	// representativePatterns.put(classLabel, topKPatternsThisClass);
	// }
	//
	// return representativePatterns;
	// }

	private TSPatterns selectTopKByCW(String classLabel,
			TSPatterns patternsInClass, int k) {

		TSPatterns rPatterns = new TSPatterns(classLabel);
		for (TSPattern p : patternsInClass.getPatterns()) {

			if (rPatterns.getPatterns().size() < k) {
				TSPattern newP = new TSPattern(p);
				rPatterns.addPattern(newP);
				continue;
			}

			int correctNum = p.getCorrectNN();
			int wrongNum = p.getWrongNN();
			int countNum = correctNum - wrongNum;

			Boolean isRemove = false;
			int removeIdx = 0;
			for (int i = 0; i < rPatterns.getPatterns().size(); i++) {
				TSPattern existPattern = rPatterns.getPatterns().get(i);
				int eCorrectNum = existPattern.getCorrectNN();
				int eWrongNum = existPattern.getWrongNN();
				int eCountNum = eCorrectNum - eWrongNum;

				// if (eCorrectNum < correctNum) {
				// countNum = eCountNum;
				// correctNum = eCorrectNum;
				// wrongNum = eWrongNum;
				// removeIdx = i;
				// isRemove = true;
				// }

				if (eCountNum < countNum) {
					countNum = eCountNum;
					correctNum = eCorrectNum;
					wrongNum = eWrongNum;
					removeIdx = i;
					isRemove = true;
				} else if (eCountNum == countNum) {
					if (eCorrectNum < correctNum) {
						countNum = eCountNum;
						correctNum = eCorrectNum;
						wrongNum = eWrongNum;
						removeIdx = i;
						isRemove = true;
					}
				}

				// else if (eCountNum == countNum
				// && isNewSTDBetter(existPattern, p)) {
				// // if (eCorrectNum < correctNum) {
				// // countNum = eCountNum;
				// // correctNum = eCorrectNum;
				// // wrongNum = eWrongNum;
				// // removeIdx = i;
				// // isRemove = true;
				// // } else if ((eCorrectNum == correctNum)
				// // && isNewSTDBetter(existPattern, p)) {
				// countNum = eCountNum;
				// correctNum = eCorrectNum;
				// wrongNum = eWrongNum;
				// removeIdx = i;
				// isRemove = true;
				// // }
				// }
			}

			if (isRemove) {
				rPatterns.getPatterns().remove(removeIdx);
				TSPattern newP = new TSPattern(p);
				rPatterns.addPattern(newP);
			}
		}
		return rPatterns;
	}

	private TSPatterns selectTopKByF1score(String classLabel,
			TSPatterns patternsInClass, int k) {

		TSPatterns topKPatternsThisClass = new TSPatterns(classLabel);

		// Add top k frequent patterns in this class.
		for (TSPattern p : patternsInClass.getPatterns()) {
			double currentError = p.getError();
			double currentNNDistSum = p.getNnDistSum();
			int currentWrongNum = p.getWrongNN();
			int currentCorrectNum = p.getCorrectNN();
			int currentF = p.getFrequency();
			if (currentError == 1)
				continue;

			if (topKPatternsThisClass.getPatterns().size() < k) {
				// if (topKPatternsThisClass.getPatterns().size() < 3) {
				TSPattern newP = new TSPattern(p);
				topKPatternsThisClass.addPattern(newP);
				continue;
			}

			Boolean isRemove = false;
			int removeIdx = 0;
			for (int i = 0; i < topKPatternsThisClass.getPatterns().size(); i++) {
				TSPattern existPattern = topKPatternsThisClass.getPatterns()
						.get(i);
				double existError = existPattern.getError();
				int existWrongNum = existPattern.getWrongNN();
				int existCorrectNum = existPattern.getCorrectNN();
				double existNNDistSum = existPattern.getNnDistSum();

				if (existError > currentError) {
					currentError = existError;
					removeIdx = i;
					isRemove = true;
				} else if (existError == currentError) {

					if (existNNDistSum > currentNNDistSum) {
						currentWrongNum = existWrongNum;
						removeIdx = i;
						isRemove = true;
					} else if (existNNDistSum == currentNNDistSum) {
						if (existCorrectNum < currentCorrectNum) {
							currentCorrectNum = existCorrectNum;
							removeIdx = i;
							isRemove = true;
						} else if (existPattern.getFrequency() > currentF) {
							currentF = existPattern.getFrequency();
							removeIdx = i;
							isRemove = true;
						}
					}
				}
			}

			if (isRemove) {
				topKPatternsThisClass.getPatterns().remove(removeIdx);
				TSPattern newP = new TSPattern(p);
				topKPatternsThisClass.addPattern(newP);
			}
		}

		if (topKPatternsThisClass.getPatterns().size() < 1) {

			int minWrongNum = (int) INF;
			int bestIdx = 0;
			for (int i = 0; i < patternsInClass.getPatterns().size(); i++) {
				TSPattern p = patternsInClass.getPatterns().get(i);
				int currentWrongNum = p.getWrongNN();

				if (currentWrongNum < minWrongNum) {
					minWrongNum = currentWrongNum;
					bestIdx = i;
				}
			}

			topKPatternsThisClass.addPattern(patternsInClass.getPatterns().get(
					bestIdx));
		}
		// else {
		// addMorePatternDynamic(topKPatternsThisClass, patternsInClass,
		// tsLen);
		// }

		return topKPatternsThisClass;
	}

	private TSPatterns selectTopKByF1scoreDynamic(String classLabel,
			TSPatterns patternsInClass, int k,
			HashMap<String, ArrayList<Integer>> selectedPIdxes,
			ArrayList<Integer> pIdexes) {
		TSPatterns topKPatternsThisClass = new TSPatterns(classLabel);
		ArrayList<Integer> topKPatternsIdx = new ArrayList<Integer>();

		// Add top k frequent patterns in this class.
		for (int pIdx = 0; pIdx < patternsInClass.getPatterns().size(); pIdx++) {
			TSPattern p = patternsInClass.getPatterns().get(pIdx);
			int candidatePIdx = pIdexes.get(pIdx);

			double currentError = p.getError();
			double currentNNDistSum = p.getNnDistSum();
			int currentWrongNum = p.getWrongNN();
			int currentCorrectNum = p.getCorrectNN();
			if (currentError == 1)
				continue;

			if (topKPatternsThisClass.getPatterns().size() < k) {
				// if (topKPatternsThisClass.getPatterns().size() < 3) {
				TSPattern newP = new TSPattern(p);
				topKPatternsThisClass.addPattern(newP);
				topKPatternsIdx.add(candidatePIdx);
				continue;
			}

			Boolean isRemove = false;
			int removeIdx = 0;
			for (int i = 0; i < topKPatternsThisClass.getPatterns().size(); i++) {
				TSPattern existPattern = topKPatternsThisClass.getPatterns()
						.get(i);
				double existError = existPattern.getError();
				int existWrongNum = existPattern.getWrongNN();
				int existCorrectNum = existPattern.getCorrectNN();
				double existNNDistSum = existPattern.getNnDistSum();

				if (existError > currentError) {
					currentError = existError;
					removeIdx = i;
					isRemove = true;
				} else if (existError == currentError) {

					if (existNNDistSum > currentNNDistSum) {
						currentWrongNum = existWrongNum;
						removeIdx = i;
						isRemove = true;
					} else if (existNNDistSum == currentNNDistSum) {
						if (existCorrectNum < currentCorrectNum) {
							currentCorrectNum = existCorrectNum;
							removeIdx = i;
							isRemove = true;
						}
					}
				}
			}

			if (isRemove) {
				topKPatternsThisClass.getPatterns().remove(removeIdx);
				TSPattern newP = new TSPattern(p);
				topKPatternsThisClass.addPattern(newP);

				topKPatternsIdx.remove(removeIdx);
				topKPatternsIdx.add(candidatePIdx);
			}
		}

		if (topKPatternsThisClass.getPatterns().size() < 1) {

			int minWrongNum = (int) INF;
			int bestIdx = 0;
			for (int i = 0; i < patternsInClass.getPatterns().size(); i++) {
				TSPattern p = patternsInClass.getPatterns().get(i);
				int currentWrongNum = p.getWrongNN();

				if (currentWrongNum < minWrongNum) {
					minWrongNum = currentWrongNum;
					bestIdx = i;
				}
			}

			topKPatternsThisClass.addPattern(patternsInClass.getPatterns().get(
					bestIdx));
			topKPatternsIdx.add(pIdexes.get(bestIdx));
		}
		// else {
		// addMorePatternDynamic(topKPatternsThisClass, patternsInClass,
		// tsLen);
		// }

		selectedPIdxes.put(classLabel, topKPatternsIdx);

		return topKPatternsThisClass;
	}

	private void addMorePatternDynamic(TSPatterns topKPatternsThisClass,
			TSPatterns patternsInClass, int tsLen) {

		ArrayList<TSPattern> allPatternsThisClass = patternsInClass
				.getPatterns();
		Collections.sort(allPatternsThisClass, new Comparator<TSPattern>() {
			public int compare(TSPattern o1, TSPattern o2) {
				if (o1.getError() == o2.getError())
					return 0;
				return o1.getError() < o2.getError() ? -1 : 1;
			}
		});

		HashMap<String, Integer> wrongClassifiedClass = new HashMap<String, Integer>();

		double bestError = topKPatternsThisClass.getPatterns().get(0)
				.getError();
		// computeWrongClass(topKPatternsThisClass.getPatterns().get(0),
		// wrongClassifiedClass);
		for (int j = 0; j < allPatternsThisClass.size(); j++) {
			TSPattern p = allPatternsThisClass.get(j);

			if (!isErrorGood(bestError, p.getError()))
				break;

			if (topKPatternsThisClass.getAllLen() >= tsLen) {
				break;
			}

			if (isContainPattern(topKPatternsThisClass.getPatterns(), p))
				continue;

			// computeWrongClass(p, wrongClassifiedClass);
			if (checkEligiability(p, topKPatternsThisClass.getPatterns())) {
				topKPatternsThisClass.addPattern(p);
			}

		}

	}

	private boolean isErrorGood(double bestError, double thisError) {

		double halfBest = 1 - (1 - bestError) / 1.5;

		if (thisError < halfBest)
			return true;

		return false;
	}

	//
	// private void addMorePattern(TSPatterns topKPatternsThisClass,
	// TSPatterns patternsInClass) {
	//
	// ArrayList<TSPattern> allPatternsThisClass = patternsInClass
	// .getPatterns();
	// Collections.sort(allPatternsThisClass, new Comparator<TSPattern>() {
	// public int compare(TSPattern o1, TSPattern o2) {
	// if (o1.getError() == o2.getError())
	// return 0;
	// return o1.getError() < o2.getError() ? -1 : 1;
	// }
	// });
	//
	// HashMap<String, Integer> wrongClassifiedClass = new HashMap<String,
	// Integer>();
	//
	// // computeWrongClass(topKPatternsThisClass.getPatterns().get(0),
	// // wrongClassifiedClass);
	// for (int j = 0; j < allPatternsThisClass.size(); j++) {
	// if (j > 4)
	// break;
	// TSPattern p = allPatternsThisClass.get(j);
	// if (p.getWrongNN() >= p.getCorrectNN())
	// break;
	// if (isContainPattern(topKPatternsThisClass.getPatterns(), p))
	// continue;
	//
	// // computeWrongClass(p, wrongClassifiedClass);
	// if (checkEligiability(p, topKPatternsThisClass.getPatterns())) {
	// topKPatternsThisClass.addPattern(p);
	// }
	//
	// }
	//
	// }

	private void computeWrongClass(TSPattern ep,
			HashMap<String, Integer> wrongClassifiedClass) {

		for (Entry<String, Integer> e : ep.getWrongClass().entrySet()) {
			String wrongLabel = e.getKey();
			int wrongNum = e.getValue();

			if (wrongClassifiedClass.containsKey(wrongLabel)) {
				int bestWrongNum = wrongClassifiedClass.get(wrongLabel);

				if (bestWrongNum > wrongNum) {
					wrongClassifiedClass.put(wrongLabel, wrongNum);
				}
			} else {
				wrongClassifiedClass.put(wrongLabel, wrongNum);
			}
		}
	}

	private boolean checkEligiability(TSPattern p,
			ArrayList<TSPattern> topKPatterns) {

		boolean hasMostCNum = true;

		Map<String, Integer> worstFor = new HashMap<String, Integer>();

		boolean firstTime = true;
		for (TSPattern topkP : topKPatterns) {
			if (topkP.getCorrectNN() >= p.getCorrectNN()) {
				hasMostCNum = false;
			}

			HashMap<String, Integer> wrongClassifiedClass = topkP
					.getWrongClass();

			if (firstTime) {
				worstFor.putAll(wrongClassifiedClass);
				firstTime = false;
				continue;
			}

			Map<String, Integer> newWorstFor = new HashMap<String, Integer>();
			for (Entry<String, Integer> e : worstFor.entrySet()) {
				String wrongLabel = e.getKey();
				if (wrongClassifiedClass.containsKey(wrongLabel)) {
					newWorstFor.put(wrongLabel, e.getValue());
				}
			}

			for (Entry<String, Integer> e : wrongClassifiedClass.entrySet()) {
				String wrongLabel = e.getKey();
				int wrongNum = e.getValue();

				if (newWorstFor.containsKey(wrongLabel)) {
					if (wrongNum < newWorstFor.get(wrongLabel)) {
						newWorstFor.put(wrongLabel, wrongNum);
					}
				}
			}

			worstFor.clear();
			worstFor.putAll(newWorstFor);
		}

		if (hasMostCNum) {
			return true;
		} else {
			String worstLabel = "";
			int worstNum = 0;
			for (Entry<String, Integer> e : worstFor.entrySet()) {
				String wrongLabel = e.getKey();
				int wrongNum = e.getValue();

				if (worstNum < wrongNum) {
					worstNum = wrongNum;
					worstLabel = wrongLabel;
				}
			}
			if (p.getWrongClass().containsKey(worstLabel)) {
				if (p.getWrongClass().get(worstLabel) < worstNum)
					return true;
			} else {
				return true;
			}
		}

		return false;
	}

	/*
	 * private boolean checkEligiability(TSPattern p, ArrayList<TSPattern>
	 * topKPatterns, int tsNum) {
	 * 
	 * for (TSPattern topkP : topKPatterns) { HashMap<String, Integer>
	 * wrongClassifiedClass = topkP .getWrongClass();
	 * 
	 * boolean isgood = false; for (Entry<String, Integer> e :
	 * wrongClassifiedClass.entrySet()) { String wrongLabel = e.getKey(); int
	 * wrongNum = e.getValue();
	 * 
	 * if (!p.getWrongClass().containsKey(wrongLabel)) { isgood = true; break; }
	 * else { if (p.getWrongClass().get(wrongLabel) < wrongNum) { isgood = true;
	 * break; } } }
	 * 
	 * if (!isgood) { return false; } }
	 * 
	 * return true; }
	 */
	private boolean checkEligiability(TSPattern p,
			HashMap<String, Integer> wrongClassifiedClass) {

		for (Entry<String, Integer> e : wrongClassifiedClass.entrySet()) {
			String wrongLabel = e.getKey();
			int wrongNum = e.getValue();

			if (!p.getWrongClass().containsKey(wrongLabel)) {
				return true;
			} else {
				if (p.getWrongClass().get(wrongLabel) < wrongNum) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isContainPattern(ArrayList<TSPattern> ps, TSPattern p) {
		for (TSPattern ep : ps) {
			if (isPatternSame(ep, p)) {
				return true;
			}
		}

		return false;
	}

	private boolean isPatternSame(TSPattern p1, TSPattern p2) {
		if (p1.getError() != p2.getError())
			return false;
		if (p1.getFrequency() != p2.getFrequency())
			return false;
		if (!p1.getPatternTS().equals(p2.getPatternTS()))
			return false;

		return true;
	}

	private Boolean isNewSTDBetter(TSPattern tp, TSPattern tpNew) {
		TSProcessor tsp = new TSProcessor();
		double[] ts = tp.getPatternTS();
		double std = tsp.stDev(ts);

		double[] tsNew = tpNew.getPatternTS();
		double stdNew = tsp.stDev(tsNew);

		if (std < stdNew) {
			return true;
		}
		return false;
	}

	//
	// private void setRNN(double[] ts,
	// HashMap<String, TSPatterns> topFrequentPatterns, String tsLabel) {
	//
	// // find 3 NN.
	// int k = 3;
	//
	// ArrayList<TSPattern> topKNNPatterns = new ArrayList<TSPattern>();
	// ArrayList<Double> relatedDist = new ArrayList<Double>();
	//
	// for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
	// TSPatterns patternsInClass = e.getValue();
	//
	// for (TSPattern p : patternsInClass.getPatterns()) {
	// double[] pValue = p.getPatternTS();
	// double dist = calcDist(ts, pValue);
	// double newDist = dist;
	//
	// if (topKNNPatterns.size() < k) {
	// topKNNPatterns.add(p);
	// relatedDist.add(dist);
	// continue;
	// }
	//
	// Boolean isRemove = false;
	// int removeIdx = 0;
	// Boolean isSame = false;
	//
	// for (int i = 0; i < topKNNPatterns.size(); i++) {
	// TSPattern existPattern = topKNNPatterns.get(i);
	// double existDist = relatedDist.get(i);
	//
	// if (existDist > dist) {
	// dist = existDist;
	// removeIdx = i;
	// isRemove = true;
	// } else if ((existDist == dist) && (dist == 0)) {
	// isSame = true;
	// break;
	// }
	// }
	//
	// if (isRemove) {
	// topKNNPatterns.remove(removeIdx);
	// relatedDist.remove(removeIdx);
	//
	// topKNNPatterns.add(p);
	// relatedDist.add(newDist);
	// } else if (isSame) {
	// topKNNPatterns.add(p);
	// relatedDist.add(newDist);
	// }
	// }
	// }
	//
	// for (TSPattern p : topKNNPatterns) {
	// if (tsLabel.equals(p.getLabel())) {
	// p.setCorrectNN(p.getCorrectNN() + 1);
	// } else {
	// p.setWrongNN(p.getWrongNN() + 1);
	// if (p.getWrongClass().containsKey(tsLabel)) {
	// p.getWrongClass().put(tsLabel,
	// p.getWrongClass().get(tsLabel) + 1);
	// } else {
	// p.getWrongClass().put(tsLabel, 1);
	// }
	// }
	// }
	//
	// // TSPattern nnPatern = topFrequentPatterns.get(tsLabel).getPatterns()
	// // .get(0);
	// // double nnDist = INF;
	// //
	// // int idx = 0;
	// //
	// // for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
	// // TSPatterns patternsInClass = e.getValue();
	// //
	// // for (TSPattern p : patternsInClass.getPatterns()) {
	// // double[] pValue = p.getPatternTS();
	// // double dist = calcDist(ts, pValue);
	// //
	// // // TODO:
	// // // This pattern is from this training ts.
	// // // if (dist == 0)
	// // // continue;
	// //
	// // if (nnDist > dist) {
	// // nnDist = dist;
	// // nnPatern = p;
	// // }
	// // }
	// //
	// // }
	// //
	// // if (tsLabel.equals(nnPatern.getLabel())) {
	// // nnPatern.setCorrectNN(nnPatern.getCorrectNN() + 1);
	// // } else {
	// // nnPatern.setWrongNN(nnPatern.getWrongNN() + 1);
	// // }
	// }
	//
	// private void setRNN(TimeSeriesTrain ts,
	// HashMap<String, TSPatterns> topFrequentPatterns, String tsLabel) {
	//
	// // find 3 NN.
	// int k = 1;
	//
	// ArrayList<TSPattern> topKNNPatterns = new ArrayList<TSPattern>();
	// ArrayList<Double> relatedDist = new ArrayList<Double>();
	//
	// for (Entry<String, TSPatterns> e : topFrequentPatterns.entrySet()) {
	// TSPatterns patternsInClass = e.getValue();
	//
	// for (TSPattern p : patternsInClass.getPatterns()) {
	// if (!isAssignOther) {
	// if ((ts.getIdx() == p.getFromTS())
	// && (ts.getTrueLable() == p.getLabel())) {
	// continue;
	// }
	// }
	//
	// double[] pValue = p.getPatternTS();
	// double dist = calcDist(ts.getValues(), pValue);
	// double newDist = dist;
	//
	// if (topKNNPatterns.size() < k) {
	// topKNNPatterns.add(p);
	// relatedDist.add(dist);
	// continue;
	// }
	//
	// Boolean isRemove = false;
	// int removeIdx = 0;
	//
	// for (int i = 0; i < topKNNPatterns.size(); i++) {
	// double existDist = relatedDist.get(i);
	//
	// if (existDist > dist) {
	// dist = existDist;
	// removeIdx = i;
	// isRemove = true;
	// }
	// }
	//
	// if (isRemove) {
	// topKNNPatterns.remove(removeIdx);
	// relatedDist.remove(removeIdx);
	//
	// topKNNPatterns.add(p);
	// relatedDist.add(newDist);
	// }
	// }
	// }
	//
	// for (TSPattern p : topKNNPatterns) {
	// if (tsLabel.equals(p.getLabel())) {
	// p.setCorrectNN(p.getCorrectNN() + 1);
	// } else {
	// p.setWrongNN(p.getWrongNN() + 1);
	// if (p.getWrongClass().containsKey(tsLabel)) {
	// p.getWrongClass().put(tsLabel,
	// p.getWrongClass().get(tsLabel) + 1);
	// } else {
	// p.getWrongClass().put(tsLabel, 1);
	// }
	// }
	// }
	//
	// }

	// private double calcDistDTW(double[] ts, double[] pValue) {
	// double dist = INF;
	// int patternLen = pValue.length;
	//
	// for (int i = 0; i < ts.length - pValue.length + 1; i++) {
	// double[] slidingWindow = new double[patternLen];
	// System.arraycopy(ts, i, slidingWindow, 0, patternLen);
	//
	// // double tempDist = eculideanDist(pValue, slidingWindow);
	// double tempDist = dtwDist(pValue, slidingWindow);
	//
	// if (tempDist < dist) {
	// dist = tempDist;
	// }
	// }
	//
	// return dist;
	// // return dist / (double) patternLen;
	// }

	//
	// private double calcDist(TimeSeriesTest tst, double[] pValue) {
	// double dist = INF;
	// int patternLen = pValue.length;
	//
	// int matchP = 0;
	// double[] ts = tst.getValues();
	//
	// for (int i = 0; i < ts.length - pValue.length + 1; i++) {
	// double[] slidingWindow = new double[patternLen];
	// System.arraycopy(ts, i, slidingWindow, 0, patternLen);
	//
	// double tempDist = DistMethods.eculideanDist(pValue, slidingWindow);
	// // double tempDist = dtwDist(pValue, slidingWindow);
	//
	// if (tempDist < dist) {
	// dist = tempDist;
	// matchP = i;
	// }
	// }
	//
	// for (Entry<Integer, Double> cMatchP : tst.getMatchPs().entrySet()) {
	// int cmp = cMatchP.getKey();
	// if (Math.abs(cmp - matchP) > patternLen / 10) {
	// if (dist > cMatchP.getValue()) {
	// dist = cMatchP.getValue();
	// }
	// }
	// }
	//
	// tst.getMatchPs().put(matchP, dist);
	// return dist;
	// // return dist / (double) patternLen;
	// }

	//
	// private ClosestMatch findBestMatch(double[] ts, double[] pValue) {
	// double dist = INF;
	// int patternLen = pValue.length;
	// int idx = 0;
	//
	// for (int i = 0; i < ts.length - pValue.length + 1; i++) {
	// double[] slidingWindow = new double[patternLen];
	// System.arraycopy(ts, i, slidingWindow, 0, patternLen);
	//
	// double tempDist = INF;
	// if (isNormalize) {
	// tempDist = DistMethods.eculideanDistNorm(pValue, slidingWindow);
	// } else {
	// tempDist = DistMethods.eculideanDist(pValue, slidingWindow);
	// }
	//
	// if (tempDist < dist) {
	// dist = tempDist;
	// idx = i;
	// }
	// }
	//
	// int startP = idx;
	// int endP = startP + pValue.length;
	// int[] postion = { startP, endP };
	//
	// ClosestMatch cm = new ClosestMatch(postion, dist, pValue);
	//
	// return cm;
	// }

	private static final Alphabet normalA = new NormalAlphabet();

	//
	// private double calcDistSymbolic(double[] ts, double[] pValue) {
	// double dist = INF;
	// int patternLen = pValue.length;
	//
	// try {
	// if (isDTW) {
	// dist = calcDistDTW(ts, pValue);
	// return dist;
	// }
	//
	// int smallestDiffCount = (int) INF;
	// Map<Integer, ArrayList<Integer>> diffCountALl = new HashMap<Integer,
	// ArrayList<Integer>>();
	//
	// int alphabet = 4;
	// int tempPaa = pValue.length / 3;
	// int intPaa = 16 < tempPaa ? 16 : tempPaa;
	//
	// double[] paa1 = TSUtils.optimizedPaa(pValue, intPaa);
	// char[] currentString1 = TSUtils.ts2String(paa1,
	// normalA.getCuts(alphabet));
	//
	// for (int i = 0; i < ts.length - pValue.length + 1; i++) {
	// double[] slidingWindow = new double[patternLen];
	// System.arraycopy(ts, i, slidingWindow, 0, patternLen);
	//
	// double[] paa2 = TSUtils.optimizedPaa(slidingWindow, intPaa);
	// char[] currentString2 = TSUtils.ts2String(paa2,
	// normalA.getCuts(alphabet));
	//
	// Integer diffICount = DistMethods.countDiff(currentString1,
	// currentString2, smallestDiffCount);
	//
	// if (smallestDiffCount > diffICount) {
	// smallestDiffCount = diffICount;
	// }
	//
	// if (diffCountALl.containsKey(diffICount)) {
	// diffCountALl.get(diffICount).add(i);
	// } else {
	// ArrayList<Integer> newIdx = new ArrayList<Integer>();
	// newIdx.add(i);
	// diffCountALl.put(diffICount, newIdx);
	// }
	// }
	//
	// for (int i : diffCountALl.get(smallestDiffCount)) {
	// double[] slidingWindow = new double[patternLen];
	// System.arraycopy(ts, i, slidingWindow, 0, patternLen);
	//
	// double tempDist = INF;
	// if (isNormalize) {
	// tempDist = DistMethods.eculideanDistNorm(pValue,
	// slidingWindow);
	// } else {
	// tempDist = DistMethods.eculideanDist(pValue, slidingWindow);
	// }
	//
	// if (tempDist < dist) {
	// dist = tempDist;
	// }
	//
	// }
	// } catch (TSException e) {
	// e.printStackTrace();
	// }
	// return dist;
	// }

	// private double calcDist(double[] ts, double[] pValue) {
	// double dist = INF;
	// int patternLen = pValue.length;
	//
	// if (isDTW) {
	// dist = calcDistDTW(ts, pValue);
	// return dist;
	// }
	//
	// for (int i = 0; i < ts.length - pValue.length + 1; i++) {
	// double[] slidingWindow = new double[patternLen];
	// System.arraycopy(ts, i, slidingWindow, 0, patternLen);
	//
	// double tempDist = INF;
	// if (isNormalize) {
	// tempDist = DistMethods.eculideanDistNorm(pValue, slidingWindow);
	// } else {
	// tempDist = DistMethods.eculideanDist(pValue, slidingWindow);
	// }
	// // double tempDist = dtwDist(pValue, slidingWindow);
	//
	// if (tempDist < dist) {
	// dist = tempDist;
	// }
	// }
	//
	// return dist;
	// // return dist / (double) patternLen;
	// }

	private int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive

		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	/**
	 * Find the best match in symbolic space.
	 * 
	 * @param ts
	 * @param pValue
	 * @return
	 */
	private int findMatchSymbolic(double[] ts, double[] pValue) {
		int patternLen = pValue.length;
		int bestIdx = 0;

		TSProcessor tsp = new TSProcessor();
		
		try {
			int smallestDiffCount = (int) INF;

			int alphabet = 4;
			int tempPaa = pValue.length / 3;
			int intPaa = 16 < tempPaa ? 16 : tempPaa;

			// PAA for pattern
			double[] paaPattern = tsp.paa(pValue, intPaa);
			// Pattern in symbolic space
			char[] currentStringPattern = tsp.ts2String(paaPattern,
					normalA.getCuts(alphabet));

			int intTS = intPaa * ts.length / pValue.length;
			double[] paaTS = tsp.paa(ts, intTS);
			char[] currentStringTS = tsp.ts2String(paaTS,
					normalA.getCuts(alphabet));

			for (int i = 0; i < currentStringTS.length
					- currentStringPattern.length + 1; i++) {

				char[] currentStringSlidingW = new char[currentStringPattern.length];
				System.arraycopy(currentStringTS, i, currentStringSlidingW, 0,
						currentStringPattern.length);

				Integer diffICount = DistMethods.countDiff(
						currentStringPattern, currentStringSlidingW,
						smallestDiffCount);
				if (smallestDiffCount > diffICount) {
					smallestDiffCount = diffICount;
					bestIdx = i;
				}
			}

			// for (int i = 0; i < ts.length - pValue.length + 1; i++) {
			// double[] slidingWindow = new double[patternLen];
			// System.arraycopy(ts, i, slidingWindow, 0, patternLen);
			//
			// double[] paaSlidingW = TSUtils.optimizedPaa(slidingWindow,
			// intPaa);
			// // Sliding window in symbolic space
			// char[] currentStringSlidingW = TSUtils.ts2String(paaSlidingW,
			// normalA.getCuts(alphabet));
			//
			// Integer diffICount = DistMethods.countDiff(
			// currentStringPattern, currentStringSlidingW,
			// smallestDiffCount);
			//
			// if (smallestDiffCount > diffICount) {
			// smallestDiffCount = diffICount;
			// bestIdx = i;
			// }
			//
			// }

		} catch (Exception e) {
			e.printStackTrace();
		}
		return bestIdx;
	}

	/**
	 * Calculating the distance between time series and pattern.
	 * 
	 * @param ts
	 *            , a series of points for time series.
	 * @param pValue
	 *            , a series of points for pattern.
	 * @return
	 */
	private double calcDist(double[] ts, double[] pValue) {
		double bestDist = INF;
		int patternLen = pValue.length;

		// if (isDTW) {
		// bestDist = calcDistDTW(ts, pValue);
		// return bestDist;
		// }

		int lastStartP = ts.length - pValue.length + 1;
		if (lastStartP < 1)
			return bestDist;

		// Find smallest place in symbolic space
		// int startP = findMatchSymbolic(ts, pValue);
		// startP randomly generate
		int startP = randInt(0, lastStartP - 1);

		double[] slidingWindow = new double[patternLen];

		System.arraycopy(ts, startP, slidingWindow, 0, patternLen);
		bestDist = DistMethods.eculideanDistNorm(pValue, slidingWindow);

		for (int i = 0; i < lastStartP; i++) {
			System.arraycopy(ts, i, slidingWindow, 0, patternLen);

			double tempDist = INF;
			if (isNormalize) {
				tempDist = DistMethods.eculideanDistNormEAbandon(pValue,
						slidingWindow, bestDist);
			} else {
				tempDist = DistMethods.eculideanDist(pValue, slidingWindow);
			}
			// double tempDist = dtwDist(pValue, slidingWindow);

			if (tempDist < bestDist) {
				bestDist = tempDist;
			}
		}

		return bestDist;
		// return dist / (double) patternLen;
	}

	// private double dtwDist(double[] ts1, double[] ts2) {
	//
	// // com.timeseries.TimeSeries tsI = new TimeSeries(ts1, false, false,
	// // ',');
	// // com.timeseries.TimeSeries tsJ = new TimeSeries(ts2, false, false,
	// // ',');
	// // com.dtw.TimeWarpInfo info = com.dtw.DTW.getWarpInfoBetween(tsI, tsJ,
	// // com.util.DistanceFunctionFactory
	// // .getDistFnByName("EuclideanDistance"));
	// // double d = info.getDistance();
	// // return d;
	// //
	// DTW dtw = new DTW(ts1, ts2);
	// return dtw.getDistance();
	// }

	private int classify(String trueClassKey, double[] oneSampleTS,
                         HashMap<String, double[]> concatenateData,
                         NumerosityReductionStrategy strategy, int[][] params,
                         int originalLen, GrammarIndcutionMethod giMethod,
                         HashMap<String, int[]> allStartPositions) {

		HashMap<String, TSPatterns> allPatterns = DataProcessor
				.getPatternsFromSequitur(concatenateData, params, giMethod,
						allStartPositions);

		double patternRate = 0.5;
		// Selected the top k most frequent subsequences for each class.
		HashMap<String, TSPatterns> refinedPatterns = selectTopFrequentPatterns(
				allPatterns, patternRate);
		if (refinedPatterns == null)
			return 0;

		// TODO: Find the reversed nearest neighbor;

		double minDist = -1.0d;
		String className = "";
		for (Entry<String, TSPatterns> e : refinedPatterns.entrySet()) {
			// Calculate the distance between input time series and patterns.
			double dist = calculateDist(oneSampleTS, e.getValue());

			if (dist > minDist) {
				className = e.getKey();
				minDist = dist;
			}
		}

		if (className.equalsIgnoreCase(trueClassKey)) {
			return 1;
		}
		return 0;
	}

	//
	// public DistMatrixElement[][] buildDistMatrix(
	// ArrayList<TSPattern> allPatterns,
	// ArrayList<TimeSeriesTrain> trainData) {
	//
	// int patternNum = allPatterns.size();
	// int tsNum = trainData.size();
	//
	// DistMatrixElement[][] distMatrix = new
	// DistMatrixElement[patternNum][tsNum];
	//
	// for (int patternIdx = 0; patternIdx < patternNum; patternIdx++) {
	// TSPattern pattern = allPatterns.get(patternIdx);
	//
	// for (int tsIdx = 0; tsIdx < tsNum; tsIdx++) {
	// TimeSeriesTrain ts = trainData.get(tsIdx);
	//
	// double dist = calcDist(ts.getValues(), pattern.getPatternTS());
	//
	// // if ((ts.getIdx() == pattern.getFromTS())
	// // && (ts.getTrueLable() == pattern.getLabel())) {
	// // dist = INF;
	// // }
	//
	// DistMatrixElement distE = new DistMatrixElement(
	// pattern.getLabel(), patternIdx, ts.getTrueLable(),
	// tsIdx, dist);
	// distMatrix[patternIdx][tsIdx] = distE;
	// }
	// }
	// return distMatrix;
	// }

	public double[] calcRepresentativePatternsAndError(
			ArrayList<TSPattern> allPatternsList,
			ArrayList<TimeSeriesTrain> trainData,
			Map<String, List<TimeSeriesTrain>> trainDataPerClass,
			HashMap<String, TSPatterns> representativePatterns,
			DistMatrixElement[][] distMatrixE) {
		// get the number of ts in the each class for training data.
		HashMap<String, Integer> tsNumPerClass = new HashMap<String, Integer>();
		for (Entry<String, List<TimeSeriesTrain>> e : trainDataPerClass
				.entrySet()) {
			String tsLabel = e.getKey();
			int tsNum = e.getValue().size();
			tsNumPerClass.put(tsLabel, tsNum);
		}

		for (int patternIdx = 0; patternIdx < allPatternsList.size(); patternIdx++) {
			TSPattern p = allPatternsList.get(patternIdx);
			int tsNum = tsNumPerClass.get(p.getLabel());

			DistMatrixElement[] distToTSforP = distMatrixE[patternIdx];

			// Find the tsNum nearest neighbor for the current pattern.
			DistMatrixElement[] nnDistToTS = Arrays.copyOf(distToTSforP, tsNum);
			Arrays.sort(nnDistToTS, new Comparator<DistMatrixElement>() {
				public int compare(DistMatrixElement o1, DistMatrixElement o2) {
					if (o1.getDist() == o2.getDist())
						return 0;
					return o1.getDist() < o2.getDist() ? -1 : 1;
				}
			});
			for (int tsIdx = tsNum; tsIdx < distToTSforP.length; tsIdx++) {
				DistMatrixElement distE = distToTSforP[tsIdx];
				if (distE.getDist() < nnDistToTS[tsNum - 1].getDist()) {
					nnDistToTS[tsNum - 1] = distE;

					Arrays.sort(nnDistToTS,
							new Comparator<DistMatrixElement>() {
								public int compare(DistMatrixElement o1,
										DistMatrixElement o2) {
									if (o1.getDist() == o2.getDist())
										return 0;
									return o1.getDist() < o2.getDist() ? -1 : 1;
								}
							});
				}

			}

			ArrayList<DistToClass> nns = new ArrayList<DistToClass>();

			for (DistMatrixElement dme : nnDistToTS) {
				DistToClass nn = new DistToClass(dme.getDist(),
						dme.getTsClassLabelString());
				nns.add(nn);
			}

			double totalNNDist = 0;
			for (DistToClass nn : nns) {
				String nnLabel = nn.getLabel();
				totalNNDist += nn.getDist();
				if (nnLabel.equalsIgnoreCase(p.getLabel())) {
					p.setCorrectNN(p.getCorrectNN() + 1);
				} else {
					p.setWrongNN(p.getWrongNN() + 1);
					if (p.getWrongClass().containsKey(nnLabel)) {
						p.getWrongClass().put(nnLabel,
								p.getWrongClass().get(nnLabel) + 1);
					} else {
						p.getWrongClass().put(nnLabel, 1);
					}
				}
			}

			p.setNnDistSum(totalNNDist);

			int correctNum = p.getCorrectNN();
			int wrongNum = p.getWrongNN();
			double error = DataProcessor.computeErrorF1(correctNum, wrongNum,
					tsNum);
			p.setError(error);
		}

		HashMap<String, ArrayList<Integer>> selectedPIdxes = new HashMap<String, ArrayList<Integer>>();

		double[] error = findTop1PatternByF1(trainData, trainDataPerClass,
				distMatrixE, representativePatterns, allPatternsList,
				selectedPIdxes);

		// error = addMorePatterns(trainData, trainDataPerClass,
		// representativePatterns, selectedPIdxes, allPatternsList,
		// distMatrixE, error);

		addRepresentativePatterns(allPatternsList, representativePatterns,
				selectedPIdxes);
		return error;
	}

	private void addRepresentativePatterns(
			ArrayList<TSPattern> allPatternsList,
			HashMap<String, TSPatterns> representativePatterns,
			HashMap<String, ArrayList<Integer>> selectedPIdxes) {
		for (Entry<String, ArrayList<Integer>> e : selectedPIdxes.entrySet()) {
			String patternLabel = e.getKey();
			ArrayList<Integer> rPatternIdx = e.getValue();

			TSPatterns tsps = new TSPatterns(patternLabel);

			for (int rPIdx : rPatternIdx) {
				TSPattern rp = allPatternsList.get(rPIdx);

				tsps.addPattern(rp);
			}
			representativePatterns.put(patternLabel, tsps);
		}

	}

	/**
	 * Add pattern if the classification accuracy improved.
	 * 
	 * @param representativePatterns
	 * @param allPatternsList
	 * @param distMatrixE
	 */
	private double[] addMorePatterns(ArrayList<TimeSeriesTrain> trainData,
			Map<String, List<TimeSeriesTrain>> trainDataPerClass,
			HashMap<String, TSPatterns> representativePatterns,
			HashMap<String, ArrayList<Integer>> selectedPIdxes,
			ArrayList<TSPattern> allPatternsList,
			DistMatrixElement[][] distMatrix, double[] error) {

		int tsNum = trainData.size();
		int clsNum = trainDataPerClass.size();
		int patternNum = allPatternsList.size();

		for (int patternIdx = 0; patternIdx < patternNum; patternIdx++) {
			// for (int tsIdx = 0; tsIdx < tsNum; tsIdx++) {
			DistMatrixElement dme = distMatrix[patternIdx][0];
			String patternClsLabel = dme.getPatternClassLabelString();

			ArrayList<Integer> existPatternIdxForthisClass = selectedPIdxes
					.get(patternClsLabel);

			// If this pattern already be selected as representative pattern.
			if (existPatternIdxForthisClass.contains(patternIdx)) {
				break;
			}

			// Else, check if including this pattern will increase the
			// classification result.
			HashMap<String, ArrayList<Integer>> newSelectedPIdxes = (HashMap<String, ArrayList<Integer>>) selectedPIdxes
					.clone();
			newSelectedPIdxes.get(patternClsLabel).add(patternIdx);
			double[] newError = calcF1Train(trainData, trainDataPerClass,
					allPatternsList, newSelectedPIdxes, distMatrix);
			if (isNewErrorBetter(error, newError, patternClsLabel)) {
				selectedPIdxes = newSelectedPIdxes;
				error = newError;
			}

			// }
		}
		return error;
	}

	private double allError = INF;
	private double newAllError = INF;

	private boolean isNewErrorBetter(double[] error, double[] newError,
			String patternClsLabel) {
		// boolean isNewBetter = false;
		// int patternClsLabelInt = Integer.parseInt(patternClsLabel) - 1;
		// if (error[patternClsLabelInt] > newError[patternClsLabelInt]) {
		// isNewBetter = true;
		// }

		// boolean isNewBetter = true;
		// for (int i = 0; i < error.length; i++) {
		// if (error[i] < newError[i]) {
		// isNewBetter = false;
		// break;
		// }
		// }
		// return isNewBetter;

		if (allError > newAllError) {
			allError = newAllError;
			return true;
		} else {
			return false;
		}

	}

	private double[] calcF1Train(ArrayList<TimeSeriesTrain> trainData,
			Map<String, List<TimeSeriesTrain>> trainDataPerClass,
			ArrayList<TSPattern> allPatternsList,
			HashMap<String, ArrayList<Integer>> selectedPIdxes,
			DistMatrixElement[][] distMatrixE) {
		int tsNum = trainData.size();

		int clsNum = selectedPIdxes.size();

		int[] missclassifiedSamplesPerClass = new int[clsNum];
		int[] correctNumPerClass = new int[clsNum];

		for (int tsIdx = 0; tsIdx < tsNum; tsIdx++) {
			String trueLabel = trainData.get(tsIdx).getTrueLable();
			int trueLabelInt = Integer.parseInt(trueLabel) - 1;
			String assignedLabel = "";
			double minDist = INF;

			for (Entry<String, ArrayList<Integer>> e : selectedPIdxes
					.entrySet()) {
				String patternLabel = e.getKey();
				ArrayList<Integer> patternIdxes = e.getValue();

				// Size of representative patterns.
				int rpNum = patternIdxes.size();

				double totalDist = 0;
				for (int patternIdx : patternIdxes) {
					DistMatrixElement dme = distMatrixE[patternIdx][tsIdx];

					if (dme.getDist() == INF) {
						rpNum--;
						continue;
					} else {
						totalDist += dme.getDist();
					}
				}

				double thisDist = INF;
				if (rpNum > 0)
					thisDist = totalDist / (double) rpNum;

				if (minDist > thisDist) {
					minDist = thisDist;
					assignedLabel = patternLabel;
				}
			}

			if (assignedLabel.equals(trueLabel)) {
				correctNumPerClass[trueLabelInt] += 1;
			} else {
				int assignedLabelInt = Integer.parseInt(assignedLabel) - 1;
				missclassifiedSamplesPerClass[assignedLabelInt] += 1;
			}

		}

		int totalCorrect = 0;
		int totalNum = 0;

		double[] error = new double[clsNum];
		for (int clsIdx = 0; clsIdx < clsNum; clsIdx++) {
			int tsNumHere = trainDataPerClass.get(String.valueOf(clsIdx + 1))
					.size();
			error[clsIdx] = DataProcessor.computeErrorF1(
					correctNumPerClass[clsIdx],
					missclassifiedSamplesPerClass[clsIdx], tsNumHere);

			totalCorrect += correctNumPerClass[clsIdx];
			totalNum += tsNumHere;
		}

		double totalError = 1 - (double) totalCorrect / (double) totalNum;
		if (newAllError > totalError)
			newAllError = totalError;
		if (allError == INF)
			allError = newAllError;

		return error;
	}

	private double[] findTop1PatternByF1(ArrayList<TimeSeriesTrain> trainData,
			Map<String, List<TimeSeriesTrain>> trainDataPerClass,
			DistMatrixElement[][] distMatrix,
			HashMap<String, TSPatterns> representativePatterns,
			ArrayList<TSPattern> allPatternsList,
			HashMap<String, ArrayList<Integer>> selectedPIdxes) {

		int k = 3;
		int clsNum = trainDataPerClass.size();

		for (int clsIdx = 0; clsIdx < clsNum; clsIdx++) {
			String forClass = String.valueOf(clsIdx + 1);
			TSPatterns patternsInClass = new TSPatterns(forClass);
			ArrayList<Integer> pIdexes = new ArrayList<Integer>();

			for (int patternIdx = 0; patternIdx < allPatternsList.size(); patternIdx++) {
				TSPattern p = allPatternsList.get(patternIdx);
				String patternLabel = p.getLabel();

				if (patternLabel.equals(forClass)) {
					patternsInClass.addPattern(p);
					pIdexes.add(patternIdx);
				}
			}

			TSPatterns topKPatternsThisClass = selectTopKByF1scoreDynamic(
					forClass, patternsInClass, k, selectedPIdxes, pIdexes);

		}

		// HashMap<String, TSPattern> topKPatterns = new HashMap<String,
		// TSPattern>();
		// HashMap<String, Integer> selectedPIdx = new HashMap<String,
		// Integer>();
		//
		// for (int patternIdx = 0; patternIdx < allPatternsList.size();
		// patternIdx++) {
		// TSPattern p = allPatternsList.get(patternIdx);
		// String patternLabel = p.getLabel();
		//
		// if (topKPatterns.containsKey(patternLabel)) {
		// TSPattern existP = topKPatterns.get(patternLabel);
		// if (p.getError() < existP.getError()) {
		// topKPatterns.put(patternLabel, p);
		// selectedPIdx.put(patternLabel, patternIdx);
		// } else if (p.getError() == existP.getError()) {
		// if (existP.getNnDistSum() > p.getNnDistSum()) {
		// topKPatterns.put(patternLabel, p);
		// selectedPIdx.put(patternLabel, patternIdx);
		// } else if (existP.getNnDistSum() == p.getNnDistSum()) {
		// if (existP.getCorrectNN() < p.getCorrectNN()) {
		// topKPatterns.put(patternLabel, p);
		// selectedPIdx.put(patternLabel, patternIdx);
		// }
		// }
		// }
		// } else {
		// topKPatterns.put(patternLabel, p);
		// selectedPIdx.put(patternLabel, patternIdx);
		// }
		// }
		//
		// // Put top first representative pattern in Map
		// for (Entry<String, TSPattern> e : topKPatterns.entrySet()) {
		// String label = e.getKey();
		// TSPatterns tsps = new TSPatterns(label);
		// tsps.getPatterns().add(e.getValue());
		// representativePatterns.put(label, tsps);
		//
		// ArrayList<Integer> idxes = new ArrayList<Integer>();
		// idxes.add(selectedPIdx.get(label));
		// selectedPIdxes.put(label, idxes);
		// }

		// Calculation classification F1 score for train data.
		double[] error = calcF1Train(trainData, trainDataPerClass,
				allPatternsList, selectedPIdxes, distMatrix);

		return error;
	}

	private String printHelp() {
		StringBuffer sb = new StringBuffer();
		sb.append("SAX-VSM parameters optimization sampler ").append(CR);
		sb.append("Expects 10 parameters:").append(CR);
		sb.append(" [1] training dataset filename").append(CR);
		sb.append(" [2] test dataset filename").append(CR);
		sb.append(" [3] minimal sliding window size").append(CR);
		sb.append(" [4] maximal sliding window size").append(CR);
		sb.append(" [5] minimal PAA size").append(CR);
		sb.append(" [6] maximal PAA size").append(CR);
		sb.append(" [7] minimal Alphabet size").append(CR);
		sb.append(" [8] maximal Alphabet size").append(CR);
		sb.append(" [8] cross-validation hold-out number").append(CR);
		sb.append(" [8] maximal amount of sampling iterations").append(CR);
		sb.append("An execution example: $java -cp \"sax-vsm-classic20.jar\" net.seninp.jmotif.direct.SAXVSMDirectSampler");
		sb.append(
				" data/SwedishLeaf/SwedishLeaf_TRAIN data/SwedishLeaf/SwedishLeaf_TRAIN_shift.txt 10 120 5 60 2 18 1 10")
				.append(CR);
		return sb.toString();
	}
}
