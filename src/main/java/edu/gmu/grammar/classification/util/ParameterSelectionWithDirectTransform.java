package edu.gmu.grammar.classification.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.gmu.connectGI.GrammarIndcutionMethod;
import edu.gmu.grammar.patterns.BestSelectedPatterns;
import edu.gmu.ps.direct.GCErrorFunction;
import net.seninp.jmotif.direct.Point;
import net.seninp.jmotif.direct.ValuePointColored;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
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

public class ParameterSelectionWithDirectTransform {

	// the number formatter
	private static final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
	private static DecimalFormat fmt = new DecimalFormat("0.00###", otherSymbols);

	// array with all rectangle centerpoints
	private static ArrayList<Double[]> centerPoints;

	// array with all rectangle side lengths in each dimension
	private static ArrayList<Double[]> lengthsSide;

	// array with distances from center points to the vertices
	private static ArrayList<Double> diagonalLength;

	// array vector of all different distances, sorted
	private static ArrayList<Double> differentDiagonalLength;

	// array vector of minimum function value for each distance
	private static double[] diagonalsMinFunc;

	// array with function values
	private static ArrayList<Double> functionValues;

	// array used to track sampled points and function values
	private static ArrayList<ValuePointColored> coordinates;

	// array with function values
	// private static ArrayList<Double> functionValues;

	// array used to track sampled points and function values
	// private static ArrayList<ValuePointColored> coordinates;
	private static HashMap<String, Double> functionHash;

	private static final double precision = 1E-16;
	private static int b = 0;
	private static double[] resultMinimum;

	private static int sampledPoints;
	private static int rectangleCounter;
	private static int indexPotentialBestRec;
	private static double minFunctionValue;
	// private static double[] minFunctionValuesDouble;

	// init bounds
	//
	private static int dimensions = 3;

	private static GCErrorFunction function;

	// static block - we instantiate the logger
	//
	private static final Logger consoleLogger;
	private static final Level LOGGING_LEVEL = Level.INFO;

	private static final String COMMA = ", ";
	private static final String CR = "\n";

	static {
		consoleLogger = (Logger) LoggerFactory.getLogger(ParameterSelectionWithDirectTransform.class);
		consoleLogger.setLevel(LOGGING_LEVEL);
	}

	// the global minimum point
	private static ValuePointColored minimum = ValuePointColored.at(Point.at(0), Double.POSITIVE_INFINITY, false);

	// private static BestCombination[] bestcombs;
	// private static TopKBestPatterns[] bestKResults;
	private static BestSelectedPatterns bestSelectedPatterns;

	private static int[] upperBounds;
	private static int[] lowerBounds;
	private static String TRAINING_DATA;
	private static String TEST_DATA;
	private static int HOLD_OUT_NUM = 1;
	private static int ITERATIONS_NUM = 20;

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
			usingDefault = false;
			// For timing
			long startTime = System.currentTimeMillis();

			// Main method
			String dataName = args[0];
			String[] initialParams = new String[10];
			System.arraycopy(args, 1, initialParams, 0, 8);
			initialParams[8] = "1";

			allStrategy = NumerosityReductionStrategy.valueOf(args[9]);
			if (args[10].endsWith("1")) {
				isSecondRefine = true;
			}

			runAndTime(initialParams, dataName);

			// For timing
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			printResultInfo(totalTime, dataName);
		} else if (args.length == 4) {
			usingDefault = true;

			// For timing
			long startTime = System.currentTimeMillis();

			// Main method
			String dataName = args[0];
			String[] initialParams = new String[10];
			System.arraycopy(args, 1, initialParams, 0, 2);
			allStrategy = NumerosityReductionStrategy.valueOf(args[3]);

			// minimal window size
			initialParams[2] = "10";
			// maximal window size
			initialParams[3] = "100";
			// minimal paa size
			initialParams[4] = "2";
			// maximal paa size
			initialParams[5] = "20";
			// minimal alphabet size
			initialParams[6] = "2";
			// maximal alphabet size
			initialParams[7] = "20";

			initialParams[8] = "1";
			initialParams[9] = "10";

			// allStrategy = NumerosityReductionStrategy.EXACT;
			isSecondRefine = true;

			runAndTime(initialParams, dataName);

			// For timing
			long endTime = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			printResultInfo(totalTime, dataName);
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
			initialParams[4] = "2";
			// maximal paa size
			initialParams[5] = "20";
			// minimal alphabet size
			initialParams[6] = "2";
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

			printResultInfo(totalTime, dataName);
		}

		else {
			System.out.print(printHelp());
			System.exit(-10);
		}

	}

	public static void printResultInfo(long totalTime, String dataName) {

		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date date = new Date();
		StringBuilder sb = new StringBuilder();
		// sb.append("Total runnig time: ");
		// sb.append(DataProcessor.millisToShortDHMS(totalTime));

		sb.append("Running time for data ");
		sb.append(dataName);
		sb.append(": ");
		sb.append(DataProcessor.millisToShortDHMS(totalTime));
		sb.append("\n");
		sb.append("Current date: ");
		sb.append(dateFormat.format(date));
		consoleLogger.info(sb.toString());
		DataProcessor.writeClassificationRlt(sb.toString());
	}

	public static void runAndTime(String[] initialParam, String display) throws IndexOutOfBoundsException, IOException {
		// // For timing
		// long startTime = System.currentTimeMillis();

		// Main method
		runParamSelect(initialParam, display);

		// // For timing
		// // consoleLogger.info("End of " + display);
		// long endTime = System.currentTimeMillis();
		// long totalTime = endTime - startTime;
		// consoleLogger.info("Running time for data " + display + ": "
		// + DataProcessor.millisToShortDHMS(totalTime));
	}

	public static void runParamSelect(String[] args, String dataName) throws IOException, IndexOutOfBoundsException {

		try {

			if (10 == args.length) {
				TRAINING_DATA = args[0];
				TEST_DATA = args[1];

				// readData();
				readDataOriginalSplit();
				printDataInfo();

				// DataProcessor.classifyTestingVarTransformedTT(trainData,
				// testData, gcParams);

				int lbwindow = Integer.valueOf(args[2]).intValue();
				int ubwindow = Integer.valueOf(args[3]).intValue();

				if (usingDefault) {
					int tsLen = trainData.entrySet().iterator().next().getValue().get(0).length;
					int tenPer = (int) (tsLen * 0.1);
					int lb = tenPer > 1 ? tenPer : 1;
					int fiftyPer = (int) (tsLen * 0.9);
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

			// ArrayList<ArrayList<String>> secondCLasses = new
			// ArrayList<ArrayList<String>>();
			// ArrayList<TopKBestPatterns[]> secondBests = new
			// ArrayList<TopKBestPatterns[]>();

			// long startTime = System.currentTimeMillis();
			// NumerosityReductionStrategy ex =
			// NumerosityReductionStrategy.EXACT;
			consoleLogger.info("running sampling for " + giMethod + " with " + allStrategy.toString() + " strategy..."
					+ "Step size: " + "{" + winStep + ", " + paaStep + ", " + alphaStep + "}");
			sample(allStrategy, dataName);

			// if (isSecondRefine) {
			// RefineBySecondParams r = new RefineBySecondParams(trainData,
			// allStrategy, giMethod, gcParams);
			// secondCLasses = r.findSecondParamsByClassifyTrain(bestKResults,
			// dataName);
			//
			// secondCLasses = new ArrayList<ArrayList<String>>();
			// secondBests = new ArrayList<TopKBestPatterns[]>();
			// for (ArrayList<String> secondC : secondCLasses) {
			// // TopKBestPatterns[] secondBestKParams = sampleSecond(
			// // allStrategy, dataName, secondC);
			// //
			// // secondBests.add(secondBestKParams);
			// }
			// }
			if (existSAXParam) {
				// classifyWithExistSAXParamsBest10(args, dataName, bestKParams,
				// secondCLasses, secondBests);
			} else {
				classifyWithTransformedData(args, dataName, bestSelectedPatterns);
			}
			// For timing
			// long endTime = System.currentTimeMillis();
			// long totalTime = endTime - startTime;
			// consoleLogger.info("Running time for strategy " + ex.toString() +
			// ": "
			// + DataProcessor.millisToShortDHMS(totalTime));

			// ------------------------------------------------------------------------------
			// startTime = System.currentTimeMillis();
			// NumerosityReductionStrategy mind =
			// NumerosityReductionStrategy.MINDIST;
			// consoleLogger.info("running sampling for " + giMethod + " with "
			// + mind.toString() + " strategy..." + "Step size: " + "{"
			// + winStep + ", " + paaStep + ", " + alphaStep + "}");
			// bestKParams = sample(mind, dataName);
			//
			// if (isSecondRefine) {
			// RefineBySecondParams r = new RefineBySecondParams(trainData,
			// allStrategy, giMethod, gcParams);
			// secondCLasses = r.findSecondParamsByClassifyTrain(bestKResults,
			// dataName);
			//
			// secondCLasses = new ArrayList<ArrayList<String>>();
			// secondBests = new ArrayList<TopKBestPatterns[]>();
			// for (ArrayList<String> secondC : secondCLasses) {
			// // TopKBestPatterns[] secondBestKParams = sampleSecond(
			// // allStrategy, dataName, secondC);
			// //
			// // secondBests.add(secondBestKParams);
			// }
			// }
			// if (existSAXParam) {
			// // classifyWithExistSAXParamsBest10(args, dataName, bestKParams,
			// // secondCLasses, secondBests);
			// } else {
			// classifyWithExistSAXParamsBest10NotRead(args, dataName,
			// bestKResults, secondCLasses, secondBests);
			// }
			// endTime = System.currentTimeMillis();
			// totalTime = endTime - startTime;
			// consoleLogger.info("Running time for strategy " + mind.toString()
			// + ": "
			// + DataProcessor.millisToShortDHMS(totalTime));

			// //------------------------------------------------------------------------------
			// startTime = System.currentTimeMillis();
			// NumerosityReductionStrategy none =
			// NumerosityReductionStrategy.NONE;
			// consoleLogger.info("running sampling for " + giMethod + " with "
			// + none.toString() + " strategy..." + "Step size: " + "{"
			// + winStep + ", " + paaStep + ", " + alphaStep + "}");
			// bestKParams = sample(none, dataName);
			//
			// if (isSecondRefine) {
			// RefineBySecondParams r = new RefineBySecondParams(trainData,
			// allStrategy, giMethod, gcParams);
			// secondCLasses = r.findSecondParamsByClassifyTrain(bestKResults,
			// dataName);
			//
			// secondCLasses = new ArrayList<ArrayList<String>>();
			// secondBests = new ArrayList<TopKBestPatterns[]>();
			// for (ArrayList<String> secondC : secondCLasses) {
			// // TopKBestPatterns[] secondBestKParams = sampleSecond(
			// // allStrategy, dataName, secondC);
			// //
			// // secondBests.add(secondBestKParams);
			// }
			// }
			// if (existSAXParam) {
			// // classifyWithExistSAXParamsBest10(args, dataName, bestKParams,
			// // secondCLasses, secondBests);
			// } else {
			// classifyWithExistSAXParamsBest10NotRead(args, dataName,
			// bestKResults, secondCLasses, secondBests);
			// }
			// endTime = System.currentTimeMillis();
			// totalTime = endTime - startTime;
			// consoleLogger.info("Running time for strategy " + none.toString()
			// + ": "
			// + DataProcessor.millisToShortDHMS(totalTime));
		}
	}

	private static void classifyWithTransformedData(String[] args, String dataName,
			BestSelectedPatterns bestSelectedPatterns) throws IndexOutOfBoundsException {

		// Using new classification method on regular data.
		DataProcessor.classifyTestingVarTransformed(bestSelectedPatterns, dataName, args, trainData, testData,
				validateData, forRNNData, giMethod, gcParams);

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

	private static void update() {
		resultMinimum = minimum(functionValues);
		// getting minimum and giving it at last points
		minFunctionValue = resultMinimum[0];
		minimum.setBest(false);
		minimum = ValuePointColored.at(Point.at(0), Double.POSITIVE_INFINITY, false);
		int i = 0;
		for (ValuePointColored valuePoint : coordinates) {
			if (valuePoint.getValue() < minimum.getValue()) {
				b = i;
				minimum = valuePoint;
			}
			i++;
		}
		minimum.setBest(true);
		// TODO: a bug?
		// coordinates.remove(b);
		// coordinates.add(minimum);
		coordinates.set(b, minimum);
		double epsilon = 1E-4;
		double e = Math.max(epsilon * Math.abs(minFunctionValue), 1E-8);
		double[] temporaryArray = new double[functionValues.size()];
		for (int i2 = 0; i2 < functionValues.size(); i2++) {
			temporaryArray[i2] = (functionValues.get(i2) - minFunctionValue + e) / diagonalLength.get(i2);
		}
		indexPotentialBestRec = (int) minimum(temporaryArray)[1];

		differentDiagonalLength = diagonalLength;
		int i1 = 0;
		while (true) {
			double diagonalTmp = differentDiagonalLength.get(i1);
			Integer[] indx = findNonMatches(differentDiagonalLength, diagonalTmp);
			ArrayList<Double> diagonalCopy = differentDiagonalLength;
			differentDiagonalLength = new ArrayList<Double>();
			differentDiagonalLength.add(diagonalTmp);

			for (int i2 = 1; i2 < indx.length + 1; i2++) {
				differentDiagonalLength.add(diagonalCopy.get(indx[i2 - 1]));
			}
			if (i1 + 1 == differentDiagonalLength.size()) {
				break;
			} else {
				i1++;
			}
		}
		Collections.sort(differentDiagonalLength);
		diagonalsMinFunc = new double[differentDiagonalLength.size()];
		for (i1 = 0; i1 < differentDiagonalLength.size(); i1++) {
			Integer[] indx1 = findMatches(diagonalLength, differentDiagonalLength.get(i1));
			ArrayList<Double> fTmp = new ArrayList<Double>();
			for (int i2 = 0; i2 < indx1.length; i2++) {
				fTmp.add(functionValues.get(indx1[i2]));
			}
			diagonalsMinFunc[i1] = minimum(fTmp)[0];
		}
	}

	private static BestSelectedPatterns kFoldSample(NumerosityReductionStrategy strategy, String dataName) {

		// Initializing global varibales.
		function = new GCErrorFunction(trainData, strategy, giMethod, gcParams);
		centerPoints = new ArrayList<Double[]>();
		lengthsSide = new ArrayList<Double[]>();
		diagonalLength = new ArrayList<Double>();
		differentDiagonalLength = new ArrayList<Double>();
		diagonalsMinFunc = new double[1];
		functionValues = new ArrayList<Double>();
		coordinates = new ArrayList<ValuePointColored>();

		sampledPoints = 0;
		rectangleCounter = 1;
		indexPotentialBestRec = 0;
		// The error of all class
		minFunctionValue = 0;

		Double[] scaledCenter = new Double[dimensions];
		double[] realCenter = new double[dimensions];
		Double[] lTmp = new Double[dimensions];
		Double dTmp = 0.0;
		Double[] cooTmp = new Double[dimensions];

		for (int i = 0; i < dimensions; i++) {
			scaledCenter[i] = 0.5;
			lTmp[i] = 0.5;
			dTmp = dTmp + scaledCenter[i] * scaledCenter[i];
			realCenter[i] = lowerBounds[i] + scaledCenter[i] * (upperBounds[i] - lowerBounds[i]);
		}
		centerPoints.add(scaledCenter);
		lengthsSide.add(lTmp);
		dTmp = Math.sqrt(dTmp);
		diagonalLength.add(dTmp);
		Point startingPoint = Point.at(realCenter);

		int clsNum = trainData.keySet().size();

		double classifyError = function.valueAtTransform(startingPoint);

		if (classifyError == 1) {
			function.selectedRepresentativePatterns = null;
			minFunctionValue = 1.0d;
		} else {
			minFunctionValue = classifyError;
		}

		sampledPoints = sampledPoints + 1;
		for (int i1 = 0; i1 < dimensions; i1++) {
			cooTmp[i1] = realCenter[i1];
		}

		// bestKResults = new TopKBestPatterns[clsNum];
		int count = 0;
		count = count + 1;

		double[] startingCoords = startingPoint.toArray();
		int windowSize = Long.valueOf(Math.round(startingCoords[0])).intValue();
		int paaSize = Long.valueOf(Math.round(startingCoords[1])).intValue();
		int alphabetSize = Long.valueOf(Math.round(startingCoords[2])).intValue();
		int[] startParams = { windowSize, paaSize, alphabetSize };

		bestSelectedPatterns = new BestSelectedPatterns(minFunctionValue, startParams,
				function.selectedRepresentativePatterns);

		minimum = ValuePointColored.at(startingPoint, minFunctionValue, true);
		coordinates.add(minimum);
		diagonalsMinFunc[0] = minFunctionValue;
		functionValues.add(minFunctionValue);
		differentDiagonalLength = diagonalLength;

		ArrayList<Integer> potentiallyOptimalRectangles = null;

		// optimization loop
		//
		for (int ctr = 0; ctr < ITERATIONS_NUM; ctr++) {
			// The minimal error and its index.
			resultMinimum = minimum(functionValues);
			double[] params = coordinates.get((int) resultMinimum[1]).getPoint().toArray();
			consoleLogger.debug("iteration: " + ctr + ", minimal value " + resultMinimum[0] + " at " + params[0] + ", "
					+ params[1] + ", " + params[2]);
			// System.out.println(resultMinimum[0] + ","+params[0] + "," +
			// params[1] + ", " + params[2]);
			potentiallyOptimalRectangles = identifyPotentiallyRec();
			// For each potentially optimal rectangle
			for (int jj = 0; jj < potentiallyOptimalRectangles.size(); jj++) {
				int j = potentiallyOptimalRectangles.get(jj);
				samplingPotentialRec(j);
			}
			update();
		}

		return bestSelectedPatterns;

	}

	private static void updateBest(double errorValue, Point point) {

		double[] coords = point.toArray();
		int windowSize = Long.valueOf(Math.round(coords[0])).intValue();
		int paaSize = Long.valueOf(Math.round(coords[1])).intValue();
		int alphabetSize = Long.valueOf(Math.round(coords[2])).intValue();

		if (errorValue < bestSelectedPatterns.getMinimalError()) {
			int[] params = { windowSize, paaSize, alphabetSize };

			bestSelectedPatterns.setMinimalError(errorValue);
			bestSelectedPatterns.setBestParams(params);
			bestSelectedPatterns.setBestSelectedPatterns(function.selectedRepresentativePatterns);
		}
	}

	/**
	 * Determine where to sample within rectangle j and how to divide the
	 * rectangle into subrectangles. Update minFunctionValue and set
	 * m=m+delta_m, where delta_m is the number of new points sampled.
	 * 
	 * @param j
	 */
	private static void samplingPotentialRec(int j) {

		double max_L = lengthsSide.get(j)[0], delta;
		Integer[] maxSideLengths;

		// get the longest side
		//
		for (int i1 = 0; i1 < lengthsSide.get(j).length; i1++) {
			max_L = Math.max(max_L, lengthsSide.get(j)[i1]);
		}

		// Identify the array maxSideLengths of dimensions with the maximum side
		// length.
		//
		maxSideLengths = findMatches(lengthsSide.get(j), max_L);
		delta = 2 * max_L / 3;
		double[] w = new double[0];
		double i1;
		double[] e_i;

		// Sample the function at the points c +- delta*e_i for all ii in
		// maxSideLengths.
		for (int ii = 0; ii < maxSideLengths.length; ii++) {
			Double[] c_m1 = new Double[dimensions];
			double[] x_m1 = new double[dimensions];
			Double[] c_m2 = new Double[dimensions];
			double[] x_m2 = new double[dimensions];
			i1 = maxSideLengths[ii];
			e_i = new double[dimensions];
			e_i[(int) i1] = 1;

			// Center point for a new rectangle
			//
			for (int i2 = 0; i2 < centerPoints.get(j).length; i2++) {
				c_m1[i2] = centerPoints.get(j)[i2] + delta * e_i[i2];
			}
			// Transform c_m1 to original search space
			for (int i2 = 0; i2 < c_m1.length; i2++) {
				x_m1[i2] = lowerBounds[i2] + c_m1[i2] * (upperBounds[i2] - lowerBounds[i2]);
			}
			// Function value at x_m1
			Point pointToSample1 = Point.at(x_m1);

			Double f_m1;
			double classifyError1 = function.valueAtTransform(pointToSample1);
			if (classifyError1 == 1) {
				f_m1 = 1.0d;
			} else {
				f_m1 = classifyError1;
				updateBest(f_m1, pointToSample1);
			}

			// TODO: here needs to be a check
			// Double f_m1 = function.valueAt(pointToSample1);
			consoleLogger.debug("@" + f_m1 + "\t" + pointToSample1.toLogString());

			// add to all points
			coordinates.add(ValuePointColored.at(pointToSample1, f_m1, false));
			sampledPoints = sampledPoints + 1;

			// Center point for a new rectangle
			//
			for (int i2 = 0; i2 < centerPoints.get(j).length; i2++) {
				c_m2[i2] = centerPoints.get(j)[i2] - delta * e_i[i2];
			}
			// Transform c_m2 to original search space
			for (int i2 = 0; i2 < c_m2.length; i2++) {
				x_m2[i2] = lowerBounds[i2] + c_m2[i2] * (upperBounds[i2] - lowerBounds[i2]);
			}
			// Function value at x_m2
			Point pointToSample2 = Point.at(x_m2);

			Double f_m2;
			double classifyError2 = function.valueAtTransform(pointToSample2);
			if (classifyError2 == 1) {
				f_m2 = 1.0d;
			} else {
				f_m2 = classifyError2;
				updateBest(f_m2, pointToSample2);
			}
			// TODO: here needs to be a check
			consoleLogger.debug("@" + f_m2 + "\t" + pointToSample2.toLogString());

			// add to all points
			coordinates.add(ValuePointColored.at(pointToSample2, f_m2, false));
			sampledPoints = sampledPoints + 1;

			double[] w_pom;
			w_pom = w;
			w = new double[ii + 1];
			System.arraycopy(w_pom, 0, w, 0, w_pom.length);
			w[ii] = Math.min(f_m2, f_m1);

			centerPoints.add(c_m1);
			centerPoints.add(c_m2);
			functionValues.add(f_m1);
			functionValues.add(f_m2);

			// System.out.println(Arrays.toString(x_m1) + ", " + f_m1);
			// System.out.println(Arrays.toString(x_m2) + ", " + f_m2);
		}

		devideRec(w, maxSideLengths, delta, j);

	}

	/**
	 * Divide the rectangle containing centerPoints.get(j) into thirds along the
	 * dimension in maxSideLengths, starting with the dimension with the lowest
	 * value of w[ii]
	 * 
	 * @param w
	 * @param maxSideLengths
	 * @param delta
	 * @param j
	 */
	private static void devideRec(double[] w, Integer[] maxSideLengths, double delta, int j) {

		double[][] ab = sort(w);

		for (int ii = 0; ii < maxSideLengths.length; ii++) {
			int i1 = maxSideLengths[(int) ab[1][ii]];
			int index1 = rectangleCounter + 2 * (int) ab[1][ii]; // Index for
																	// new
																	// rectangle
			int index2 = rectangleCounter + 2 * (int) ab[1][ii] + 1; // Index
																		// for
																		// new
																		// rectangle
			lengthsSide.get(j)[i1] = delta / 2;
			int index = 0;
			if (index2 + 1 > index1 + 1) {
				index = index2 + 1;
			} else {
				index = index1 + 1;
			}

			Double[] lTmp = new Double[dimensions];
			Double[] lTmp2 = new Double[dimensions];
			for (int i2 = 0; i2 < lengthsSide.get(0).length; i2++) {
				lTmp[i2] = lengthsSide.get(j)[i2];
				lTmp2[i2] = lengthsSide.get(j)[i2];
			}
			if (index == lengthsSide.size() + 2) {
				lengthsSide.add(lTmp);
				lengthsSide.add(lTmp2);
			} else {
				Double[] lTmp3;
				int lengthsSize = lengthsSide.size();
				for (int i2 = 0; i2 < index - lengthsSize; i2++) {
					lTmp3 = new Double[dimensions];
					lengthsSide.add(lTmp3);
				}
				lengthsSide.set(index1, lTmp);
				lengthsSide.set(index2, lTmp2);
			}

			diagonalLength.set(j, 0.0);
			Double dTmp;
			for (int i2 = 0; i2 < lengthsSide.get(j).length; i2++) {
				dTmp = diagonalLength.get(j) + lengthsSide.get(j)[i2] * lengthsSide.get(j)[i2];
				diagonalLength.set(j, dTmp);
			}
			diagonalLength.set(j, Math.sqrt(diagonalLength.get(j)));
			dTmp = diagonalLength.get(j);
			Double d_kop2 = diagonalLength.get(j);
			if (index == diagonalLength.size() + 2) {
				diagonalLength.add(dTmp);
				diagonalLength.add(d_kop2);
			} else {
				Double dTmp3;
				int size = diagonalLength.size();
				for (int i2 = 0; i2 < index - size; i2++) {
					dTmp3 = 0.0;
					diagonalLength.add(dTmp3);
				}
				diagonalLength.set(index1, diagonalLength.get(j));
				diagonalLength.set(index2, diagonalLength.get(j));
			}
		}
		rectangleCounter = rectangleCounter + 2 * maxSideLengths.length;
	}

	/**
	 * Identify the set of all potentially optimal rectangles.
	 */
	private static ArrayList<Integer> identifyPotentiallyRec() {

		double localPrecision = 1E-12;

		// find rectangles with the same diagonal
		//
		Integer[] sameDiagonalIdxs = findMatches(differentDiagonalLength, diagonalLength.get(indexPotentialBestRec));

		ArrayList<Integer> s_1 = new ArrayList<Integer>();
		for (int i = sameDiagonalIdxs[0]; i < differentDiagonalLength.size(); i++) {
			Integer[] indx3 = findMatches(functionValues, diagonalsMinFunc[i]);
			Integer[] indx4 = findMatches(diagonalLength, differentDiagonalLength.get(i));
			Integer[] idx2 = findArrayIntersection(indx3, indx4);
			s_1.addAll(Arrays.asList(idx2));
		}

		// s_1 now includes all rectangles i, with diagonals[i] >=
		// diagonals(indexPotentialBestRec)
		//
		ArrayList<Integer> s_2 = new ArrayList<Integer>();
		ArrayList<Integer> s_3 = new ArrayList<Integer>();
		if (differentDiagonalLength.size() - sameDiagonalIdxs[0] > 2) {

			double a1 = diagonalLength.get(indexPotentialBestRec),
					a2 = differentDiagonalLength.get(differentDiagonalLength.size() - 1),
					b1 = functionValues.get(indexPotentialBestRec),
					b2 = diagonalsMinFunc[differentDiagonalLength.size() - 1];

			// The line is defined by: y = slope*x + const
			double slope = (b2 - b1) / (a2 - a1);
			double consta = b1 - slope * a1;

			for (int i1 = 0; i1 < s_1.size(); i1++) {
				int j = s_1.get(i1).intValue();
				if (functionValues.get(j) <= slope * diagonalLength.get(j) + consta + localPrecision) {
					s_2.add(j);
				}
			}

			if (0 == s_2.size()) {
				return s_1;
			}

			// s_2 now contains all points in S_1 which lie on or below the line
			// Find the points on the convex hull defined by the points in s_2
			double[] xx = new double[s_2.size()];
			double[] yy = new double[s_2.size()];
			for (int i1 = 0; i1 < xx.length; i1++) {
				xx[i1] = diagonalLength.get(s_2.get(i1).intValue());
				yy[i1] = functionValues.get(s_2.get(i1).intValue());
			}
			double[] h = conhull(xx, yy);
			for (int i1 = 0; i1 < h.length; i1++) {
				s_3.add(s_2.get((int) h[i1]));
			}
		} else {
			s_3 = s_1;
		}
		return s_3;
	}

	private static void sample(NumerosityReductionStrategy strategy, String dataName) {

		kFoldSample(strategy, dataName);

		// int clsNum = trainData.keySet().size();
		// // int rlt[][] = new int[clsNum][];
		// ResultParams[] rlt = new ResultParams[clsNum];
		//
		// for (int j = 0; j < clsNum; j++) {
		// // DataProcessor.writeTopPatterns(bestcombs[j].getThisRPatterns(),
		// // j,
		// // dataName);
		//
		// ArrayList<BestCombination> bestcombs = bestKResults[j]
		// .getBestKComb();
		//
		// StringBuffer sb = new StringBuffer();
		//
		// sb.append("For class ").append(j + 1).append(": ");
		//
		// ArrayList<int[]> reses = new ArrayList<int[]>();
		// for (BestCombination b : bestcombs) {
		//
		// double minV = b.getMinimalError();
		// sb.append("min CV error ").append(fmt.format(minV))
		// .append(" reached at ");
		//
		// int[] bestParams = b.getBestParams();
		// sb.append(Arrays.toString(bestParams)).append(COMMA);
		//
		// int[] res = Arrays.copyOf(bestParams, 4);
		// res[3] = strategy.index();
		//
		// reses.add(res);
		// // rlt[j] = res;
		// }
		// rlt[j] = new ResultParams();
		// rlt[j].setPramList(reses);
		//
		// consoleLogger.debug(sb.toString());
		// }
		//
		// StringBuffer sb = new StringBuffer();
		// sb.append("\n");
		// for (int i = 0; i < rlt.length; i++) {
		// ResultParams iPram = rlt[i];
		// for (int[] res : iPram.getPramList()) {
		// sb.append(Arrays.toString(res));
		// }
		// sb.append("\n");
		// }
		// consoleLogger.info(sb.toString());
		//
		// return rlt;
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
