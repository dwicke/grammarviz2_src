package net.seninp.grammarviz.logic;

import edu.gmu.grammar.classification.util.ClassificationResults;
import edu.gmu.grammar.classification.util.PSDirectTransformAllClass;
import edu.gmu.grammar.classification.util.RPMTrainedData;
import edu.gmu.grammar.patterns.TSPattern;
import net.seninp.grammarviz.model.GrammarVizMessage;
import net.seninp.util.StackTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * Created by David Fleming on 1/24/17.
 */
public class RPMHandler extends Observable implements Runnable {

    private PSDirectTransformAllClass RPM;
    private RPMTrainedData trainingResults;
    private String trainingFilename;
    private double[][] trainingData;
    private String[] trainingLabels;
    private ClassificationResults testingResults;
    private String[] testingLabels;
    private TSPattern[] finalPatterns;
    private int numberOfIterations;

    public RPMHandler() {
        super();
        this.RPM = new PSDirectTransformAllClass();
        this.numberOfIterations = PSDirectTransformAllClass.DEFAULT_NUMBER_OF_ITERATIONS;
    }

    public synchronized void RPMTrain(String filename, double[][] data, String[] labels) throws java.io.IOException {
        this.trainingResults = this.RPM.RPMTrain(filename, data, labels, PSDirectTransformAllClass.DEFAULT_STRATEGY,
                this.numberOfIterations);
        this.finalPatterns = this.trainingResults.finalPatterns();
    }

    @Override
    public void run() {
        this.log("Starting RPM Training in Background");
        try {
            this.RPMTrain(this.trainingFilename, this.trainingData, this.trainingLabels);
            this.setChanged();
            notifyObservers(new GrammarVizMessage(GrammarVizMessage.RPM_TRAIN_RESULTS_UPDATE_MESSAGE, this));

            this.log("Finished RPM Training in Background");
        } catch(Exception e) {
            this.log("error while training RPM model " + StackTrace.toString(e));
            e.printStackTrace();
        }
    }

    public synchronized void RPMTestData(String filename, double[][] data, String[] labels) throws java.io.IOException {
        this.testingLabels = labels;
        this.testingResults = this.RPM.RPMTestData(filename, data, labels);
    }

    public synchronized void RPMLoadModel(String filename) {
        RPMTrainedData rpmTrainedData = null;
        try {
            FileInputStream loadFile = new FileInputStream(filename);
            ObjectInputStream loadStream = new ObjectInputStream(loadFile);
            rpmTrainedData = (RPMTrainedData) loadStream.readObject();
            loadStream.close();
            loadFile.close();
        } catch(ClassNotFoundException e) {
            this.log("error " + filename + " is not a RPM Model");
            e.printStackTrace();
        } catch(Exception e) {
            this.log("error while loading RPM model " + StackTrace.toString(e));
            e.printStackTrace();
        }

        if(!(rpmTrainedData == null)) {
            this.trainingResults = rpmTrainedData;
            this.trainingFilename = rpmTrainedData.training_data_path;
            this.finalPatterns = rpmTrainedData.finalPatterns();
            this.numberOfIterations = rpmTrainedData.iterations_num;
        }

    }

    public synchronized void RPMSaveModel(String filename) {
        try {
            FileOutputStream saveFile = new FileOutputStream(filename);
            ObjectOutputStream saveStream = new ObjectOutputStream(saveFile);
            saveStream.writeObject(this.trainingResults);
            saveStream.close();
            saveFile.close();
        } catch(Exception e) {
            this.log("error while saving RPM model " + StackTrace.toString(e));
            e.printStackTrace();
        }

    }

    public synchronized TSPattern[] getRepresentativePatterns() {
        return this.finalPatterns;
    }

    public synchronized String[][] getResults() {
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

    public synchronized int getWindowSize() {
        return this.trainingResults.windowSize;
    }

    public synchronized int getPaa() {
        return this.trainingResults.paa;
    }

    public synchronized int getAlphabet() {
        return this.trainingResults.alphabet;
    }

    public synchronized String[] getTrainedLabels() {
        return this.trainingLabels;
    }

    public synchronized String[] getTestingLabels() {
        return this.testingLabels;
    }

    public synchronized void setNumberOfIterations(int numberOfIterations) { this.numberOfIterations = numberOfIterations; }

    public synchronized int getNumberOfIterations() {return this.numberOfIterations; }


    public synchronized String getTrainingFilename() {
        return trainingFilename;
    }

    public synchronized void setTrainingFilename(String trainingFilename) {
        this.trainingFilename = trainingFilename;
    }

    public synchronized double[][] getTrainingData() {
        return trainingData;
    }

    public synchronized void setTrainingData(double[][] trainingData) {
        this.trainingData = trainingData;
    }

    public synchronized String[] getTrainingLabels() {
        return trainingLabels;
    }

    public synchronized void setTrainingLabels(String[] trainingLabels) {
        this.trainingLabels = trainingLabels;
    }

    public synchronized void forceRPMModelReload() {
        this.trainingResults.trainData = this.RPM.convertGrammarVizData(this.trainingData, this.trainingLabels);
        this.RPM.loadRPMTrain(this.trainingResults);
    }

    @Override
    public synchronized String toString() {
        StringBuilder output = new StringBuilder();
        String[][] results = this.getResults();
        for(int i = 0; i < results.length; i++) {
            output.append(results[i][0] + ": ");
            output.append(results[i][1] + "\n");
        }

        return output.toString();
    }

    private void log(String message) {
        this.setChanged();
        notifyObservers(new GrammarVizMessage(GrammarVizMessage.STATUS_MESSAGE, "RPM Handler: " + message));
    }
}