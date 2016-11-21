package edu.gmu.grammar.classification.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.gmu.connectGI.GrammarIndcutionMethod;
import edu.gmu.grammar.classification.RefineBySecondParams;
import edu.gmu.grammar.patterns.TSPattern;
import edu.gmu.grammar.patterns.TSPatterns;
import edu.gmu.ps.direct.GCErrorFunction;
import net.seninp.jmotif.direct.Point;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.jmotif.sax.TSProcessor;
import net.seninp.util.StackTrace;
import net.seninp.util.UCRUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class ParameterSelection {

	// the number formatter
	private static final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
	private static DecimalFormat fmt = new DecimalFormat("0.00###", otherSymbols);

	// array with all rectangle side lengths in each dimension
	private static ArrayList<Double[]> lengthsSide;

	// array with distances from center points to the vertices
	private static ArrayList<Double> diagonalLength;

	// array vector of all different distances, sorted
	private static ArrayList<Double> differentDiagonalLength;

	// array vector of minimum function value for each distance
	private static double[] diagonalsMinFunc;

	private static double DIFFT = 0.005;

	// array with function values
	// private static ArrayList<Double> functionValues;

	// array used to track sampled points and function values
	// private static ArrayList<ValuePointColored> coordinates;
	private static HashMap<String, Double> functionHash;

	private static final double precision = 1E-16;
	private static int b = 0;
	// private static double[] resultMinimum;

	private static int sampledPoints;
	private static int rectangleCounter;
	private static int indexPotentialBestRec;
	// private static double minFunctionValue;
	private static double[] minFunctionValuesDouble;

	// init bounds
	//
	private static int dimensions = 3;

	// private static GCErrorFunction function;

	// static block - we instantiate the logger
	//
	private static final Logger consoleLogger;
	private static final Level LOGGING_LEVEL = Level.INFO;

	private static final String COMMA = ", ";
	private static final String CR = "\n";

	static {
		consoleLogger = (Logger) LoggerFactory.getLogger(ParameterSelection.class);
		consoleLogger.setLevel(LOGGING_LEVEL);
	}

	// the global minimum point
	// private static ValuePointColored minimum = ValuePointColored.at(
	// Point.at(0), Double.POSITIVE_INFINITY, false);

	// private static BestCombination[] bestcombs;
	private static TopKBestPatterns[] bestKResults;

	private static int[] upperBounds;
	private static int[] lowerBounds;
	private static String TRAINING_DATA;
	private static String TEST_DATA;
	private static int HOLD_OUT_NUM = 1;
	private static int ITERATIONS_NUM = 1;

	private static Map<String, List<double[]>> trainData;
	private static Map<String, List<double[]>> forRNNData;
	private static Map<String, List<double[]>> validateData;
	private static Map<String, List<double[]>> testData;

	private static NumerosityReductionStrategy allStrategy = NumerosityReductionStrategy.EXACT;

	private static Boolean existSAXParam = false;
	private static Boolean isShifted = false;
	private static String storedFileName = "Mallet";
	private static int storedClsName = 8;
	private static GrammarIndcutionMethod giMethod = GrammarIndcutionMethod.SEQUITUR;
	private static int bestK = 3;
	// isUsingMax, isDTW, isNormalize, isUsingMin, isAssignOther
	private static boolean gcParams[] = { false, false, true, false, false };
	private static boolean timing = false;
	private static boolean isSecondRefine = false;
	private static boolean usingDefault = false;

	private static int winStep = 5;
	private static int paaStep = 1;
	private static int alphaStep = 1;

	/**
	 * 
	 * @param args
	 *            : DataName, trainFile, testFile, minWindowSize, maxWindowSize,
	 *            minPAA, maxPAA, minAlphabetSize, maxAlphabetSize,
	 *            NumerosityReductionStrategy, isSecondRefine
	 * @throws IOException
	 * @throws IndexOutOfBoundsException
	 * @throws TSException
	 */
	public static void main(String[] args) throws IOException, IndexOutOfBoundsException {

		// consoleLogger.info("processing paramleters: " +
		// Arrays.toString(args));
		if (args.length == 11) {

			// For timing
			long startTime = System.currentTimeMillis();

			// Main method
			String dataName = args[0];
			String[] initialParams = new String[10];
			System.arraycopy(args, 1, initialParams, 0, 8);
			initialParams[8] = "1";
			initialParams[9] = "10";

			allStrategy = NumerosityReductionStrategy.valueOf(args[9]);
			if (args[10].endsWith("1")) {
				isSecondRefine = true;
			}

			runAndTime(initialParams, dataName);

			// For timing
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			consoleLogger.info("Total runnig time: " + DataProcessor.millisToShortDHMS(totalTime));
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = new Date();
			consoleLogger.info("Current date: " + dateFormat.format(date));
		} else if (args.length == 3) {
			usingDefault = true;

			// For timing
			long startTime = System.currentTimeMillis();

			// Main method
			String dataName = args[0];
			String[] initialParams = new String[10];
			System.arraycopy(args, 1, initialParams, 0, 2);

			// minimal window size
			initialParams[2] = "10";
			// maximal window size
			initialParams[3] = "100";
			// minimal paa size
			initialParams[4] = "3";
			// maximal paa size
			initialParams[5] = "20";
			// minimal alphabet size
			initialParams[6] = "3";
			// maximal alphabet size
			initialParams[7] = "20";

			initialParams[8] = "1";
			initialParams[9] = "10";

			allStrategy = NumerosityReductionStrategy.EXACT;
			isSecondRefine = true;

			runAndTime(initialParams, dataName);

			// For timing
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			consoleLogger.info("Total runnig time: " + DataProcessor.millisToShortDHMS(totalTime));
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			Date date = new Date();
			consoleLogger.info("Current date: " + dateFormat.format(date));
		}

		else {
			System.out.print(printHelp());
			System.exit(-10);
		}

	}

	public static void runAndTime(String[] initialParam, String display) throws IndexOutOfBoundsException, IOException {
		// For timing
		long startTime = System.currentTimeMillis();

		// Main method
		runParamSelect(initialParam, display);

		// For timing
		// consoleLogger.info("End of " + display);
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		consoleLogger.info("Running time for data " + display + ": " + DataProcessor.millisToShortDHMS(totalTime));
	}

	public static void runParamSelect(String[] args, String dataName) throws IOException, IndexOutOfBoundsException {

		try {

			if (10 == args.length) {
				TRAINING_DATA = args[0];
				TEST_DATA = args[1];

				// readData();
				readDataOriginalSplit();
				printDataInfo();

				int lbwindow = Integer.valueOf(args[2]).intValue();
				int ubwindow = Integer.valueOf(args[3]).intValue();

				if (usingDefault) {
					int tsLen = trainData.entrySet().iterator().next().getValue().get(0).length;
					int tenPer = (int) (tsLen * 0.1);
					int lb = tenPer > 1 ? tenPer : 1;
					int fiftyPer = (int) (tsLen * 0.5);
					int ub = fiftyPer > 1 ? fiftyPer : 1;

					lbwindow = lb;
					ubwindow = ub;

					args[2] = String.valueOf(lb);
					args[3] = String.valueOf(ub);
				}

				// window size, PAA size, alphabet size.
				lowerBounds = new int[] { lbwindow, Integer.valueOf(args[4]).intValue(),
						Integer.valueOf(args[6]).intValue() };
				upperBounds = new int[] { ubwindow, Integer.valueOf(args[5]).intValue(),
						Integer.valueOf(args[7]).intValue() };

				consoleLogger.info("processing paramleters: " + Arrays.toString(args));
			} else {
				// System.out.print(printHelp());
				System.exit(-10);
			}
		} catch (Exception e) {
			System.err.println("There was parameters error....");
			System.err.println(StackTrace.toString(e));
			System.out.print(printHelp());
			System.exit(-10);
		}

		ResultParams[] bestKParams;
		SecondBestParams sbp = new SecondBestParams();
		if (existSAXParam) {
			bestKParams = usingSAXParamsBest10(storedFileName, storedClsName);

			// classifyWithExistSAXParams(args, dataName, testParams);
		} else {
			consoleLogger.info("running sampling for " + giMethod + " with " + allStrategy.toString() + " strategy..."
					+ "Step size: " + "{" + winStep + ", " + paaStep + ", " + alphaStep + "}");
			bestKParams = sample(allStrategy, dataName);
		}

		// Get second round patterns which can separate the hard-classified
		// classes of the first round

		ArrayList<ArrayList<String>> secondCLasses = new ArrayList<ArrayList<String>>();
		ArrayList<TopKBestPatterns[]> secondBests = new ArrayList<TopKBestPatterns[]>();
		if (isSecondRefine) {
			RefineBySecondParams r = new RefineBySecondParams(trainData, allStrategy, giMethod, gcParams);
			secondCLasses = r.findSecondParamsByClassifyTrain(bestKResults, dataName);

			secondBests = new ArrayList<TopKBestPatterns[]>();
			for (ArrayList<String> secondC : secondCLasses) {
				TopKBestPatterns[] secondBestKParams = sampleSecond(allStrategy, dataName, secondC);

				secondBests.add(secondBestKParams);
			}
		}

		// for (int j = 0; j < secondBests.size(); j++) {
		// ResultParams[] sp = secondBests.get(j);
		// ArrayList<String> sc = secondCLasses.get(j);
		//
		// StringBuffer sb = new StringBuffer();
		// sb.append("\n");
		// for (int i = 0; i < sp.length; i++) {
		// ResultParams iPram = sp[i];
		// for (int[] res : iPram.getPramList()) {
		// sb.append(Arrays.toString(res));
		// }
		// sb.append("\n");
		// }
		//
		// for (String s : sc) {
		// sb.append(s);
		// sb.append(",");
		// }
		// sb.append("\n");
		// consoleLogger.info(sb.toString());
		// }

		if (existSAXParam) {
			// classifyWithExistSAXParamsBest10(args, dataName, bestKParams,
			// secondCLasses, secondBests);
		} else {
			System.gc();
			classifyWithExistSAXParamsBest10NotRead(args, dataName, bestKResults, secondCLasses, secondBests);
		}
	}

	private static void classifyWithExistSAXParamsBest10(String[] args, String dataName, ResultParams[] best10Params,
			ArrayList<ArrayList<String>> secondCLasses, ArrayList<ResultParams[]> secondBests)
					throws IndexOutOfBoundsException {
		// StringBuffer sb = new StringBuffer();
		// sb.append("\n");
		// for (int i = 0; i < best10Params.length; i++) {
		// // sb.append("\tFor class ").append(i + 1)
		// // .append(" the best 10 params are:\n");
		// ResultParams iPram = best10Params[i];
		// for (int[] res : iPram.getPramList()) {
		// sb.append(Arrays.toString(res));
		// }
		// sb.append("\n");
		// }
		// consoleLogger.info(sb.toString());

		if (isShifted) {
			// Testing with shifted data.
			// Map<String, List<double[]>> shiftedData =
			// generateShiftData(testData);
			Map<String, List<double[]>> shiftedData = DataProcessor.readShiftedData(dataName);
			// DataProcessor.writeShiftedData(shiftedData, dataName);
			DataProcessor.classifyTestingVarBest10Shifted(best10Params, dataName, args, trainData, validateData,
					forRNNData, shiftedData, giMethod, gcParams);
		} else {
			// Using new classification method on regular data.
			DataProcessor.classifyTestingVarBest10(best10Params, dataName, args, trainData, testData, validateData,
					forRNNData, giMethod, gcParams, secondCLasses, secondBests);
		}

	}

	private static void classifyWithExistSAXParamsBest10NotRead(String[] args, String dataName,
			TopKBestPatterns[] best10Params, ArrayList<ArrayList<String>> secondCLasses,
			ArrayList<TopKBestPatterns[]> secondBests) throws IndexOutOfBoundsException {

		// Using new classification method on regular data.
		DataProcessor.classifyTestingVarBest10NotRead(best10Params, dataName, args, trainData, testData, validateData,
				forRNNData, giMethod, gcParams, secondCLasses, secondBests);

	}

	private static void classifyWithExistSAXParams(String[] args, String dataName, int[][] testParams)
			throws IndexOutOfBoundsException {
		consoleLogger.info("processing paramleters: " + Arrays.toString(args));

		// Using new classification method on regular data.
		DataProcessor.classifyTestingVar(testParams, args, trainData, testData, validateData, forRNNData, giMethod,
				gcParams);

		// Testing with shifted data.
		// Map<String, List<double[]>> shiftedData =
		// generateShiftData(testData);
		// // DataProcessor.writeShiftedData(shiftedData, dataName);
		// DataProcessor.classifyTestingVarShifted(testParams, args, trainData,
		// validateData, forRNNData, shiftedData);
	}

	/**
	 * DataName, trainFile, testFile, minWindowSize, maxWindowSize, minPAA,
	 * maxPAA, minAlphabetSize, maxAlphabetSize, NumerosityReductionStrategy,
	 * isSecondRefine
	 * 
	 * @return
	 */
	private static String printHelp() {
		StringBuffer sb = new StringBuffer();
		sb.append("RepresentativePatternSelection parameters optimization sampler ").append(CR);
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
		sb.append(" CBF data/cbf/CBF_TRAIN data/cbf/CBF_TEST 25 40 3 20 3 20 EXACT 1").append(CR);
		sb.append("Another execution example: $java -jar \"representativePattern.jar\"");
		sb.append(" CBF data/cbf/CBF_TRAIN data/cbf/CBF_TEST").append(CR);
		return sb.toString();
	}

	private static Map<String, List<double[]>> generateShiftData(Map<String, List<double[]>> originalData) {
		Map<String, List<double[]>> shiftedData = new HashMap<String, List<double[]>>();

		for (Entry<String, List<double[]>> e : originalData.entrySet()) {
			String label = e.getKey();
			List<double[]> tses = e.getValue();
			List<double[]> rotatedTSes = new ArrayList<double[]>();

			for (double[] ts : tses) {
				double r = Math.random();
				int startP = (int) (r * ts.length);

				double[] firstPart = Arrays.copyOfRange(ts, startP, ts.length);
				double[] secondPart = Arrays.copyOfRange(ts, 0, startP);
				double[] rotatedTS = ArrayUtils.addAll(firstPart, secondPart);
				rotatedTSes.add(rotatedTS);
			}
			shiftedData.put(label, rotatedTSes);
		}

		return shiftedData;

	}

	private static int[][] usingSAXParams() {

		// int[][] testParams = { { 23, 3, 6, 1 }, { 10, 5, 4, 1 },
		// { 29, 3, 4, 1 }, { 28, 3, 8, 1 }, { 10, 3, 5, 1 },
		// { 14, 4, 3, 1 }, { 19, 3, 4, 1 }, { 11, 3, 4, 1 },
		// { 16, 3, 5, 1 }, { 15, 3, 3, 1 }, { 29, 3, 4, 1 },
		// { 11, 3, 4, 1 }, { 19, 3, 8, 1 }, { 14, 3, 4, 1 },
		// { 26, 3, 9, 1 } };
		// int[][] testParams = { { 38, 3, 4, 1 }, { 32, 3, 4, 1 },
		// { 27, 3, 4, 1 }, { 13, 3, 3, 1 }, { 22, 3, 4, 1 },
		// { 66, 3, 8, 1 }, { 58, 3, 4, 1 }, { 70, 3, 6, 1 },
		// { 30, 3, 6, 1 }, { 43, 3, 6, 1 }, { 27, 3, 4, 1 },
		// { 72, 3, 12, 1 }, { 53, 3, 4, 1 }, { 64, 3, 6, 1 } };
		// int[][] testParams = { { 10, 3, 4, 1 }, { 10, 3, 6, 1 },
		// { 11, 3, 8, 1 }, { 11, 3, 6, 1 }, { 12, 3, 4, 1 },
		// { 10, 5, 6, 1 } };

		// int[][] testParamsOSU = { { 72, 3, 11, 2 }, { 78, 3, 11, 2 },
		// { 83, 3, 14, 2 }, { 77, 4, 4, 2 }, { 96, 4, 3, 2 },
		// { 77, 4, 3, 2 } };

		// int[][] testParamsFA = { { 71, 4, 8, 2 }, { 76, 3, 8, 2 },
		// { 68, 5, 4, 2 }, { 45, 4, 3, 2 }, { 68, 4, 10, 2 },
		// { 70, 5, 3, 2 }, { 35, 4, 6, 2 }, { 46, 4, 3, 2 },
		// { 65, 4, 5, 2 }, { 27, 3, 8, 2 }, { 72, 5, 3, 2 },
		// { 74, 4, 7, 2 }, { 90, 6, 5, 2 }, { 99, 4, 8, 2 } };

		int[][] testParamsFF = { { 188, 9, 12, 2 }, { 199, 11, 7, 2 }, { 199, 6, 3, 2 }, { 200, 4, 5, 2 } };
		int[][] testParamsFA = { { 71, 4, 8, 2 }, { 76, 3, 8, 2 }, { 42, 3, 4, 2 }, { 45, 4, 3, 2 }, { 61, 3, 10, 2 },
				{ 52, 3, 5, 2 }, { 35, 4, 6, 2 }, { 46, 4, 3, 2 }, { 65, 4, 5, 2 }, { 27, 3, 8, 2 }, { 55, 3, 14, 2 },
				{ 56, 4, 3, 2 }, { 21, 3, 8, 2 }, { 58, 6, 4, 2 } };

		// int[][] testParamsFAOriginal = { { 49, 3, 15, 2 }, { 60, 3, 10, 2 },
		// { 53, 4, 3, 2 }, { 21, 5, 3, 2 }, { 64, 4, 8, 2 },
		// { 50, 4, 8, 2 }, { 53, 4, 6, 2 }, { 50, 7, 3, 2 },
		// { 19, 4, 5, 2 }, { 52, 3, 6, 2 }, { 47, 4, 7, 2 },
		// { 65, 4, 11, 2 }, { 62, 3, 3, 2 }, { 46, 4, 10, 2 } };
		// int[][] testParamsFAOriginal = { { 48, 5, 3, 2 }, { 42, 4, 9, 2 },
		// { 49, 4, 8, 2 }, { 44, 5, 3, 2 }, { 47, 7, 3, 2 },
		// { 48, 5, 5, 2 }, { 29, 5, 4, 2 }, { 48, 5, 3, 2 },
		// { 49, 6, 3, 2 }, { 35, 3, 11, 2 }, { 44, 4, 7, 2 },
		// { 42, 3, 10, 2 }, { 47, 3, 4, 2 }, { 46, 4, 10, 2 } };
		// int[][] testParamsFAOriginal = { { 31, 3, 7, 2 }, { 40, 6, 3, 2 },
		// { 36, 3, 4, 2 }, { 38, 5, 3, 2 }, { 41, 4, 3, 2 },
		// { 32, 3, 12, 2 }, { 40, 5, 3, 2 }, { 35, 3, 11, 2 },
		// { 19, 4, 3, 2 }, { 36, 3, 11, 2 }, { 18, 3, 4, 2 },
		// { 16, 3, 8, 2 }, { 38, 3, 10, 2 }, { 38, 5, 3, 2 }, };

		// int[][] testParams = { { 13, 3, 11, 1 }, { 28, 7, 3, 1 },
		// { 47, 3, 7, 1 }, { 42, 6, 6, 1 } };
		// int[][] testParamsFF = { { 35, 4, 13, 2 }, { 36, 3, 5, 2 },
		// { 35, 5, 4, 1 }, { 35, 5, 5, 2 } };

		// int[][] testParamsCF = { { 51, 15, 9, 2 }, { 51, 15, 9, 2 } };

		// int[][] testParams = { { 53, 3, 5, 1 }, { 10, 4, 5, 1 },
		// { 13, 5, 3, 1 }, { 30, 5, 3, 1 }, { 30, 5, 3, 1 },
		// { 36, 4, 3, 1 }, { 11, 5, 4, 1 }, { 11, 3, 11, 1 },
		// { 34, 3, 9, 1 }, { 17, 5, 3, 1 }, { 18, 3, 3, 1 },
		// { 18, 3, 10, 1 }, { 15, 3, 8, 1 }, { 10, 5, 6, 1 },
		// { 30, 3, 7, 1 } };

		// int[][] testParamsFish = { { 128, 4, 12, 1 }, { 17, 4, 6, 1 },
		// { 56, 9, 8, 1 }, { 76, 5, 4, 1 }, { 61, 15, 4, 1 },
		// { 72, 8, 6, 1 }, { 40, 13, 6, 1 } };
		// int[][] testParamsFish = { { 130, 6, 14, 2 }, { 94, 4, 4, 2 },
		// { 117, 9, 14, 2 }, { 133, 14, 10, 2 }, { 162, 13, 6, 2 },
		// { 129, 11, 9, 2 }, { 133, 9, 10, 2 } };

		// int[][] testParamsFish = { { 100, 12, 4, 2 }, { 94, 4, 4, 2 },
		// { 117, 9, 14, 2 }, { 102, 4, 9, 2 }, { 110, 3, 8, 2 },
		// { 112, 11, 4, 2 }, { 116, 7, 14, 2 } };

		// int[][] testParamsSL = { { 85, 4, 5, 2 }, { 21, 3, 6, 2 },
		// { 92, 3, 7, 2 }, { 35, 4, 4, 2 }, { 29, 5, 5, 2 },
		// { 31, 3, 9, 2 }, { 28, 4, 4, 2 }, { 15, 6, 4, 2 },
		// { 95, 3, 12, 2 }, { 13, 3, 11, 2 }, { 56, 3, 15, 2 },
		// { 27, 7, 3, 2 }, { 26, 4, 5, 2 }, { 68, 3, 15, 2 },
		// { 51, 3, 9, 2 } };
		// int[][] testParamsSLOriginal = { { 49, 7, 4, 2 }, { 44, 5, 5, 2 },
		// { 37, 9, 3, 2 }, { 28, 3, 10, 2 }, { 31, 5, 6, 2 },
		// { 26, 8, 3, 2 }, { 16, 4, 5, 2 }, { 13, 7, 4, 2 },
		// { 34, 4, 14, 2 }, { 15, 14, 3, 2 }, { 22, 5, 3, 2 },
		// { 15, 4, 7, 2 }, { 26, 7, 4, 2 }, { 24, 3, 5, 2 },
		// { 29, 3, 10, 2 } };

		int[][] testParamsCBF = { { 60, 5, 8, 2 }, { 60, 11, 4, 2 }, { 60, 8, 8, 2 } };
		int[][] testParamsBF = { { 99, 5, 3, 2 }, { 145, 3, 4, 2 }, { 148, 3, 14, 2 }, { 147, 3, 13, 2 },
				{ 146, 3, 12, 2 } };

		return testParamsCBF;
	}

	private static ResultParams[] usingSAXParamsBest10(String fileName, int clsNum) {

		String path = "Result/goodParams/";

		// String fileName = "SwedishLeaf";
		// int clsNum = 15;

		// String fileName = "FaceAll";
		// int clsNum = 14;

		// String fileName = "Coffee";
		// int clsNum = 2;

		// String fileName = "OSULeaf";
		// int clsNum = 6;

		// String fileName = "CBF";
		// int clsNum = 3;

		// String fileName = "GunPoint";
		// int clsNum = 2;

		String fullNameSwedish = path + fileName;

		ResultParams[] rlt = new ResultParams[clsNum];
		int i = 0;

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(new File(fullNameSwedish)));

			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.trim().length() == 0) {
					continue;
				}
				String[] split = line.trim().split("\\[|\\]");

				ArrayList<int[]> pramList = new ArrayList<int[]>();
				for (String l : split) {
					if (l.length() < 3)
						continue;

					String[] paramsString = l.split("[,\\s]+|\\s+\\[(.*?)\\]");

					int windowSize = Integer.parseInt(paramsString[0]);
					int paa = Integer.parseInt(paramsString[1]);
					int alphabet = Integer.parseInt(paramsString[2]);
					int strategy = Integer.parseInt(paramsString[3]);

					int[] param = { windowSize, paa, alphabet, strategy };
					pramList.add(param);
				}
				rlt[i] = new ResultParams();
				rlt[i].setPramList(pramList);
				i++;
			}

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rlt;
	}

	private static TopKBestPatterns[] kFoldSample(NumerosityReductionStrategy strategy, String dataName) {

		GCErrorFunction function = new GCErrorFunction(trainData, strategy, giMethod, gcParams);

		double[] realCenter = new double[dimensions];
		for (int i = 0; i < dimensions; i++) {
			realCenter[i] = lowerBounds[i];
		}

		Point startingPoint = Point.at(realCenter);

		int clsNum = trainData.keySet().size();
		minFunctionValuesDouble = function.valueAt(startingPoint);
		if (minFunctionValuesDouble == null) {
			minFunctionValuesDouble = new double[clsNum];
			Arrays.fill(minFunctionValuesDouble, 10);
			function.thisRPatterns = null;
		}

		bestKResults = new TopKBestPatterns[clsNum];

		int count = 0;
		count = count + 1;

		for (int j = 0; j < clsNum; j++) {
			BestCombination bestcombs = new BestCombination(minFunctionValuesDouble[j], lowerBounds,
					function.thisRPatterns);
			bestKResults[j] = new TopKBestPatterns();
			bestKResults[j].getBestKComb().add(bestcombs);

			consoleLogger.debug("iteration: " + count + ", minimal value " + minFunctionValuesDouble[j] + " for class "
					+ (j + 1) + " at " + lowerBounds[0] + ", " + lowerBounds[1] + ", " + lowerBounds[2]);
		}

		// double[] best10Value = new double[10];
		// Arrays.fill(best10Value, 1);

		outer: for (int windSize = lowerBounds[0]; windSize <= upperBounds[0]; windSize += winStep) {
			for (int paaSize = lowerBounds[1]; paaSize <= upperBounds[1]; paaSize += paaStep) {
				for (int alphabetSize = lowerBounds[2]; alphabetSize <= upperBounds[2]; alphabetSize += alphaStep) {
					// outer: for (int windSize = upperBounds[0]; windSize >=
					// lowerBounds[0]; windSize -= winStep) {
					// for (int paaSize = lowerBounds[1]; paaSize <=
					// upperBounds[1]; paaSize += paaStep) {
					// for (int alphabetSize = lowerBounds[2]; alphabetSize <=
					// upperBounds[2]; alphabetSize += alphaStep) {

					// For timing
					long startTime = System.currentTimeMillis();

					// for (int windSize = upperBounds[0]; windSize >=
					// lowerBounds[0]; windSize -= winStep) {
					// for (int paaSize = upperBounds[1]; paaSize >=
					// lowerBounds[1]; paaSize -= paaStep) {
					// for (int alphabetSize = upperBounds[2]; alphabetSize >=
					// lowerBounds[2]; alphabetSize -= alphaStep) {

					double[] candidateP = { (double) windSize, (double) paaSize, (double) alphabetSize };
					Point candiPoint = Point.at(candidateP);

					if (candiPoint.equals(startingPoint)) {
						continue;
					}

					double[] errorValue = function.valueAt(candiPoint);
					// consoleLogger.info("iteration: " + count + " at "
					// + windSize + ", " + paaSize + ", " + alphabetSize);
					if (errorValue == null) {
						continue;
					}

					for (int i = 0; i < errorValue.length; i++) {
						String label = String.valueOf(i + 1);
						// if ((errorValue[i] - minFunctionValuesDouble[i]) >
						// DIFFT) {
						if ((errorValue[i] - minFunctionValuesDouble[i]) >= 0) {
							if (isGoodForTop10(bestKResults[i].getBestKComb(), errorValue[i])) {

								int[] params = { windSize, paaSize, alphabetSize };
								BestCombination bestcombs = new BestCombination(errorValue[i], params,
										function.thisRPatterns);

								replaceWorst(bestKResults[i].getBestKComb(), bestcombs);
							}
						} else {
							minFunctionValuesDouble[i] = errorValue[i];
							int[] params = { windSize, paaSize, alphabetSize };

							BestCombination bestcombs = new BestCombination(minFunctionValuesDouble[i], params,
									function.thisRPatterns);

							replaceWorst(bestKResults[i].getBestKComb(), bestcombs);
							consoleLogger.debug("iteration: " + count + ", minimal value " + minFunctionValuesDouble[i]
									+ " for class " + (i + 1) + " at " + windSize + ", " + paaSize + ", "
									+ alphabetSize);
						}
					}
					count++;

					if (isTopKAllZero(bestKResults)) {
						break outer;
					}

					if (timing) {
						// For timing
						long endTime = System.currentTimeMillis();
						long totalTime = endTime - startTime;
						consoleLogger.debug("One iteration time: " + DataProcessor.millisToShortDHMS(totalTime));
					}
				}
			}
		}

		return bestKResults;

	}

	private static TopKBestPatterns[] kFoldSampleSecond(NumerosityReductionStrategy strategy, String dataName,
                                                        Map<String, List<double[]>> trainDataSecond) {

		GCErrorFunction function = new GCErrorFunction(trainDataSecond, strategy, giMethod, gcParams);

		double[] realCenter = new double[dimensions];
		for (int i = 0; i < dimensions; i++) {
			realCenter[i] = lowerBounds[i];
		}

		Point startingPoint = Point.at(realCenter);

		int clsNum = trainDataSecond.keySet().size();
		double[] minFunctionValuesDouble = function.valueAt(startingPoint);
		if (minFunctionValuesDouble == null) {
			minFunctionValuesDouble = new double[clsNum];
			Arrays.fill(minFunctionValuesDouble, 10);
			function.thisRPatterns = null;
		}

		TopKBestPatterns[] bestKResults = new TopKBestPatterns[clsNum];

		int count = 0;
		count = count + 1;

		for (int j = 0; j < clsNum; j++) {
			BestCombination bestcombs = new BestCombination(minFunctionValuesDouble[j], lowerBounds,
					function.thisRPatterns);
			bestKResults[j] = new TopKBestPatterns();
			bestKResults[j].getBestKComb().add(bestcombs);
		}

		// double[] best10Value = new double[10];
		// Arrays.fill(best10Value, 1);

		outer: for (int windSize = lowerBounds[0]; windSize <= upperBounds[0]; windSize += winStep) {
			for (int paaSize = lowerBounds[1]; paaSize <= upperBounds[1]; paaSize += paaStep) {
				for (int alphabetSize = lowerBounds[2]; alphabetSize <= upperBounds[2]; alphabetSize += alphaStep) {

					double[] candidateP = { (double) windSize, (double) paaSize, (double) alphabetSize };
					Point candiPoint = Point.at(candidateP);
					if (candiPoint.equals(startingPoint)) {
						continue;
					}

					double[] errorValue = function.valueAt(candiPoint);
					if (errorValue == null) {
						continue;
					}

					for (int i = 0; i < errorValue.length; i++) {
						String label = String.valueOf(i + 1);
						if ((errorValue[i] - minFunctionValuesDouble[i]) >= 0) {
							if (isGoodForTop10(bestKResults[i].getBestKComb(), errorValue[i])) {

								int[] params = { windSize, paaSize, alphabetSize };
								BestCombination bestcombs = new BestCombination(errorValue[i], params,
										function.thisRPatterns);

								replaceWorst(bestKResults[i].getBestKComb(), bestcombs);
							}
						} else {
							minFunctionValuesDouble[i] = errorValue[i];
							int[] params = { windSize, paaSize, alphabetSize };

							BestCombination bestcombs = new BestCombination(minFunctionValuesDouble[i], params,
									function.thisRPatterns);

							replaceWorst(bestKResults[i].getBestKComb(), bestcombs);
							consoleLogger.debug("iteration: " + count + ", minimal value " + minFunctionValuesDouble[i]
									+ " for class " + (i + 1) + " at " + windSize + ", " + paaSize + ", "
									+ alphabetSize);

						}
					}
					count++;

					if (isTopKAllZero(bestKResults)) {
						break outer;
					}
				}
			}
		}

		return bestKResults;

	}

	private static Boolean isTopKAllZero(TopKBestPatterns[] bestKResults) {

		if (bestKResults[0].getBestKComb().size() < bestK) {
			return false;
		}

		for (int i = 0; i < bestKResults.length; i++) {
			TopKBestPatterns tkbpi = bestKResults[i];

			ArrayList<BestCombination> topkBest = tkbpi.getBestKComb();

			for (BestCombination bc : topkBest) {
				if (bc.getMinimalError() > 0)
					return false;
			}
		}

		return true;
	}

	private static boolean isGoodForTop10(ArrayList<BestCombination> bestKComb, double d) {

		if (bestKComb.size() < bestK)
			return true;

		for (BestCombination existB : bestKComb) {
			if (existB.getMinimalError() > d)
				return true;
		}

		return false;
	}

	private static void replaceWorst(ArrayList<BestCombination> top10Best, BestCombination b) {

		if (top10Best.size() < bestK) {

			top10Best.add(b);
			return;
		}

		double maxError = 0;
		int worstIdx = 0;
		for (int idx = 0; idx < top10Best.size(); idx++) {
			BestCombination existB = top10Best.get(idx);
			if (maxError < existB.getMinimalError()) {
				worstIdx = idx;
				maxError = existB.getMinimalError();
			}
		}
		top10Best.remove(worstIdx);
		top10Best.add(b);
	}

	private static Boolean isNewSTDBetter(TSPatterns tsps, TSPatterns tspsNew, String clsLabel) {
		TSProcessor tsp = new TSProcessor();
		double std = 0;
		for (TSPattern tp : tsps.getPatterns()) {
			double[] ts = tp.getPatternTS();
			std += tsp.stDev(ts);
		}

		double stdNew = 0;
		for (TSPattern tp : tspsNew.getPatterns()) {
			double[] tsNew = tp.getPatternTS();
			stdNew += tsp.stDev(tsNew);
		}

		if (std < stdNew) {
			return true;
		}
		return false;
	}

	private static ResultParams[] sample(NumerosityReductionStrategy strategy, String dataName) {

		kFoldSample(strategy, dataName);

		int clsNum = trainData.keySet().size();
		// int rlt[][] = new int[clsNum][];
		ResultParams[] rlt = new ResultParams[clsNum];

		for (int j = 0; j < clsNum; j++) {
			// DataProcessor.writeTopPatterns(bestcombs[j].getThisRPatterns(),
			// j,
			// dataName);

			ArrayList<BestCombination> bestcombs = bestKResults[j].getBestKComb();

			StringBuffer sb = new StringBuffer();

			sb.append("For class ").append(j + 1).append(": ");

			ArrayList<int[]> reses = new ArrayList<int[]>();
			for (BestCombination b : bestcombs) {

				double minV = b.getMinimalError();
				sb.append("min CV error ").append(fmt.format(minV)).append(" reached at ");

				int[] bestParams = b.getBestParams();
				sb.append(Arrays.toString(bestParams)).append(COMMA);

				int[] res = Arrays.copyOf(bestParams, 4);
				res[3] = strategy.index();

				reses.add(res);
				// rlt[j] = res;
			}
			rlt[j] = new ResultParams();
			rlt[j].setPramList(reses);

			consoleLogger.debug(sb.toString());
		}

		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i = 0; i < rlt.length; i++) {
			ResultParams iPram = rlt[i];
			for (int[] res : iPram.getPramList()) {
				sb.append(Arrays.toString(res));
			}
			sb.append("\n");
		}
		consoleLogger.info(sb.toString());

		return rlt;
	}

	private static TopKBestPatterns[] sampleSecond(NumerosityReductionStrategy strategy, String dataName,
                                                   ArrayList<String> secondC) {

		Map<String, List<double[]>> trainDataSecond = new HashMap<String, List<double[]>>();
		int idxRefine = 1;
		for (String c : secondC) {
			trainDataSecond.put(String.valueOf(idxRefine), trainData.get(c));
			idxRefine++;
		}

		TopKBestPatterns[] bestKResults = kFoldSampleSecond(strategy, dataName, trainDataSecond);

		int clsNum = trainDataSecond.keySet().size();
		// int rlt[][] = new int[clsNum][];
		ResultParams[] rlt = new ResultParams[clsNum];

		for (int j = 0; j < clsNum; j++) {
			// DataProcessor.writeTopPatterns(bestcombs[j].getThisRPatterns(),
			// j,
			// dataName);

			ArrayList<BestCombination> bestcombs = bestKResults[j].getBestKComb();

			StringBuffer sb = new StringBuffer();

			sb.append("For class ").append(j + 1).append(": ");

			ArrayList<int[]> reses = new ArrayList<int[]>();
			for (BestCombination b : bestcombs) {

				double minV = b.getMinimalError();
				sb.append("min CV error ").append(fmt.format(minV)).append(" reached at ");

				int[] bestParams = b.getBestParams();
				sb.append(Arrays.toString(bestParams)).append(COMMA);

				int[] res = Arrays.copyOf(bestParams, 4);
				res[3] = strategy.index();

				reses.add(res);
				// rlt[j] = res;
			}
			rlt[j] = new ResultParams();
			rlt[j].setPramList(reses);

			consoleLogger.debug(sb.toString());
		}

		StringBuffer sb = new StringBuffer();
		sb.append("\n");
		for (int i = 0; i < rlt.length; i++) {
			ResultParams iPram = rlt[i];
			for (int[] res : iPram.getPramList()) {
				sb.append(Arrays.toString(res));
			}
			sb.append("\n");
		}
		consoleLogger.debug(sb.toString());

		return bestKResults;
	}

	private static void readData() throws IOException {
		Map<String, List<double[]>> trainD = UCRUtils.readUCRData(TRAINING_DATA);
		Map<String, List<double[]>> testD = UCRUtils.readUCRData(TEST_DATA);
		trainData = new HashMap<String, List<double[]>>();
		forRNNData = new HashMap<String, List<double[]>>();
		validateData = new HashMap<String, List<double[]>>();
		testData = new HashMap<String, List<double[]>>();

		for (Entry<String, List<double[]>> e : trainD.entrySet()) {
			String label = e.getKey();
			List<double[]> tses1 = e.getValue();
			List<double[]> tses2 = testD.get(label);

			tses1.addAll(tses2);

			int totalSize = tses1.size();

			int validateSize = totalSize / 2;
			int trainSize = validateSize / 2;
			int testSize = totalSize - trainSize - validateSize;

			trainData.put(label, tses1.subList(validateSize + testSize, totalSize));
			validateData.put(label, tses1.subList(0, validateSize));
			testData.put(label, tses1.subList(validateSize, validateSize + testSize));

			// trainData.put(label, tses1.subList(0, trainSize));
			// validateData.put(label,
			// tses1.subList(trainSize, trainSize + validateSize));
			// testData.put(label,
			// tses1.subList(trainSize + validateSize, totalSize));
		}
		// forRNNData = validateData;
		for (Entry<String, List<double[]>> vData : validateData.entrySet()) {
			String label = vData.getKey();
			List<double[]> tses = vData.getValue();

			int forRNNSize = tses.size() / 2;
			if (forRNNSize > 10) {
				forRNNSize = 10;
			} else if (forRNNSize < 5) {
				forRNNSize = 5;
			}
			if (forRNNSize > tses.size())
				forRNNSize = tses.size();

			List<double[]> tRNNTS = tses.subList(0, forRNNSize);
			forRNNData.put(label, tRNNTS);
		}
	}

	private static void readDataOriginalSplit() throws IOException {
		testData = new HashMap<String, List<double[]>>();
		testData = UCRUtils.readUCRData(TEST_DATA);

		Map<String, List<double[]>> trainD = UCRUtils.readUCRData(TRAINING_DATA);

		winStep = trainD.get("1").get(0).length * 8 / 100;

		// DataProcessor.writeShiftedData(trainD, "new");
		// DataProcessor.writeShiftedData(testData, "newtest");
		trainData = trainD;
		validateData = trainD;

		// trainData = new HashMap<String, List<double[]>>();
		// forRNNData = new HashMap<String, List<double[]>>();
		// validateData = new HashMap<String, List<double[]>>();
		//
		// for (Entry<String, List<double[]>> e : trainD.entrySet()) {
		// String label = e.getKey();
		// List<double[]> tses = e.getValue();
		//
		// int totalSize = tses.size();
		//
		// // int validateSize = totalSize * 2 / 3;
		// int validateSize = totalSize / 2;
		// int trainSize = totalSize - validateSize;
		//
		// trainData.put(label, tses.subList(validateSize, totalSize));
		// validateData.put(label, tses.subList(0, validateSize));
		// }
		forRNNData = trainD;
		// for (Entry<String, List<double[]>> vData : validateData.entrySet()) {
		// String label = vData.getKey();
		// List<double[]> tses = vData.getValue();
		//
		// int forRNNSize = tses.size() / 2;
		// if (forRNNSize > 10) {
		// forRNNSize = 10;
		// } else if (forRNNSize < 5) {
		// forRNNSize = 5;
		// }
		// if (forRNNSize > tses.size())
		// forRNNSize = tses.size();
		//
		// List<double[]> tRNNTS = tses.subList(0, forRNNSize);
		// forRNNData.put(label, tRNNTS);
		// }
	}

	private static void printDataInfo() {
		consoleLogger.debug("trainData classes: " + trainData.size() + ", series length: "
				+ trainData.entrySet().iterator().next().getValue().get(0).length);
		for (Entry<String, List<double[]>> e : trainData.entrySet()) {
			consoleLogger.debug(" training class: " + e.getKey() + " series: " + e.getValue().size());
		}

		consoleLogger.debug("Validation Data classes: " + validateData.size() + ", series length: "
				+ validateData.entrySet().iterator().next().getValue().get(0).length);
		for (Entry<String, List<double[]>> e : validateData.entrySet()) {
			consoleLogger.debug(" validation class: " + e.getKey() + " series: " + e.getValue().size());
		}

		// consoleLogger
		// .info("For RNN classes: "
		// + forRNNData.size()
		// + ", series length: "
		// + forRNNData.entrySet().iterator().next().getValue()
		// .get(0).length);
		// for (Entry<String, List<double[]>> e : forRNNData.entrySet()) {
		// consoleLogger.info(" forRNNData class: " + e.getKey() + " series: "
		// + e.getValue().size());
		// }

		consoleLogger.debug("testData classes: " + testData.size() + ", series length: "
				+ testData.entrySet().iterator().next().getValue().get(0).length);
		for (Entry<String, List<double[]>> e : testData.entrySet()) {
			consoleLogger.debug(" test class: " + e.getKey() + " series: " + e.getValue().size());
		}
	}

	private static int[] toIntArray(Double[] array) {
		int[] res = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			res[i] = (int) Math.round(array[i]);
		}
		return res;
	}

	/**
	 * Finds all points on the convex hull, even redundant ones.
	 */
	private static double[] conhull(double[] x, double[] y) {
		// System.out.println(Arrays.toString(x) + " : " + Arrays.toString(y));
		int m = x.length;
		double[] h;
		int start = 0, flag = 0, v, w, a, b, c, leftturn, j, k;
		double determinant;
		if (x.length != y.length) {
			System.out.println("Input dimension must agree");
			return null;
		}
		if (m == 2) {
			h = new double[2];
			h[0] = 0;
			h[1] = 1;
			return h;
		}
		if (m == 1) {
			h = new double[1];
			h[0] = 0;
			return h;
		}
		v = start;
		w = x.length - 1;
		h = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			h[i] = i + 1;
		}
		while ((next(v, m) != 0) || (flag == 0)) {
			if (next(v, m) == w) {
				flag = 1;
			}
			// getting three points
			a = v;
			b = next(v, m);
			c = next(next(v, m), m);
			determinant = (x[a] * y[b] * 1) + (x[b] * y[c] * 1) + (x[c] * y[a] * 1) - (1 * y[b] * x[c])
					- (1 * y[c] * x[a]) - (1 * y[a] * x[b]);

			if (determinant >= 0) {
				leftturn = 1;
			} else {
				leftturn = 0;
			}
			if (leftturn == 1) {
				v = next(v, m);
			} else {
				j = next(v, m);
				k = 0;
				double[] x1 = new double[x.length - 1];
				for (int i = 0; i < x1.length; i++) {
					if (j == i) {

						k++;
					}
					x1[i] = x[k];
					k++;
				}
				x = x1;
				k = 0;
				x1 = new double[y.length - 1];
				for (int i = 0; i < x1.length; i++) {
					if (j == i) {

						k++;
					}
					x1[i] = y[k];
					k++;
				}
				y = x1;
				k = 0;
				x1 = new double[h.length - 1];
				for (int i = 0; i < x1.length; i++) {
					if (j == i) {

						k++;
					}
					x1[i] = h[k];
					k++;
				}
				h = x1;
				m = m - 1;
				w = w - 1;
				v = pred(v, m);
			}
		}
		for (int i = 0; i < h.length; i++) {
			h[i] = h[i] - 1;
		}
		return h;
	}

	/**
	 * returns next point if the last then the first
	 * 
	 * @param v
	 * @param m
	 * @return
	 */
	private static int next(int v, int m) {
		if ((v + 1) == m) {
			return 0;
		} else {
			if ((v + 1) < m) {
				return (v + 1);
			} else {
				return -1;
			}
		}
	}

	/**
	 * M is the size, v is the index, returns the previous index value.
	 */
	private static int pred(int idx, int size) {
		if ((idx + 1) == 1) {
			return size - 1;
		} else {
			if ((idx + 1) > 1) {
				return (idx - 1);
			} else {
				return -1;
			}
		}
	}

	/**
	 * returns sorted array and the original indicies
	 * 
	 * @param array
	 * @return
	 */
	private static double[][] sort(double[] array) {
		double[][] arr1 = new double[3][array.length];
		double[][] arr2 = new double[2][array.length];
		System.arraycopy(array, 0, arr1[0], 0, array.length);
		Arrays.sort(array);
		for (int i = 0; i < array.length; i++) {
			for (int i1 = 0; i1 < array.length; i1++) {
				if (array[i] == arr1[0][i1] && arr1[2][i1] != 1) {
					arr1[2][i1] = 1;
					arr1[1][i] = i1;
					break;
				}
			}
		}
		arr2[0] = array;
		arr2[1] = arr1[1];
		return arr2;
	}

	/**
	 * Finds an index and a minimal value of an array.
	 */
	private static double[] minimum(double[] array) {
		Double min = array[0];
		double[] res = { min, 0.0 };
		for (int i = 0; i < array.length; i++) {
			if (min > array[i]) {
				min = array[i];
				res[0] = min;
				res[1] = i;
			}
		}
		return res;
	}

	/**
	 * Finds an index and a minimal value of an array.
	 */
	private static double[] minimum(ArrayList<Double> array) {
		Double min = array.get(0);
		double[] res = { min, 0.0 };
		for (int i = 0; i < array.size(); i++) {
			if (min > array.get(i)) {
				min = array.get(i);
				res[0] = min;
				res[1] = i;
			}
		}
		return res;
	}

	/**
	 * Finds matches.
	 */
	private static Integer[] findMatches(Double[] array, double value) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < array.length; i++) {
			if (Math.abs(array[i] - value) <= precision) {
				res.add(i);
			}
		}
		return res.toArray(new Integer[res.size()]);
	}

	/**
	 * Finds matches.
	 */
	private static Integer[] findMatches(ArrayList<Double> array, double value) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < array.size(); i++) {
			if (Math.abs(array.get(i) - value) <= precision) {
				res.add(i);
			}
		}
		return res.toArray(new Integer[res.size()]);
	}

	/**
	 * Finds array elements that are not equal to the value up to threshold.
	 */
	private static Integer[] findNonMatches(ArrayList<Double> array, double value) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 0; i < array.size(); i++) {
			if (Math.abs(array.get(i) - value) > precision) {
				res.add(i);
			}
		}
		return res.toArray(new Integer[res.size()]);
	}

	/**
	 * Returns arrays intersection.
	 */
	private static Integer[] findArrayIntersection(Integer[] arr1, Integer[] arr2) {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i1 = 0; i1 < arr1.length; i1++) {
			for (int i2 = 0; i2 < arr2.length; i2++) {
				if (arr1[i1] == arr2[i2]) {
					res.add(arr2[i2]);
				}
			}
		}
		return res.toArray(new Integer[res.size()]);
	}

	protected static String toLogStr(int[] p, double accuracy, double error) {

		StringBuffer sb = new StringBuffer();
		if (NumerosityReductionStrategy.MINDIST.index() == p[3]) {
			sb.append("MINDIST, ");
		} else if (NumerosityReductionStrategy.EXACT.index() == p[3]) {
			sb.append("EXACT, ");
		} else if (NumerosityReductionStrategy.NONE.index() == p[3]) {
			sb.append("NOREDUCTION, ");
		}
		sb.append("window ").append(p[0]).append(COMMA);
		sb.append("PAA ").append(p[1]).append(COMMA);
		sb.append("alphabet ").append(p[2]).append(COMMA);
		sb.append(" accuracy ").append(fmt.format(accuracy)).append(COMMA);
		sb.append(" error ").append(fmt.format(error));

		return sb.toString();
	}

}
