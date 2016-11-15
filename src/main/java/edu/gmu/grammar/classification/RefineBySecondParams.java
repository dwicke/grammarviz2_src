package edu.gmu.grammar.classification;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.gmu.connectGI.GrammarIndcutionMethod;
import edu.gmu.grammar.classification.util.*;
import edu.gmu.grammar.patterns.TSPatterns;
import net.seninp.jmotif.direct.Point;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;
import net.seninp.util.StackTrace;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;

public class RefineBySecondParams {

	private ArrayList<TimeSeriesTrain> trainData;
	private Map<String, List<TimeSeriesTrain>> trainDataPerClass;
	private Map<String, List<double[]>> inputTrainData;
	private GrammarIndcutionMethod giMethod;
	private boolean gcParams[];
	private NumerosityReductionStrategy numerosityReductionStrategy;

	private static final Logger consoleLogger;
	private static final Level LOGGING_LEVEL = Level.INFO;
	static {
		consoleLogger = (Logger) LoggerFactory
				.getLogger(RefineBySecondParams.class);
		consoleLogger.setLevel(LOGGING_LEVEL);
	}

	public RefineBySecondParams(Map<String, List<double[]>> inputTrainData,
			NumerosityReductionStrategy strategy,
			GrammarIndcutionMethod giMethod, boolean gcParams[]) {
		this.trainData = new ArrayList<TimeSeriesTrain>();
		this.trainDataPerClass = new HashMap<String, List<TimeSeriesTrain>>();
		this.inputTrainData = inputTrainData;
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
	 * 
	 * @param bestKParams
	 */
	public ArrayList<ArrayList<String>> findSecondParamsByClassifyTrain(
			TopKBestPatterns[] bestKParams, String dataName) {

		int clsNum = bestKParams.length;

		// StringBuffer sb = new StringBuffer();
		// sb.append("\n");
		// for (int i = 0; i < bestKParams.length; i++) {
		// // sb.append("\tFor class ").append(i + 1)
		// // .append(" the best 10 params are:\n");
		// ResultParams iPram = bestKParams[i];
		// for (int[] res : iPram.getPramList()) {
		// sb.append(Arrays.toString(res));
		// }
		// sb.append("\n");
		// }
		// consoleLogger.info(sb.toString());

		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2],
				gcParams[3], gcParams[4]);

		// Performing classification on training data.
		ArrayList<TimeSeriesTest> rltTses = gcp
				.doClassifyNewMethodBest10NotRead(bestKParams, dataName,
						inputTrainData, inputTrainData, inputTrainData,
						inputTrainData, giMethod);

		ResultOnTrain[] rs = needRefinedWithByClassifyTrain(rltTses);

		ArrayList<ArrayList<String>> secondCLasses = new ArrayList<ArrayList<String>>();

		ArrayList<String> secondCs1 = new ArrayList<String>();

		int worstIdx = 0;
		int maxIncorrectNum = 0;

		int worstErrorIdx = 0;
		double worstError = 0;

		// find the most missclassified number
		for (int i = 0; i < rs.length; i++) {
			ResultOnTrain r = rs[i];
			if (maxIncorrectNum < r.getMaxIncorrectNum()) {
				worstIdx = i;
				maxIncorrectNum = r.getMaxIncorrectNum();
			}

			if (worstError < r.getError()) {
				worstErrorIdx = i;
				worstError = r.getError();
			}
		}
		int t = maxIncorrectNum * 2 / 3;

		if (maxIncorrectNum > 1) {
			Map<String, Integer> classifiedAs = rs[worstIdx]
					.getMissclassifiedAs();
			secondCs1.add(String.valueOf(worstIdx + 1));

			addHardClassifiedClass(classifiedAs, t, secondCs1, rs);
		}
		// for (Entry<String, Integer> e : classifiedAs.entrySet()) {
		// String classiedAsClass = e.getKey();
		// int wrongNum = e.getValue();
		//
		// if (wrongNum > t) {
		// secondCs1.add(classiedAsClass);
		// }
		// }

		if (secondCs1.size() < clsNum && secondCs1.size() > 1) {
			secondCLasses.add(secondCs1);
		}

		if (worstErrorIdx != worstIdx) {
			ArrayList<String> secondCs2 = new ArrayList<String>();

			if (worstError > 0) {
				Map<String, Integer> classifiedAs2 = rs[worstErrorIdx]
						.getMissclassifiedAs();
				secondCs2.add(String.valueOf(worstErrorIdx + 1));

				addHardClassifiedClass(classifiedAs2, t, secondCs2, rs);

				// for (Entry<String, Integer> e : classifiedAs2.entrySet()) {
				// String classiedAsClass = e.getKey();
				// int wrongNum = e.getValue();
				//
				// if (wrongNum > t) {
				// secondCs2.add(classiedAsClass);
				// }
				// }
			}
			if (secondCs2.size() < clsNum && secondCs2.size() > 1) {
				secondCLasses.add(secondCs2);
			}
		}
		return secondCLasses;
	}

	public void addHardClassifiedClass(Map<String, Integer> classifiedAs,
			int t, ArrayList<String> secondCs, ResultOnTrain[] rs) {
		for (Entry<String, Integer> e : classifiedAs.entrySet()) {
			String classiedAsClass = e.getKey();

			if (secondCs.contains(classiedAsClass))
				continue;

			int wrongNum = e.getValue();

			if (wrongNum > t) {
				secondCs.add(classiedAsClass);
				Map<String, Integer> newClassifiedAs = rs[Integer
						.parseInt(classiedAsClass) - 1].getMissclassifiedAs();
				addHardClassifiedClass(newClassifiedAs, t, secondCs, rs);
			}
		}
	}

	/**
	 * Computes the value at point.
	 * 
	 * @param point
	 * @return
	 */
	public ResultOnTrain[] needRefinedWithByClassifyTrain(
			ArrayList<TimeSeriesTest> rltTses) {
		int clsNum = inputTrainData.entrySet().size();

		ResultOnTrain[] result = new ResultOnTrain[clsNum];

		Map<String, Integer> assignedResultNum = new HashMap<String, Integer>();
		int[][] missClassifiedNum = new int[clsNum][clsNum];

		Map<String, Integer> correctNumPerClass = new HashMap<String, Integer>();
		for (TimeSeriesTest testTS : rltTses) {
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

		for (int j = 0; j < clsNum; j++) {
			result[j] = new ResultOnTrain();
			String classLabel = String.valueOf(j + 1);
			int tsNum = inputTrainData.get(classLabel).size();
			int correctNum = 0;
			if (correctNumPerClass.containsKey(classLabel)) {
				correctNum = correctNumPerClass.get(classLabel);
			}
			int misNum = 0;
			for (int i = 0; i < clsNum; i++) {
				misNum += missClassifiedNum[i][j];
			}

			double error = DataProcessor.computeErrorF1(correctNum, misNum,
					tsNum);

			result[j].setError(error);

			int maxIncorrectNum = 0;
			for (int i = 0; i < clsNum; i++) {
				int classifiedAsINum = missClassifiedNum[j][i];
				if (classifiedAsINum > 0) {
					String label = String.valueOf(i + 1);

					result[j].getMissclassifiedAs()
							.put(label, classifiedAsINum);
				}
				if (maxIncorrectNum < classifiedAsINum)
					maxIncorrectNum = classifiedAsINum;
			}
			result[j].setMaxIncorrectNum(maxIncorrectNum);
		}
		return result;
	}

	/**
	 * 
	 * @param bestKParams
	 */
	public void findSecondParams(ResultParams[] bestKParams) {
		int clsNum = bestKParams.length;
		for (int i = 0; i < clsNum; i++) {
			String classLabel = String.valueOf(i + 1);
			ResultParams top10PramsClsI = bestKParams[i];
			for (int[] paramClsI : top10PramsClsI.getPramList()) {
				int windowSize = paramClsI[0];
				int paaSize = paramClsI[1];
				int alphabetSize = paramClsI[2];
				int strategy = paramClsI[3];

				double[] candidateP = { (double) windowSize, (double) paaSize,
						(double) alphabetSize };
				Point candiPoint = Point.at(candidateP);

				ResultOnTrain[] r = needRefinedWith(candiPoint, strategy);
				System.out.println(r.length);
			}
		}
	}

	/**
	 * Computes the value at point.
	 * 
	 * @param point
	 * @return
	 */
	public ResultOnTrain[] needRefinedWith(Point point, int strategy) {

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
			params[0][3] = strategy;

			HashMap<String, int[]> allStartPositions = new HashMap<String, int[]>();

			// Concatenate training time series
			HashMap<String, double[]> concatenateData = DataProcessor
					.concatenateTrainInTrain(trainDataPerClass,
							allStartPositions);

			// TODO: write concatenated data.
			// writeConcatenatedData(concatenateData);

			int tsLen = trainData.get(0).getValues().length;
			// Get representative patterns
			HashMap<String, TSPatterns> allPatterns = DataProcessor
					.getPatternsFromSequitur(concatenateData, params, giMethod,
							allStartPositions);

			if (allPatterns == null) {
				return null;
			}

			double patternRate = 0.5;
			// HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
			// .selectTopFrequentPatterns(allPatterns, patternRate);
			HashMap<String, TSPatterns> topFrequentPatterns = GCProcess
					.selectTopFrequentPatterns(allPatterns, patternRate);
			if (topFrequentPatterns != null) {
				int clsNum = topFrequentPatterns.size();
				int[] missclassifiedSamplesPerClass = new int[clsNum];
				int[] correctNumPerClass = new int[clsNum];
				Arrays.fill(missclassifiedSamplesPerClass, 0);
				Arrays.fill(correctNumPerClass, 0);

				GCProcess gcp = new GCProcess(gcParams[0], gcParams[1],
						gcParams[2], gcParams[3], gcParams[4]);
				HashMap<String, TSPatterns> representativePatterns = gcp
						.selectBestFromRNNTrain(topFrequentPatterns, 3,
								trainDataPerClass);

				ResultOnTrain[] result = new ResultOnTrain[clsNum];

				for (int i = 0; i < clsNum; i++) {
					result[i] = new ResultOnTrain();
				}

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
							String label = String.valueOf(res + 1);
							int clsLabel = Integer.parseInt(trueClassLabel) - 1;
							if (result[clsLabel].getMissclassifiedAs()
									.containsKey(label)) {
								result[clsLabel].getMissclassifiedAs().put(
										label,
										result[clsLabel].getMissclassifiedAs()
												.get(label) + 1);
							} else {
								result[clsLabel].getMissclassifiedAs().put(
										label, 1);
							}
						}
					}
				}

				double[] error = new double[clsNum];
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
					result[l].setError(error[l]);
				}

				return result;
			} else {
				return null;
			}

		} catch (Exception e) {
			System.err.println("Exception caught: " + StackTrace.toString(e));
			return null;
		}

	}

	private int classifyTrain(String trueClassKey, TimeSeriesTrain oneSampleTS,
			HashMap<String, TSPatterns> representativePatterns) {
		int k = 1;
		GCProcess gcp = new GCProcess(gcParams[0], gcParams[1], gcParams[2],
				gcParams[3], gcParams[4]);
		String assignedLabel = gcp.knnClassifyTrain(oneSampleTS,
				representativePatterns, k);

		if (assignedLabel.equalsIgnoreCase("PatternFromThisTS")) {
			return -10;
		}

		if (assignedLabel.equalsIgnoreCase(trueClassKey)) {
			return -1;
		}
		// return 0;
		return Integer.parseInt(assignedLabel) - 1;
	}

}
