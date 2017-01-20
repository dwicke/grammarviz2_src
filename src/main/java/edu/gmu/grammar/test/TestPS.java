package edu.gmu.grammar.test;

import edu.gmu.grammar.classification.util.*;

import java.io.IOException;

public class TestPS{

	public static void main(String[] args) {

		try {
			//String[] inputArgs = { "CBF", "data/CBF/CBF_TRAIN", "data/CBF/CBF_TEST" };
			//String[] inputArgs = { "TOR", "data/TOR/TOR_TRAIN_ONE_EIGHTY", "data/TOR/TOR_TEST_ONE_EIGHTY" };
			//String[] inputArgs = { "CAMERA-DATA", "data/camera-data/camera-to-basestation-rpm", "data/camera-data/camera-to-basestation-rpm-test" };
			//testThree(inputArgs);
			PSDirectTransformAllClass testing = new PSDirectTransformAllClass();
			//RPMTrainedData output = testing.RPMTrain("data/CBF/CBF_TRAIN");
			//ClassificationResults testoutput =  testing.RPMTestData("data/CBF/CBF_TEST");
			//RPMTrainedData output = PSDirectTransformAllClass.RPMTrain("TOR", "data/TOR/TOR_TRAIN_ONE_EIGHTY");
			//PSDirectTransformAllClass.RPMTestData("TOR", "data/TOR/TOR_TEST_ONE_EIGHTY");
			//RPMTrainedData output = PSDirectTransformAllClass.RPMTrain("CAMERA-DATA", "data/camera-data/camera-to-basestation-rpm");
			//PSDirectTransformAllClass.RPMTestData("CAMERA-DATA", "data/camera-data/camera-to-basestation-rpm-test");
			//System.out.println(output);
			//System.out.println(testoutput);
			// testThree(initialParamsTF_alarm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
