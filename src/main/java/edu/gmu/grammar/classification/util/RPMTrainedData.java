package edu.gmu.grammar.classification.util;

import edu.gmu.grammar.classification.GCProcessMultiClass;
import edu.gmu.grammar.patterns.BestSelectedPatterns;
import edu.gmu.grammar.patterns.PatternsSimilarity;
import edu.gmu.grammar.patterns.TSPattern;
import net.seninp.jmotif.sax.NumerosityReductionStrategy;

import java.util.List;
import java.util.Map;

/**
 * Created by David Fleming on 12/2/16.
 */
public class RPMTrainedData {
    public int paa;
    public int windowSize;
    public int alphabet;

    public BestSelectedPatterns[] bestSelectedPatternsAllClass;

    public String training_data_path;
    public Map<String, List<double[]>> trainData;
    public String allStrategy;

    public int[] upperBounds;
    public int[] lowerBounds;

    public int folderNum;
    public double rpFrequencyTPer;
    public int maxRPNum;
    public double overlapTPer;
    public Boolean isCoverageFre;

    public double pSimilarity;

    public int iterations_num;

    public TSPattern[] finalPatterns() {
        GCProcessMultiClass gcp = new GCProcessMultiClass(this.folderNum);
        return gcp.combinePatterns(this.bestSelectedPatternsAllClass);
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        output.append("Train Data Path: " + this.training_data_path + "\n");
        output.append("Best Patterns All Classes");
        TSPattern[] patterns = this.finalPatterns();
        for(int i = 0; i < patterns.length; i++) {
            output.append(patterns[i].toString() + "\n");
        }

        return output.toString();
    }

}
