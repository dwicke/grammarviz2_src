package edu.gmu.grammar.classification.util;

import java.util.List;
import java.util.Map;

/**
 * Created by David Fleming on 12/2/16.
 */
public class ClassificationResults {
    public String testDataPath;

    public Map<String, List<double[]>> testData;

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Test Data Path: " + this.testDataPath + "\n");
        return output.toString();
    }
}