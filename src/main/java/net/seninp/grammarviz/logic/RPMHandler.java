package net.seninp.grammarviz.logic;

import edu.gmu.grammar.classification.util.ClassificationResults;
import edu.gmu.grammar.classification.util.PSDirectTransformAllClass;
import edu.gmu.grammar.classification.util.RPMTrainedData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by David Fleming on 1/24/17.
 */
public class RPMHandler {

    private PSDirectTransformAllClass RPM;
    private RPMTrainedData trainingResults;
    private String[] trainingLabels;
    private ClassificationResults testingResults;
    private String[] testingLabels;

    public RPMHandler() {
        this.RPM = new PSDirectTransformAllClass();
    }

    public void RPMTrain(String filename, double[][] data, String[] labels) throws java.io.IOException {
        this.trainingLabels = labels;
        this.trainingResults = this.RPM.RPMTrain(filename, data, labels);
    }

    public void RPMTestData(String filename, double[][] data, String[] labels) throws java.io.IOException {
        this.testingLabels = labels;
        this.testingResults = this.RPM.RPMTestData(filename, data, labels);
    }

    public String[][] getResults() {
        if(this.testingResults == null)
            return null;

        HashMap<String, int[]> convertedResults = new HashMap<String, int[]>();

        String[] entries = this.testingResults.results.split("\n");
        for(int i = 1; i < entries.length; i++) {
            String[] columns = entries[i].split(",");
            String actualClassLabel = columns[1].split(":")[0];

            if(!convertedResults.containsKey(actualClassLabel)) {
                convertedResults.put(actualClassLabel, new int[2]);
            }

            int[] ratio = convertedResults.get(actualClassLabel);
            ratio[1]++;
            if(columns[3].equals("+")) {
                ratio[0]++;
            }
        }

        String[][] output = new String[convertedResults.size()][2];
        int i = 0;
        for(Map.Entry<String, int[]> entry : convertedResults.entrySet()) {
            output[i][0] = entry.getKey();
            int[] ratio = entry.getValue();
            output[i][1] = ratio[0] + "/" + ratio[1];
            i++;
        }

        return output;
    }

    public int getWindowSize() {
        return this.trainingResults.windowSize;
    }

    public int getPaa() {
        return this.trainingResults.paa;
    }

    public int getAlphabet() {
        return this.trainingResults.alphabet;
    }

    public String[] getTrainedLabels() {
        return this.trainingLabels;
    }

    public String[] getTestingLabels() {
        return this.testingLabels;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        String[][] results = this.getResults();
        for(int i = 0; i < results.length; i++) {
            output.append(results[i][0] + ": ");
            output.append(results[i][1] + "\n");
        }

        return output.toString();
    }
}