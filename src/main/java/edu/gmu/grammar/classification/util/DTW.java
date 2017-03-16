package edu.gmu.grammar.classification.util;

import weka.core.DistanceFunction;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.neighboursearch.PerformanceStats;
import weka.core.Option;

import java.io.Serializable;

/**
 * Created by David Fleming on 3/2/17.
 */
public class DTW implements DistanceFunction, Serializable {

    protected double windowSizePercent = 1;
    protected Instances insts;
    protected boolean invertSelection;
    protected java.lang.String AttributeIndices;

    public DTW() {
        this.invertSelection = false;
    }

    public DTW(double windowSizePercent) {
        this.windowSizePercent = windowSizePercent;
        this.invertSelection = false;
    }

    public static double DTW(double[] s, double[] t, int windowSize) {
        int window = Math.max(windowSize, Math.abs(s.length - t.length));
        double[][] D = new double[2][t.length + 1];
        java.util.Arrays.fill(D[0], Double.POSITIVE_INFINITY);
        java.util.Arrays.fill(D[1], Double.POSITIVE_INFINITY);
        D[0][0] = 0.0;

        for(int i = 1; i <= s.length; i++) {
            int jStop = Math.min(t.length, i + window);
            for(int j = Math.max(1, i - window); j <= jStop; j++) {
                double cost = Math.abs(s[i - 1] - t[j - 1]); // Distance

                D[1][j] = cost + Math.min(D[0    ][j    ],   // D[i - 1][j    ]
                                 Math.min(D[1    ][j - 1],   // D[i    ][j - 1]
                                          D[0    ][j - 1])); // D[i - 1][j - 1]
            }

            double[] temp = D[0];
            D[0] = D[1];
            D[1] = temp;
            java.util.Arrays.fill(D[1], Double.POSITIVE_INFINITY);
        }

        return D[0][t.length];
    }

    public double distance(Instance first, Instance second) {
        return DTW.DTW(first.toDoubleArray(), second.toDoubleArray(),
                (int) (Math.max(first.numValues(), second.numValues()) * windowSizePercent));
    }

    public double distance(Instance first, Instance second, double cutOffValue) {
        return distance(first, second);
    }

    public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
        return distance(first, second);
    }

    public double distance(Instance first, Instance second, PerformanceStats stats) {
        return distance(first, second);
    }

    public void postProcessDistances(double[] distances) {

    }

    public void setAttributeIndices(java.lang.String value) {
        this.AttributeIndices = value;
    }

    public java.lang.String getAttributeIndices() {
        return this.AttributeIndices;
    }

    public void setInvertSelection(boolean value) {
        this.invertSelection = value;
    }

    public boolean getInvertSelection() {
        return this.invertSelection;
    }

    public void setInstances(Instances insts) {
        this.insts = insts;
    }

    public Instances getInstances() {
        return this.insts;
    }

    public void update(Instance ins) {

    }

    public void clean() {
        this.insts = null;
    }

    public java.lang.String[] getOptions() {
        return null;
    }

    public void setOptions(java.lang.String[] options)
            throws java.lang.Exception {
    }

    public java.util.Enumeration<Option> listOptions() {
        return null;
    }

    public static void main(String[] argv) {
        java.util.Random rand = new java.util.Random(1001001);
        int size = 1000;
        double[] s = new double[size];
        double[] t = new double[size];
        for(int i = 0; i < size; i++) {
            s[i] = rand.nextDouble();
            t[i] = rand.nextDouble();
        }

        System.out.println("s: " + s.length +
                " t: " + t.length);
        System.out.println("dtw:" + DTW(s, t, (int) Math.round(s.length * .01)));

    }

}
