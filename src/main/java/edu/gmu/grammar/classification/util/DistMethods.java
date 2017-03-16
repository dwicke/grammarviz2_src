package edu.gmu.grammar.classification.util;

import java.util.Random;

public class DistMethods {
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
	public static double calcDistEuclidean(double[] ts, double[] p) {
		if (ts.length - p.length < 0) { return Double.POSITIVE_INFINITY; }

		int lastStart = ts.length - p.length;
		int randStart = new Random().nextInt(lastStart + 1);
		double best = euclideanDistNorm(ts, p, randStart);

		for (int i = 0; i < lastStart; i++) {
			best = euclideanDistNorm(ts, p, i, best);
		}

		return best;
	}

	private static double euclideanDistNorm(double[] ts, double[] p, int w) {
		return euclideanDistNorm(ts, p, w, Double.POSITIVE_INFINITY);
	}

	private static double euclideanDistNorm(double[] ts, double[] p, int start, double best) {
		double bestDist = Math.pow(best * p.length, 2);
		double dist = 0;

		for (int i = 0; i < p.length; i++) {
			dist += Math.pow(ts[start + i] - p[i], 2);
			if (dist > bestDist) { return best; }
		}

		return Math.sqrt(dist) / p.length;
	}

	public static double calcDistDTW(double[] ts, double[] p) {
		if (ts.length - p.length < 0) { return Double.POSITIVE_INFINITY; }

		int lastStart = ts.length - p.length;
		int randStart = new Random().nextInt(lastStart + 1);
		double best = dtwDistNorm(ts, p, randStart);

		for (int i = 0; i < lastStart; i++) {
			best = dtwDistNorm(ts, p, i, best);
		}

		return best;
	}
	private static double dtwDistNorm(double[] ts, double[] p, int start) {
		return dtwDistNorm(ts, p, start, Double.POSITIVE_INFINITY);
	}

	// TODO: Update to be memory efficient on column/row use
	private static double dtwDistNorm(double[] ts, double[] p, int start, double best) {
        int n = p.length;
		int w = (int) Math.round(WARP_WINDOW * n);
		double bestDist = Math.pow(best * n, 2);
		//if (dtwLowerBoundDist(ts, p, start, w) >= bestDist) { return best; }

		double[][] dtw = new double[n + 1][n + 1];
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
				double dist = Math.pow(p[i - 1] - ts[start + j - 1], 2);
				double min = dtw[i - 1][j - 1];

				if (min > dtw[i - 1][j]) {
					min = dtw[i - 1][j];
				} else if (min > dtw[i][j - 1]) {
					min = dtw[i][j - 1];
				}

				dtw[i][j] = dist + min;
				if (dtw[i][j] > bestDist) { return best; }
			}
		}

		return Math.sqrt(dtw[n][n]) / n;
	}

	private static double dtwLowerBoundDist(double[] ts, double[] p, int start, int r) {
		double dist = 0;
		for (int i = 0; i < p.length; i++) {
			double u = upperBound(p, i, r);
			double l = lowerBound(p, i, r);

			if (ts[start + i] > u) {
				dist += Math.pow(ts[start + i] - u, 2);
			} else if (ts[start + i] < l) {
				dist += Math.pow(ts[start + i] - l, 2);
			}
		}

		return dist;
	}

	private static double upperBound(double q[], int idx, int r) {
		int max = (idx + r < q.length) ? idx + r : q.length - 1;
		int min = (idx - r > 0)        ? idx - r : 0;

		double upper = q[min];
		for (int i = min + 1; i <= max; i++) {
			if (upper < q[i]) { upper = q[i]; }
		}

		return upper;
	}

	private static double lowerBound(double q[], int idx, int r) {
		int max = (idx + r < q.length) ? idx + r : q.length - 1;
		int min = (idx - r > 0)        ? idx - r : 0;

		double lower = q[min];
		for (int i = min + 1; i <= max; i++) {
			if (lower > q[i]) { lower = q[i]; }
		}

		return lower;
	}
}
