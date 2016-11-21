package edu.gmu.grammar.classification.util;

import edu.gmu.connectGI.GetRulesFromGI;
import edu.gmu.connectGI.GrammarIndcutionMethod;
import edu.gmu.grammar.classification.GCProcess;
import edu.gmu.grammar.patterns.BestSelectedPatterns;
import edu.gmu.grammar.patterns.TSPattern;
import edu.gmu.grammar.patterns.TSPatterns;
import net.seninp.gi.logic.RuleInterval;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.util.UCRUtils;
import org.apache.commons.lang.ArrayUtils;
import weka.core.Instances;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class DataProcessor {
	public static final Character DELIMITER = '~';

	public static void main(String[] args) {
		String dataName = "PP_Thumb";

		String typeTrain = "TRAIN";
		String filePathTrain = "C:\\Users\\user.2012ETF\\Dropbox\\work\\Research\\Source code\\Website Data\\"
				+ dataName + "\\" + dataName + "_" + typeTrain + ".arff";
		String filePathToTrain = "C:\\Users\\user.2012ETF\\Dropbox\\work\\Research\\Source code\\Website Data\\"
				+ dataName + "\\" + dataName + "_" + typeTrain;
		converArffToFile(filePathTrain, filePathToTrain);

		String typeTest = "TEST";
		String filePathTest = "C:\\Users\\user.2012ETF\\Dropbox\\work\\Research\\Source code\\Website Data\\" + dataName
				+ "\\" + dataName + "_" + typeTest + ".arff";
		String filePathToTest = "C:\\Users\\user.2012ETF\\Dropbox\\work\\Research\\Source code\\Website Data\\"
				+ dataName + "\\" + dataName + "_" + typeTest;
		converArffToFile(filePathTest, filePathToTest);
	}

	public static HashMap<String, double[]> concatenateTrain(Map<String, List<double[]>> trainData,
			HashMap<String, int[]> allStartPositions) {

		HashMap<String, double[]> concatenatedData = new HashMap<String, double[]>();

		for (Entry<String, List<double[]>> e : trainData.entrySet()) {
			String classLabel = e.getKey();

			ArrayList<Integer> temp = new ArrayList<Integer>();
			int startPoint = 1;

			int tsNum = e.getValue().size();
			int tsIdx = 1;

			for (double[] series : e.getValue()) {
				double[] existSeries = concatenatedData.get(classLabel);
				if (null == existSeries) {
					concatenatedData.put(classLabel, series);
				} else {
					double[] newExistSeries = ArrayUtils.addAll(existSeries, series);
					concatenatedData.put(classLabel, newExistSeries);
				}

				if (tsIdx < tsNum) {
					startPoint += series.length;
					temp.add(startPoint);
				}
				tsIdx++;
			}
			int[] tempInt = convertIntegers(temp);
			allStartPositions.put(classLabel, tempInt);
		}

		return concatenatedData;
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	public static HashMap<String, double[]> concatenateTrainInTrain(
			Map<String, List<TimeSeriesTrain>> trainDataPerClass, HashMap<String, int[]> allStartPositions) {

		HashMap<String, double[]> concatenatedData = new HashMap<String, double[]>();

		List<Entry<String, List<TimeSeriesTrain>>> list = new ArrayList<Entry<String, List<TimeSeriesTrain>>>(
				trainDataPerClass.entrySet());
		Collections.shuffle(list);

		for (Entry<String, List<TimeSeriesTrain>> e : list) {
			String classLabel = e.getKey();
			// if(classLabel.equals("41"))
			// System.out.println();

			// Record the start point of time series in concatenated one.
			ArrayList<Integer> temp = new ArrayList<Integer>();
			// temp.add(0);
			int startPoint = 0;

			int tsNum = e.getValue().size();
			int tsIdx = 1;

			for (TimeSeriesTrain series : e.getValue()) {

				double[] existSeries = concatenatedData.get(classLabel);
				if (null == existSeries) {
					concatenatedData.put(classLabel, series.getValues());
				} else {
					double[] newExistSeries = ArrayUtils.addAll(existSeries, series.getValues());
					concatenatedData.put(classLabel, newExistSeries);

				}
				if (tsIdx < tsNum) {
					startPoint += series.getValues().length;
					temp.add(startPoint);
				}
				tsIdx++;
			}

			// if (temp.size() < 1)
			// temp.add(0);

			int[] tempInt = convertIntegers(temp);
			allStartPositions.put(classLabel, tempInt);

		}

		return concatenatedData;
	}

	public static void classifyTesting(int[] params, String[] thisArgs) throws IndexOutOfBoundsException {
		String[] args = { thisArgs[0], thisArgs[1], Integer.toString(params[0]), Integer.toString(params[1]),
				Integer.toString(params[2]) };
		// GCProcess.main(args);
	}

	public static void classifyTestingVarBest10(ResultParams[] best10Params, String dataName, String[] thisArgs,
			Map<String, List<double[]>> trainData, Map<String, List<double[]>> testData,
			Map<String, List<double[]>> validateData, Map<String, List<double[]>> forRNNData,
			GrammarIndcutionMethod giMethod, boolean gcParams[], ArrayList<ArrayList<String>> secondCLasses,
			ArrayList<ResultParams[]> secondBests) throws IndexOutOfBoundsException {

		// isUsingMax, isDTW, isNormalize
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2], gcParams[3], gcParams[4]);
		// gcp.doClassifyNewMethodBest10(best10Params, dataName, trainData,
		// testData, validateData, forRNNData, giMethod);
		// gcp.doClassifyNewMethodBest10Second(best10Params, dataName,
		// trainData,
		// testData, validateData, forRNNData, giMethod, secondCLasses,
		// secondBests);
	}

	public static void classifyTestingVarBest10NotRead(TopKBestPatterns[] best10Params, String dataName,
			String[] thisArgs, Map<String, List<double[]>> trainData, Map<String, List<double[]>> testData,
			Map<String, List<double[]>> validateData, Map<String, List<double[]>> forRNNData,
			GrammarIndcutionMethod giMethod, boolean gcParams[], ArrayList<ArrayList<String>> secondCLasses,
			ArrayList<TopKBestPatterns[]> secondBests) throws IndexOutOfBoundsException {

		// isUsingMax, isDTW, isNormalize
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2], gcParams[3], gcParams[4]);
		// gcp.doClassifyNewMethodBest10(best10Params, dataName, trainData,
		// testData, validateData, forRNNData, giMethod);
		gcp.doClassifyNewMethodBest10SecondNotRead(best10Params, dataName, trainData, testData, validateData,
				forRNNData, giMethod, secondCLasses, secondBests);
	}

	public static void classifyTestingVarTransformed(BestSelectedPatterns bestSelectedPatterns, String dataName,
			String[] thisArgs, Map<String, List<double[]>> trainData, Map<String, List<double[]>> testData,
			Map<String, List<double[]>> validateData, Map<String, List<double[]>> forRNNData,
			GrammarIndcutionMethod giMethod, boolean gcParams[]) throws IndexOutOfBoundsException {

		// isUsingMax, isDTW, isNormalize
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2], gcParams[3], gcParams[4]);
		// gcp.doClassifyNewMethodBest10(best10Params, dataName, trainData,
		// testData, validateData, forRNNData, giMethod);
		gcp.doClassifyTransformed(bestSelectedPatterns, dataName, trainData, testData, validateData, forRNNData,
				giMethod);
		// gcp.doClassifyTransformed(bestSelectedPatterns, dataName, trainData,
		// trainData, validateData, forRNNData, giMethod);
	}

	public static void classifyTestingVarTransformedTT(Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData, boolean gcParams[]) throws IndexOutOfBoundsException {

		// isUsingMax, isDTW, isNormalize
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2], gcParams[3], gcParams[4]);
		// gcp.doClassifyNewMethodBest10(best10Params, dataName, trainData,
		// testData, validateData, forRNNData, giMethod);
		// gcp.doClassifyTransformed(bestSelectedPatterns,
		// dataName, trainData, testData, validateData, forRNNData,
		// giMethod);
		// gcp.doClassifyTransformed(bestSelectedPatterns,
		// dataName, trainData, trainData, validateData, forRNNData,
		// giMethod);
		gcp.doClassifyTest(trainData, testData);
	}

	public static void classifyTestingVarBest10Shifted(ResultParams[] best10Params, String dataName, String[] thisArgs,
			Map<String, List<double[]>> trainData, Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData, Map<String, List<double[]>> shiftedData,
			GrammarIndcutionMethod giMethod, boolean gcParams[]) throws IndexOutOfBoundsException {
		// isUsingMax, isDTW, isNormalize
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2], gcParams[3], gcParams[4]);
		gcp.doClassifyNewMethodBest10Shifted(best10Params, dataName, trainData, validateData, forRNNData, shiftedData,
				giMethod);
	}

	public static void classifyTestingVar(int[][] params, String[] thisArgs, Map<String, List<double[]>> trainData,
			Map<String, List<double[]>> testData, Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData, GrammarIndcutionMethod giMethod, boolean gcParams[])
					throws IndexOutOfBoundsException {
		// isUsingMax, isDTW, isNormalize
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2], gcParams[3], gcParams[4]);
		gcp.doClassifyNewMethod(params, trainData, testData, validateData, forRNNData, giMethod);
	}

	public static void classifyTestingVarShifted(int[][] params, String[] thisArgs,
			Map<String, List<double[]>> trainData, Map<String, List<double[]>> validateData,
			Map<String, List<double[]>> forRNNData, Map<String, List<double[]>> shiftedData,
			GrammarIndcutionMethod giMethod, boolean gcParams[]) throws IndexOutOfBoundsException {
		// isUsingMax, isDTW, isNormalize
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2], gcParams[3], gcParams[4]);
		gcp.doClassifyNewMethodShifted(params, trainData, validateData, forRNNData, shiftedData, giMethod);
	}

	public static void writeTopPatternsBest10(HashMap<String, TSPatterns> thisRPatterns, int clsIdx, int pIdx,
			String dataName) {

		String fileName = "Patterns_" + dataName + "_Class_" + (clsIdx + 1) + "_param_" + (pIdx + 1);

		String dirPath = "result/Patterns/" + dataName + "/";
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
			BufferedWriter output = new BufferedWriter(new FileWriter(file));

			for (Entry<String, TSPatterns> entry : thisRPatterns.entrySet()) {
				String label = entry.getKey();
				TSPatterns tsPatterns = entry.getValue();

				StringBuffer sb = new StringBuffer();
				for (TSPattern p : tsPatterns.getPatterns()) {
					sb.append(label);
					sb.append(",");
					for (double d : p.getPatternTS()) {
						sb.append(d);
						sb.append(",");
					}
					sb.append("\n");
				}

				output.write(sb.toString());
			}
			output.close();
			// System.out.println("\nWritten to file: " +
			// file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeArray(double[][] rlt) {

		String fileName = "arrayValue1";

		String dirPath = "result/";
		String fullPath = dirPath + fileName;

		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fullPath, true)))) {
			for (int i = 0; i < rlt.length; i++) {
				for (int j = 0; j < rlt[0].length; j++) {
					out.print(rlt[i][j]);
					if (j != rlt[0].length - 1) {
						out.print(", ");
					}
				}
				out.println();
			}
			// more code
			// out.println("more text");
			// more code
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
		}

	}

	public static void writeClassificationRlt(String rlt) {

		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
		Date date = new Date();

		String fileName = "Result_" + dateFormat.format(date);

		String dirPath = "result/";
		String fullPath = dirPath + fileName;

		File theDir = new File(dirPath);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + dirPath);

			try {
				theDir.mkdir();
			} catch (SecurityException se) {
				// handle it
			}
		}

		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fullPath, true)))) {
			out.print(rlt);
			// more code
			// out.println("more text");
			// more code
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
		}

	}

	public static void writeTopPatterns(HashMap<String, TSPatterns> thisRPatterns, int clsIdx, String dataName) {

		String fileName = "Patterns_" + dataName + "_Class_" + (clsIdx + 1);

		String dirPath = "result/Patterns/";
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
			BufferedWriter output = new BufferedWriter(new FileWriter(file));

			for (Entry<String, TSPatterns> entry : thisRPatterns.entrySet()) {
				String label = entry.getKey();
				TSPatterns tsPatterns = entry.getValue();

				StringBuffer sb = new StringBuffer();
				for (TSPattern p : tsPatterns.getPatterns()) {
					sb.append(label);
					sb.append(",");
					for (double d : p.getPatternTS()) {
						sb.append(d);
						sb.append(",");
					}
					sb.append("\n");
				}

				output.write(sb.toString());
			}
			output.close();
			System.out.println("\nWritten to file: " + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeFinalPatterns(TSPattern[] finalPatterns, String dataName) {

		DateFormat dateFormat = new SimpleDateFormat("MM_dd_yyyy");
		Date date = new Date();

		String fileName = "Patterns_" + dataName + dateFormat.format(date);

		String dirPath = "result/";
		String fullPath = dirPath + fileName;

		File theDir = new File(dirPath);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + dirPath);

			try {
				theDir.mkdir();
			} catch (SecurityException se) {
				// handle it
			}
		}

		try {
			File file = new File(fullPath);
			File dirFile = new File(dirPath);
			if (!(dirFile.isDirectory())) {
				dirFile.mkdirs();
			}

			if (!(file.exists())) {
				file.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(file));

			for (TSPattern pattern : finalPatterns) {
				String label = pattern.getLabel();
				// int fromTS = pattern.getFromTS();

				StringBuffer sb = new StringBuffer();
				sb.append(label);
				sb.append(",");
				for (double d : pattern.getPatternTS()) {
					sb.append(d);
					sb.append(",");
				}
				sb.append("\n");

				output.write(sb.toString());
			}
			output.close();
			System.out.println("\nWritten to file: " + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeConcatenatedData(HashMap<String, double[]> concatenateData) {

		String dirPath = "result/concatenatedFiles/";
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
				System.out.println("\nWritten to file: " + file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static void writeTransferedData(double[][] distToPI, String name) {

		String dirPath = "result/transferedData/";

		String fileName = "transfered_class_" + name;
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

			BufferedWriter output = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < distToPI[0].length; i++) {
				StringBuffer sb = new StringBuffer();
				for (int j = 0; j < distToPI.length; j++) {
					sb.append(distToPI[j][i]);
					sb.append(" ");
				}
				sb.append("\n");
				output.write(sb.toString());
			}

			output.close();
			// System.out.println("\nWritten to file: " +
			// file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void writePatternToFile(TSPattern[] patterns, String dataName) {
		String dirPath = "result/Patterns/";
		String fileName = "pattern_" + dataName;
		String fullPath = dirPath + fileName;

		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fullPath, true)))) {
			for (TSPattern tsp : patterns) {
				String clLabel = tsp.getLabel();
				double[] patternValue = tsp.getPatternTS();
				int fromTS = tsp.getFromTS();
				int startP = tsp.getStartP();

				out.print(clLabel);
				out.print(",");
				for (double v : patternValue) {
					out.print(v);
					out.print(" ");
				}
				out.print(",");
				out.print(fromTS);
				out.print(",");
				out.print(startP);
				out.print(",");
				out.println(patternValue.length);
				// out.print("\n");
			}

		} catch (IOException e) {
		}
	}

	public static void writeDIRECTNumToFile(int num, int tsLen) {
		String dirPath = "result/";
		String fileName = "DIRECT_Time";
		String fullPath = dirPath + fileName;

		try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(fullPath, true)))) {

			out.print(num);
			out.print(",");
			out.println(tsLen);

		} catch (IOException e) {
		}
	}

	public static Map<String, List<double[]>> readShiftedData(String dataName) {
		String dirPath = "result/ShiftedData/";
		String fullPath = dirPath + dataName + "_test_shifted";

		try {
			return UCRUtils.readUCRData(fullPath);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	public static double computeAccuracy(int correctNum, int tsNumHere) {
		return 1 - ((double) correctNum) / ((double) tsNumHere);
	}

	public static double computeErrorF1(int correctNum, int misClassifiedNum, int tsNumHere) {

		if (correctNum <= 0)
			return 1;

		double precision = 1;
		if (correctNum + misClassifiedNum != 0) {
			precision = ((double) correctNum) / ((double) correctNum + (double) misClassifiedNum);
		}
		double recall = ((double) correctNum) / ((double) tsNumHere);

		double f1Score = 0;
		if (precision + recall != 0) {
			f1Score = (2 * precision * recall) / (precision + recall);
		}

		return 1 - f1Score;
	}

	public static void writeShiftedData(Map<String, List<double[]>> shiftedData, String dataName) {
		String dirPath = "result/ShiftedData/";
		String fullPath = dirPath + dataName + "_test_shifted";
		try {
			File file = new File(fullPath);
			File dirFile = new File(dirPath);
			if (!(dirFile.isDirectory())) {
				dirFile.mkdirs();
			}

			if (!(file.exists())) {
				file.createNewFile();
			}
			BufferedWriter output = new BufferedWriter(new FileWriter(file));

			for (Entry<String, List<double[]>> entry : shiftedData.entrySet()) {
				String label = entry.getKey();
				List<double[]> tses = entry.getValue();

				StringBuffer sb = new StringBuffer();
				for (double[] ts : tses) {
					sb.append(label);
					sb.append(",");
					for (double d : ts) {
						sb.append(d);
						sb.append(",");
					}
					sb.append("\n");
				}

				output.write(sb.toString());
			}
			output.close();
			System.out.println("\nWritten to file: " + file.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process train data into the format of concatenating method.
	 * 
	 * @param data
	 * @return
	 */
	public static Map<String, double[]> processTrainData(Map<String, List<double[]>> data) {
		Map<String, double[]> tsData;
		tsData = new HashMap<String, double[]>();

		for (Entry<String, List<double[]>> e : data.entrySet()) {
			String classLabel = e.getKey();
			int classCounter = 0;
			for (double[] series : e.getValue()) {
				tsData.put(classLabel + DELIMITER + classCounter, series);
				classCounter++;
			}
		}

		return tsData;
	}

	public static String millisToShortDHMS(long duration) {
		String res = "";
		long days = TimeUnit.MILLISECONDS.toDays(duration);
		long hours = TimeUnit.MILLISECONDS.toHours(duration)
				- TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(duration));
		long minutes = TimeUnit.MILLISECONDS.toMinutes(duration)
				- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(duration));
		long seconds = TimeUnit.MILLISECONDS.toSeconds(duration)
				- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration));
		if (days == 0) {
			res = String.format("%02d:%02d:%02d", hours, minutes, seconds);
		} else {
			res = String.format("%dd%02d:%02d:%02d", days, hours, minutes, seconds);
		}
		return res;
	}

	public static void converArffToFile(String path, String filePathTo) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			Instances data = new Instances(reader);

			reader.close();

			double[][] reversed_rlt = new double[data.numAttributes()][];

			data.setClassIndex(data.numAttributes() - 1);

			for (int i = 0; i < data.numAttributes() - 1; i++) {

				double[] values = data.attributeToDoubleArray(i);
				reversed_rlt[i + 1] = values;
			}
			reversed_rlt[0] = data.attributeToDoubleArray(data.numAttributes() - 1);

			double[][] rlt = new double[data.numInstances()][data.numAttributes()];

			for (int i = 0; i < data.numInstances(); i++) {
				for (int j = 0; j < data.numAttributes(); j++) {
					rlt[i][j] = reversed_rlt[j][i];
					if (j == 0)
						rlt[i][j]++;
				}
			}

			write(filePathTo, rlt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void write(String filename, double[][] x) throws IOException {
		BufferedWriter outputWriter = null;
		outputWriter = new BufferedWriter(new FileWriter(filename));
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x[0].length; j++) {
				outputWriter.write(x[i][j] + " ");
			}
			outputWriter.newLine();

		}
		outputWriter.flush();
		outputWriter.close();
	}

	//
	// /**
	// * Get patterns from concatenated data with Sequitur.
	// *
	// * @param concatenateData
	// * @param params
	// * @return
	// */
	// public static HashMap<String, TSPatterns> getAllPatternsFromSequitur(
	// HashMap<String, double[]> concatenateData, int[][] params,
	// int originalLen, GrammarIndcutionMethod giMethod,
	// HashMap<String, int[]> allStartPositions) {
	//
	// HashMap<String, TSPatterns> allPatterns = new HashMap<String,
	// TSPatterns>();
	//
	// int windowSize = params[0][0];
	// int paaSize = params[0][1];
	// int alphabetSize = params[0][2];
	// int strategy = params[0][3];
	// NumerosityReductionStrategy nRStrategy =
	// NumerosityReductionStrategy.fromValue(strategy);
	// // String nRStrategy = "nr_exact";
	// // if (strategy == NumerosityReductionStrategy.NOREDUCTION.index()) {
	// // nRStrategy = "nr_off";
	// // } else if (strategy == NumerosityReductionStrategy.CLASSIC.index()) {
	// // nRStrategy = "nr_mindist";
	// // }
	//
	// int classNum = 1;
	// for (Entry<String, double[]> entry : concatenateData.entrySet()) {
	// String classLabel = entry.getKey();
	// double[] concatenatedTS = entry.getValue();
	// int[] startPositions = allStartPositions.get(classLabel);
	//
	// GetRulesFromGI gi = new GetRulesFromGI();
	// ArrayList<RepeatedPattern> allPatternsFromSequitur = gi
	// .getAllPatterns(windowSize, paaSize, alphabetSize,
	// nRStrategy, concatenatedTS, originalLen, giMethod,
	// startPositions);
	//
	// if (allPatternsFromSequitur == null) {
	// return null;
	// }
	//
	// TSPatterns patterns = new TSPatterns(classLabel);
	// // String patternFileName = folderName + "patterns_" + classLabel;
	//
	// readAllPatterns(concatenatedTS, allPatternsFromSequitur,
	// gi.getPatternsLocation(), patterns);
	//
	// // System.out.println("Class: " + classLabel + " has "
	// // + patterns.getPatterns().size() + " patterns");
	// allPatterns.put(classLabel, patterns);
	// }
	//
	// return allPatterns;
	// }

	/**
	 * Get patterns from concatenated data with Sequitur.
	 * 
	 * @param concatenateData
	 * @param params
	 * @return
	 */
	public static HashMap<String, TSPatterns> getPatternsFromSequitur(HashMap<String, double[]> concatenateData,
			int[][] params, GrammarIndcutionMethod giMethod, HashMap<String, int[]> allStartPositions) {

		HashMap<String, TSPatterns> allPatterns = new HashMap<String, TSPatterns>();

		int windowSize = params[0][0];
		int paaSize = params[0][1];
		int alphabetSize = params[0][2];
		int strategy = params[0][3];
		NumerosityReductionStrategy nRStrategy = NumerosityReductionStrategy.fromValue(strategy);
		// String nRStrategy = "nr_exact";
		// if (strategy == NumerosityReductionStrategy.NOREDUCTION.index()) {
		// nRStrategy = "nr_off";
		// } else if (strategy == NumerosityReductionStrategy.CLASSIC.index()) {
		// nRStrategy = "nr_mindist";
		// }

		int classNum = 1;
		for (Entry<String, double[]> entry : concatenateData.entrySet()) {
			String classLabel = entry.getKey();
			double[] concatenatedTS = entry.getValue();
			int[] startPositions = allStartPositions.get(classLabel);

			// String folderName =
			// "C:\\Users\\user.2012ETF\\Dropbox\\work\\Research\\grammar\\data\\temp\\";
			// String fileName = folderName + "concatenated" + classLabel +
			// ".txt";
			// wirteFile(concatenatedTS, fileName);

			// Concatenate training time series
			// long startTime = System.currentTimeMillis();
			GetRulesFromGI gi = new GetRulesFromGI();
			ArrayList<int[]> patternsLocation = gi.getGrammars(windowSize, paaSize, alphabetSize, nRStrategy,
					concatenatedTS, giMethod, startPositions);
			// long endTime = System.currentTimeMillis();
			// long totalTime = endTime - startTime;
			// System.out.println(DataProcessor.millisToShortDHMS(totalTime));

			if (patternsLocation == null) {
				return null;
			}

			TSPatterns patterns = new TSPatterns(classLabel);
			// String patternFileName = folderName + "patterns_" + classLabel;

			readPatterns(concatenatedTS, patternsLocation, patterns, startPositions);

			// System.out.println("Class: " + classLabel + " has "
			// + patterns.getPatterns().size() + " patterns");
			allPatterns.put(classLabel, patterns);
		}

		return allPatterns;
	}

	/**
	 * Get patterns from concatenated data with Sequitur.
	 * 
	 * @param concatenateData
	 * @param params
	 * @return
	 */
	public static HashMap<String, TSPatterns> getPatternsFromSequitur(HashMap<String, double[]> concatenateData,
			int[][] params, int originalLen, double[] previousError, GrammarIndcutionMethod giMethod,
			HashMap<String, int[]> allStartPositions) {

		HashMap<String, TSPatterns> allPatterns = new HashMap<String, TSPatterns>();

		int windowSize = params[0][0];
		int paaSize = params[0][1];
		int alphabetSize = params[0][2];
		int strategy = params[0][3];
		NumerosityReductionStrategy nRStrategy = NumerosityReductionStrategy.fromValue(strategy);
		// String nRStrategy = "nr_exact";
		// if (strategy == NumerosityReductionStrategy.NOREDUCTION.index()) {
		// nRStrategy = "nr_off";
		// } else if (strategy == NumerosityReductionStrategy.CLASSIC.index()) {
		// nRStrategy = "nr_mindist";
		// }

		int classNum = 1;
		for (Entry<String, double[]> entry : concatenateData.entrySet()) {
			String classLabel = entry.getKey();
			double[] concatenatedTS = entry.getValue();
			int[] startPositions = allStartPositions.get(classLabel);

			int i = Integer.parseInt(classLabel) - 1;
			double error = previousError[i];

			if (error == 0) {
				continue;
			}
			// String folderName =
			// "C:\\Users\\user.2012ETF\\Dropbox\\work\\Research\\grammar\\data\\temp\\";
			// String fileName = folderName + "concatenated" + classLabel +
			// ".txt";
			// wirteFile(concatenatedTS, fileName);

			// Concatenate training time series
			// long startTime = System.currentTimeMillis();
			GetRulesFromGI gi = new GetRulesFromGI();
			ArrayList<int[]> patternsLocation = gi.getGrammars(windowSize, paaSize, alphabetSize, nRStrategy,
					concatenatedTS, giMethod, startPositions);
			// long endTime = System.currentTimeMillis();
			// long totalTime = endTime - startTime;
			// System.out.println(DataProcessor.millisToShortDHMS(totalTime));

			if (patternsLocation == null) {
				return null;
			}

			TSPatterns patterns = new TSPatterns(classLabel);
			// String patternFileName = folderName + "patterns_" + classLabel;

			readPatterns(concatenatedTS, patternsLocation, patterns, startPositions);

			// System.out.println("Class: " + classLabel + " has "
			// + patterns.getPatterns().size() + " patterns");
			allPatterns.put(classLabel, patterns);
		}

		return allPatterns;
	}

	/**
	 * Read subsequences according to the location of patterns in concatenated
	 * time series.
	 * 
	 * @param concatenatedTS
	 * @param allPatternsFromSequitur
	 * @param patterns
	 */
	public static void readAllPatterns(double[] concatenatedTS, ArrayList<RepeatedPattern> allPatternsFromSequitur,
			ArrayList<int[]> patternsLocation, TSPatterns patterns) {

		for (int i = 0; i < allPatternsFromSequitur.size(); i++) {
			RepeatedPattern rp = allPatternsFromSequitur.get(i);

			int[] location = patternsLocation.get(i);
			int startPosition = location[0];
			int patternLength = location[1];
			int x = location[2];
			double[] patternTS1 = Arrays.copyOfRange(concatenatedTS, startPosition, startPosition + patternLength);

			ArrayList<RuleInterval> ris = rp.getSequences();
			int frequency = ris.size();

			if (x != frequency) {
				System.err.println("Not Same!");
			}

			TSPattern tp = new TSPattern(frequency, patternTS1, patterns.getLabel(), startPosition);

			for (RuleInterval ri : ris) {
				int startP = ri.getStart();

				double[] patternTS = Arrays.copyOfRange(concatenatedTS, startP, ri.getEnd());

				tp.getPatternsInClass().add(patternTS);
			}
			patterns.addPattern(tp);
		}
	}

	/**
	 * Read subsequences according to the location of patterns in concatenated
	 * time series.
	 * 
	 * @param concatenatedTS
	 * @param patternsLocation
	 * @param patterns
	 */
	public static void readPatterns(double[] concatenatedTS, ArrayList<int[]> patternsLocation, TSPatterns patterns,
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

			double[] patternTS = Arrays.copyOfRange(concatenatedTS, startPosition, startPosition + patternLength);

			TSPattern tp = new TSPattern(frequency, patternTS, patterns.getLabel(), startPosition);
			int sp = findIdx(startingPositions, startPosition);
			tp.setFromTS(sp);
			patterns.addPattern(tp);
		}

	}

	private static int findIdx(int[] startingPositions, int startPosition) {
		int idx = 1;
		for (int sp : startingPositions) {
			if (sp >= startPosition)
				break;
			idx++;
		}

		return idx;
	}

	private static int[] getIntervals(int orignalLen, int tsLen) {
		int tsNum = tsLen / orignalLen;
		int[] intervals = new int[tsNum - 1];
		for (int i = 0; i < tsNum - 1; i++) {
			intervals[i] = (i + 1) * orignalLen;
		}

		return intervals;
	}

	public static HashMap<String, double[]> concatenateTrainBK(Map<String, double[]> trainData) {

		HashMap<String, double[]> concatenatedData = new HashMap<String, double[]>();

		for (Entry<String, double[]> e : trainData.entrySet()) {
			String seriesKey = e.getKey();
			String classLabel = seriesKey.substring(0, seriesKey.indexOf(DELIMITER));
			double[] series = e.getValue();

			double[] existSeries = concatenatedData.get(classLabel);
			if (null == existSeries) {
				concatenatedData.put(classLabel, series);
			} else {
				double[] newExistSeries = ArrayUtils.addAll(existSeries, series);
				concatenatedData.put(classLabel, newExistSeries);
			}
		}

		return concatenatedData;
	}

	//
	// /**
	// * Get patterns from concatenated data with Sequitur.
	// *
	// * @param concatenateData
	// * @param params
	// * @return
	// */
	// public static TSPatterns getPatternsFromSequiturForClassI(
	// HashMap<String, double[]> concatenateData, int[][] params,
	// int originalLen, int classIdx, GrammarIndcutionMethod giMethod) {
	// String classLabel = String.valueOf(classIdx + 1);
	//
	// int windowSize = params[0][0];
	// int paaSize = params[0][1];
	// int alphabetSize = params[0][2];
	// int strategy = params[0][3];
	// String nRStrategy = "nr_exact";
	// if (strategy == NumerosityReductionStrategy.NOREDUCTION.index()) {
	// nRStrategy = "nr_off";
	// } else if (strategy == NumerosityReductionStrategy.CLASSIC.index()) {
	// nRStrategy = "nr_mindist";
	// }
	//
	// double[] concatenatedTS = concatenateData.get(classLabel);
	//
	// GetRulesFromGI gi = new GetRulesFromGI();
	// ArrayList<int[]> patternsLocation = gi
	// .getGrammars(windowSize, paaSize, alphabetSize, nRStrategy,
	// concatenatedTS, originalLen, giMethod);
	//
	// if (patternsLocation == null) {
	// return null;
	// }
	//
	// TSPatterns patternsForI = new TSPatterns(classLabel);
	//
	// readPatterns(concatenatedTS, patternsLocation, patternsForI,
	// originalLen);
	//
	// return patternsForI;
	// }

}
