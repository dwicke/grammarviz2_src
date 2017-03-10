package edu.gmu.grammar.classification.util;

import java.util.Random;

public class DistMethods {

	private static final double INF = 10000000000000000000f;
	private static final double WARP_WINDOW = 0.10;

	/**
	 * Calculating the distance between time series and pattern.
	 *
	 * @param ts
	 *            , a series of points for time series.
	 * @param pValue
	 *            , a series of points for pattern.
	 * @return
	 */
	public static double calcDistTSAndPattern(double[] ts, double[] pValue) {
		double bestDist = INF;
		int patternLen = pValue.length;

		int lastStartP = ts.length - pValue.length + 1;
		if (lastStartP < 1)
			return bestDist;

		// Find smallest place in symbolic space
		// int startP = findMatchSymbolic(ts, pValue);
		// startP randomly generate
		int startP = randInt(0, lastStartP - 1);

		double[] slidingWindow = new double[patternLen];

		System.arraycopy(ts, startP, slidingWindow, 0, patternLen);
		//bestDist = eculideanDistNorm(pValue, slidingWindow);
		bestDist = dtwDistNorm(pValue, slidingWindow);

		for (int i = 0; i < lastStartP; i++) {
			System.arraycopy(ts, i, slidingWindow, 0, patternLen);
			bestDist = dtwDistNorm(pValue, slidingWindow, bestDist);
			//double tempDist = eculideanDistNormEAbandon(pValue,
			//		slidingWindow, bestDist);
			//double tempDist = dtwDist(pValue, slidingWindow);
			//if (tempDist < bestDist) {
			//	bestDist = tempDist;
			//}
		}

		return bestDist;
	}

	private static int randInt(int min, int max) {

		// NOTE: Usually this should be a field rather than a method
		// variable so that it is not re-seeded every call.
		Random rand = new Random();

		// nextInt is normally exclusive of the top value,
		// so add 1 to make it inclusive

		int randomNum = rand.nextInt((max - min) + 1) + min;

		return randomNum;
	}

	public static double eculideanDist(double[] ts1, double[] ts2) {
		double dist = 0;
		double tsLen = ts1.length;

		for (int i = 0; i < ts1.length; i++) {
			double diff = ts1[i] - ts2[i];
			dist += Math.pow(diff, 2);
		}

		return Math.sqrt(dist / tsLen);
	}

	// public static double dtwDistNorm(double[] ts1, double[] ts2) {
	//
	// try {
	// ts1 = TSUtils.zMinusMean(ts1);
	// ts2 = TSUtils.zMinusMean(ts2);
	// // ts1 = TSUtils.zNormalize(ts1);
	// // ts2 = TSUtils.zNormalize(ts2);
	// } catch (TSException e) {
	// e.printStackTrace();
	// }
	// DTW dtw = new DTW(ts1, ts2);
	// return dtw.getDistance();
	// }

	public static double eculideanDistNormEAbandon(double[] ts1, double[] ts2,
			double bsfDist) {
		// return dtwDistNorm(ts1,ts2);
		double dist = 0;
		double tsLen = ts1.length;
		// try {
		// ts1 = TSUtils.zMinusMean(ts1);
		// ts2 = TSUtils.zMinusMean(ts2);
		// // ts1 = TSUtils.zNormalize(ts1);
		// // ts2 = TSUtils.zNormalize(ts2);
		// } catch (TSException e) {
		// e.printStackTrace();
		// }

		double bsf = Math.pow(tsLen * bsfDist, 2);

		for (int i = 0; i < ts1.length; i++) {
			double diff = ts1[i] - ts2[i];
			dist += Math.pow(diff, 2);

			if (dist > bsf)
				return Double.NaN;

		}

		// return Math.sqrt(dist / tsLen);
		return Math.sqrt(dist) / tsLen;
	}

	public static double eculideanDistNorm(double[] ts1, double[] ts2) {
		// return dtwDistNorm(ts1,ts2);
		double dist = 0;
		double tsLen = ts1.length;
		// try {
		// ts1 = TSUtils.zMinusMean(ts1);
		// ts2 = TSUtils.zMinusMean(ts2);
		// // ts1 = TSUtils.zNormalize(ts1);
		// // ts2 = TSUtils.zNormalize(ts2);
		// } catch (TSException e) {
		// e.printStackTrace();
		// }

		for (int i = 0; i < ts1.length; i++) {
			double diff = ts1[i] - ts2[i];
			dist += Math.pow(diff, 2);
		}

		// return Math.sqrt(dist / tsLen);
		return Math.sqrt(dist) / tsLen;
	}

	public static int countDiff(char[] ts1, char[] ts2, int bsf) {
		int diffCount = 0;

		for (int i = 0; i < ts1.length; i++) {
			if (ts1[i] != ts2[i]) {
				diffCount++;
				if (diffCount > bsf)
					return diffCount;
			}
		}

		return diffCount;
	}

	private static double dtwDistNorm(double[] p, double[] ts) {
		return dtwDistNorm(p, ts, Double.POSITIVE_INFINITY);
	}

	/*
	 *
	 */
	private static double dtwDistNorm(double[] p, double[] ts, double bsf) {
		int n = p.length;
		int w = (int) (WARP_WINDOW * n);
		double bsfDist = Math.pow(bsf * n, 2);
		double[][] dtw = new double[n+1][n+1];

		if (lowerBoundDist(p, ts, w) < bsfDist) {
			return bsf;
		}

		for (int i = 0; i <= n; i++) {
			for (int j = 0; j <= n; j++) {
				dtw[i][j] = Double.POSITIVE_INFINITY;
			}
		}
		dtw[0][0] = 0;

		for (int i = 1; i <= n; i++) {
			int jMin = (i - w > 1) ? i - w : 1;
			int jMax = (i + w < n) ? i + w : n;

			for (int j = jMin; j <= jMax; j++) {
				double dist  = Math.pow(p[i-1] - ts[j-1], 2);
				double min = dtw[i-1][j-1];

				if (min > dtw[i-1][j]) {
					min = dtw[i-1][j];
				} else if (min > dtw[i][j-1]) {
					min = dtw[i][j-1];
				}

				dtw[i][j] = dist + min;
				if (dtw[i][j] > bsfDist) {
					return bsf;
				}
			}
		}

		return Math.sqrt(dtw[n][n]) / n;
	}

	private static double lowerBoundDist(double[] p, double[] ts, int w) {
		double dist = 0;
		for (int i = 0; i < p.length; i++) {
			double qLower = (i - w > 0) ? p[i-w] : p[0];
			double qUpper = (i + w < p.length) ? p[i+w] : p[p.length-1];
			double u = (qUpper > qLower) ? qUpper : qLower;
			double l = (qUpper < qLower) ? qUpper : qLower;

			if (ts[i] > u) {
				dist += Math.pow(ts[i] - u, 2);
			} else if (ts[i] < l) {
				dist += Math.pow(ts[i] - l, 2);
			}
		}

		return Math.sqrt(dist) / p.length;
	}
}
