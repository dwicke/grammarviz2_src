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
	 * @param p
	 *            , a series of points for pattern.
	 * @return
	 */
	public static double calcDistTSAndPattern(double[] ts, double[] p) {
		double[] slidingWindow = new double[p.length];
		int patternLen = p.length;
		int lastStartPoint = ts.length - p.length + 1;
		if (lastStartPoint < 1) { return Double.POSITIVE_INFINITY; }
		int startPoint = new Random().nextInt(lastStartPoint);

		System.arraycopy(ts, startPoint, slidingWindow, 0, p.length);
		double best = euclideanDistNorm(slidingWindow, p);
		//double best = dtwDistNorm(slidingWindow, p);

		for (int i = 0; i < lastStartPoint; i++) {
			System.arraycopy(ts, i, slidingWindow, 0, p.length);
			best = euclideanDistNorm(slidingWindow, p, best);
			//best = dtwDistNorm(slidingWindow, p, best);
		}

		return best;
	}

	private static double euclideanDistNorm(double[] ts, double[] p) {
		return euclideanDistNorm(ts, p, Double.POSITIVE_INFINITY);
	}

	private static double euclideanDistNorm(double[] ts, double[] p, double best) {
		double bestDist = Math.pow(best * p.length, 2);
		double dist = 0;

		for (int i = 0; i < p.length; i++) {
			dist += Math.pow(ts[i] - p[i], 2);
			if (dist > bestDist) { return best; }
		}

		return Math.sqrt(dist) / p.length;
	}

	private static double dtwDistNorm(double[] ts, double[] p) {
		return dtwDistNorm(p, ts, Double.POSITIVE_INFINITY);
	}

	private static double dtwDistNorm(double[] ts, double[] p, double best) {
		int n = p.length;
		int w = (int) (WARP_WINDOW * n);
		double bestDist = Math.pow(best * n, 2);
		double[][] dtw = new double[n+1][n+1];

		if (dtwLowerBoundDist(ts, p, w) < bestDist) { return best; }

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
				if (dtw[i][j] > bestDist) { return best; }
			}
		}

		return Math.sqrt(dtw[n][n]) / n;
	}

	private static double dtwLowerBoundDist(double[] ts, double[] p, int w) {
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
