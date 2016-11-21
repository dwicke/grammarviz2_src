package edu.gmu.grammar.test;

import edu.gmu.grammar.classification.util.*;

import java.io.IOException;

public class TestPS{

	public static void main(String[] args) {

		try {
			//String[] inputArgs = { "CBF", "data/CBF/CBF_TRAIN", "data/CBF/CBF_TEST" };
			String[] inputArgs = { "TOR", "data/TOR/TOR_TRAIN_ONE_EIGHTY", "data/TOR/TOR_TEST_ONE_EIGHTY" };
			testThree(inputArgs);
			// testThree(initialParamsTF_alarm);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void testThree(String[] initialParams) throws IndexOutOfBoundsException, IOException {
		runMethods(initialParams, "EXACT");
		// runMethods(initialParams, "NONE");
		// runMethods(initialParams, "MINDIST");
	}

	private static void runMethods(String[] initialParams, String strategy)
			throws IndexOutOfBoundsException, IOException {
		String[] newParams = new String[4];
		System.arraycopy(initialParams, 0, newParams, 0, 3);
		newParams[3] = strategy;
		PSDirectTransformAllClass.main(newParams);
	}

}
