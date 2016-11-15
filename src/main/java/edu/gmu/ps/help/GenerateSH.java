package edu.gmu.ps.help;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class GenerateSH {
	public static void main(String[] args) {

		// run("NonInvasiveFatalECGThorax1");
		// run("StarLightCurves");
		//
		// run("5type_alarm");
		// run("TF_alarm");

		// run("Coffee_shift");
		// run("FaceFour_shift");
		// run("Gun_Point_shift");
		// run("SwedishLeaf_shift");
		// run("Trace_shift");
		// run("OSULeaf_shift");

		run("CBF");
		run("Beef");
		run("Cricket_Y");
		run("Cricket_X");
		run("Coffee");
		run("CinC_ECG_torso");
		run("ChlorineConcentration");
//		run("Adiac");
		run("50words");
		run("Cricket_Z");
		run("DiatomSizeReduction");
		run("ECG200");
		run("ECGFiveDays");
		run("FaceAll");
		run("FaceFour");
		run("FacesUCR");
		run("Fish");
		run("Gun_Point");
		run("Haptics");
		run("InlineSkate");
		run("ItalyPowerDemand");
		run("Lighting2");
		run("Lighting7");
		run("MALLAT");
		run("MedicalImages");
		run("MoteStrain");
		run("OliveOil");
		run("OSULeaf");
		run("SonyAIBORobotSurface");
		run("SonyAIBORobotSurfaceII");
		run("SwedishLeaf");
		run("Symbols");
		run("synthetic_control");
		run("Trace");
		run("Two_Patterns");
		run("TwoLeadECG");
		run("uWaveGestureLibrary_X");
		run("uWaveGestureLibrary_Y");
		run("uWaveGestureLibrary_Z");
		run("wafer");
		run("WordsSynonyms");
		run("yoga");
	}

	public static void run(String dataName) {
		subAll(dataName);
		// String jarName = "lspaper2.jar";
		// String message = generateSHMessage(dataName, jarName);
		// writeClassificationRlt(message, dataName);

		// String message = generateSHMessageFS(dataName);
		// writeClassificationRltFS(message, dataName);
		// subAllFS(dataName);

		subAllRP(dataName, "EXACT");
		subAllRP(dataName, "MINDIST");
		subAllRP(dataName, "NONE");
		runRP(dataName);
	}

	public static void runRP(String dataName) {

		String jarName = "runrpdynamic.jar";

		String strategyName = "EXACT";
		String message = generateSHMessageRP(dataName, jarName, strategyName);
		writeClassificationRltRP(message, dataName, strategyName);

		strategyName = "MINDIST";
		message = generateSHMessageRP(dataName, jarName, strategyName);
		writeClassificationRltRP(message, dataName, strategyName);

		strategyName = "NONE";
		message = generateSHMessageRP(dataName, jarName, strategyName);
		writeClassificationRltRP(message, dataName, strategyName);
	}

	public static void subAllFS(String dataName) {
		StringBuffer message = new StringBuffer();
		message.append("dos2unix fs");
		message.append(dataName);
		message.append(".sh\n");
		message.append("qsub fs");
		message.append(dataName);
		message.append(".sh\n");
		System.out.println(message);
	}

	public static void subAllRP(String dataName, String strategy) {
		StringBuffer message = new StringBuffer();
		message.append("dos2unix rp");
		message.append(dataName);
		message.append("_");
		message.append(strategy);
		message.append(".sh\n");
		message.append("qsub rp");
		message.append(dataName);
		message.append("_");
		message.append(strategy);
		message.append(".sh\n");
		System.out.println(message);
	}

	public static void subAll(String dataName) {
		StringBuffer message = new StringBuffer();
		message.append("dos2unix ls");
		message.append(dataName);
		message.append(".sh\n");
		message.append("qsub ls");
		message.append(dataName);
		message.append(".sh\n");
		System.out.println(message);
	}

	public static void writeClassificationRltFS(String message, String dataName) {

		String fileName = "fs" + dataName + ".sh";

		String dirPath = "data/result/FS/";
		String fullPath = dirPath + fileName;

		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(fullPath, true)))) {
			out.print(message);
		} catch (IOException e) {
		}

	}

	public static void writeClassificationRlt(String message, String dataName) {

		String fileName = "ls" + dataName + ".sh";

		String dirPath = "data/result/LSpaper/";
		String fullPath = dirPath + fileName;

		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(fullPath, true)))) {
			out.print(message);
		} catch (IOException e) {
		}

	}

	public static void writeClassificationRltRP(String message,
			String dataName, String strategyName) {

		String fileName = "rp" + dataName + "_" + strategyName + ".sh";

		String dirPath = "data/result/RP/";
		String fullPath = dirPath + fileName;

		try (PrintWriter out = new PrintWriter(new BufferedWriter(
				new FileWriter(fullPath, true)))) {
			out.print(message);
		} catch (IOException e) {
		}

	}

	public static String generateSHMessageFS(String dataName) {

		StringBuffer allMeassge = new StringBuffer();
		allMeassge.append("#!/bin/sh\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Specify the shell to use, by default it uses bash\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Specify the name for your job name, this is the job name by which grid engine will \n");
		allMeassge
				.append("# refer to your job, this could be different from name of your executable or name of your script file\n");
		allMeassge.append("#$ -N FS_");
		allMeassge.append(dataName);
		allMeassge.append("\n");
		allMeassge.append("#\n");

		// allMeassge.append("#$ -pe shared 8\n");
		// allMeassge.append("#\n");

		allMeassge.append("#$ -l mf=10G\n");
		allMeassge.append("#\n");

		allMeassge
				.append("# Join the standard output & error files into one file (y)[yes] or write to separate files (n)[no]\n");
		allMeassge.append("# The default is n [no]\n");
		allMeassge.append("#$ -j y\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Use the directory from where the job is submitted\n");
		allMeassge.append("#$ -cwd\n");
		allMeassge.append("#\n");

		allMeassge.append("# Specify the queue where the job is submitted\n");
		allMeassge.append("## -q all.q\n");

		allMeassge
				.append("# The output path and file name if different from job name\n");
		allMeassge.append("#$ -o coutput_FS_");
		allMeassge.append(dataName);
		allMeassge.append("\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Send mail to: (Replace the edu.gmu-user-id with your user-id)\n");
		allMeassge.append("#$ -M xwang24@edu.gmu.edu\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Send mail at 'b' = submission , 'e' = completion, 's' = suspend, 'a' = aborted events, \n");
		allMeassge.append("# you can also combine this arguments like below\n");
		allMeassge.append("#$ -m ae\n");
		allMeassge.append("#\n");
		allMeassge.append("# Start the job\n");
		allMeassge.append("#  your application or script goes here");
		allMeassge.append("\n");
		allMeassge.append("\n");
		allMeassge.append("\n");

		allMeassge.append(". /etc/profile\n");
		allMeassge.append("module load gcc/4.8.4\n");
		allMeassge.append("\n");

		allMeassge.append("./FastShapelet ");
		allMeassge.append("../data/");
		allMeassge.append(dataName);
		allMeassge.append("/");
		allMeassge.append(dataName);
		allMeassge.append("_TRAIN ");
		allMeassge.append("TOBEREPLACETRAIN ");
		allMeassge.append("1 10 10 tree_");
		allMeassge.append(dataName);
		allMeassge.append(".txt ");
		allMeassge.append("time_");
		allMeassge.append(dataName);
		allMeassge.append(".txt\n");

		allMeassge.append("./Classify ");
		allMeassge.append(" ../data/");
		allMeassge.append(dataName);
		allMeassge.append("/");
		allMeassge.append(dataName);
		allMeassge.append("_TEST ");
		allMeassge.append("TOBEREPLACETEST ");
		allMeassge.append("tree_");
		allMeassge.append(dataName);
		allMeassge.append(".txt ");
		allMeassge.append("time_");
		allMeassge.append(dataName);
		allMeassge.append(".txt");

		// allMeassge.append(" >> ");
		// allMeassge.append("logls");
		// allMeassge.append(dataName);
		// allMeassge.append(".txt");

		// System.out.println(allMeassge);
		return allMeassge.toString();
	}

	public static String generateSHMessageRP(String dataName, String jarName,
			String strategyName) {
		String thresholdPer = "003";

		StringBuffer allMeassge = new StringBuffer();
		allMeassge.append("#!/bin/sh\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Specify the shell to use, by default it uses bash\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Specify the name for your job name, this is the job name by which grid engine will \n");
		allMeassge
				.append("# refer to your job, this could be different from name of your executable or name of your script file\n");
		allMeassge.append("#$ -N RP02_");
		allMeassge.append(dataName);
		allMeassge.append("_");
		allMeassge.append(strategyName);
		allMeassge.append("_");
		allMeassge.append(thresholdPer);
		allMeassge.append("\n");
		allMeassge.append("#\n");

		allMeassge.append("#$ -pe shared 8\n");
		allMeassge.append("#\n");

		allMeassge.append("#$ -l mf=10G\n");
		allMeassge.append("#\n");

		allMeassge
				.append("# Join the standard output & error files into one file (y)[yes] or write to separate files (n)[no]\n");
		allMeassge.append("# The default is n [no]\n");
		allMeassge.append("#$ -j y\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Use the directory from where the job is submitted\n");
		allMeassge.append("#$ -cwd\n");
		allMeassge.append("#\n");

		// allMeassge.append("# Specify the queue where the job is submitted\n");
		// allMeassge.append("#$ -q all.q\n");

		allMeassge
				.append("# The output path and file name if different from job name\n");
		allMeassge.append("#$ -o coutput_RP_");
		allMeassge.append(dataName);
		allMeassge.append("_");
		allMeassge.append(strategyName);
		allMeassge.append("_");
		allMeassge.append(thresholdPer);
		allMeassge.append("\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Send mail to: (Replace the edu.gmu-user-id with your user-id)\n");
		allMeassge.append("#$ -M xwang24@edu.gmu.edu\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Send mail at 'b' = submission , 'e' = completion, 's' = suspend, 'a' = aborted events, \n");
		allMeassge.append("# you can also combine this arguments like below\n");
		allMeassge.append("#$ -m ae\n");
		allMeassge.append("#\n");
		allMeassge.append("# Start the job\n");
		allMeassge.append("#  your application or script goes here");
		allMeassge.append("\n");
		allMeassge.append("\n");
		allMeassge.append("\n");

		allMeassge.append("java -jar ");
		allMeassge.append("\"");
		allMeassge.append(jarName);
		allMeassge.append("\" ");
		allMeassge.append(dataName);
		allMeassge.append(" ../data/");
		allMeassge.append(dataName);
		allMeassge.append("/");
		allMeassge.append(dataName);
		allMeassge.append("_TRAIN");
		allMeassge.append(" ../data/");
		allMeassge.append(dataName);
		allMeassge.append("/");
		allMeassge.append(dataName);
		allMeassge.append("_TEST ");
		allMeassge.append(strategyName);
		allMeassge.append(" >> ");
		allMeassge.append("logrp");
		allMeassge.append(dataName);
		allMeassge.append(".txt");
		allMeassge.append("\n");

		// allMeassge.append("java -jar ");
		// allMeassge.append("\"");
		// allMeassge.append(jarName);
		// allMeassge.append("\" ");
		// allMeassge.append(dataName);
		// allMeassge.append(" ../data/");
		// allMeassge.append(dataName);
		// allMeassge.append("/");
		// allMeassge.append(dataName);
		// allMeassge.append("_TRAIN");
		// allMeassge.append(" ../data/");
		// allMeassge.append(dataName);
		// allMeassge.append("/");
		// allMeassge.append(dataName);
		// allMeassge.append("_TEST MINDIST");
		// allMeassge.append(" >> ");
		// allMeassge.append("logrp");
		// allMeassge.append(dataName);
		// allMeassge.append(".txt");
		// allMeassge.append("\n");
		//
		// allMeassge.append("java -jar ");
		// allMeassge.append("\"");
		// allMeassge.append(jarName);
		// allMeassge.append("\" ");
		// allMeassge.append(dataName);
		// allMeassge.append(" ../data/");
		// allMeassge.append(dataName);
		// allMeassge.append("/");
		// allMeassge.append(dataName);
		// allMeassge.append("_TRAIN");
		// allMeassge.append(" ../data/");
		// allMeassge.append(dataName);
		// allMeassge.append("/");
		// allMeassge.append(dataName);
		// allMeassge.append("_TEST NONE");
		// allMeassge.append(" >> ");
		// allMeassge.append("logrp");
		// allMeassge.append(dataName);
		// allMeassge.append(".txt");

		// System.out.println(allMeassge);
		return allMeassge.toString();
	}

	public static String generateSHMessage(String dataName, String jarName) {

		StringBuffer allMeassge = new StringBuffer();
		allMeassge.append("#!/bin/sh\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Specify the shell to use, by default it uses bash\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Specify the name for your job name, this is the job name by which grid engine will \n");
		allMeassge
				.append("# refer to your job, this could be different from name of your executable or name of your script file\n");
		allMeassge.append("#$ -N LS_");
		allMeassge.append(dataName);
		allMeassge.append("\n");
		allMeassge.append("#\n");

		allMeassge.append("#$ -pe shared 8\n");
		allMeassge.append("#\n");

		allMeassge.append("#$ -l mf=10G\n");
		allMeassge.append("#\n");

		allMeassge
				.append("# Join the standard output & error files into one file (y)[yes] or write to separate files (n)[no]\n");
		allMeassge.append("# The default is n [no]\n");
		allMeassge.append("#$ -j y\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Use the directory from where the job is submitted\n");
		allMeassge.append("#$ -cwd\n");
		allMeassge.append("#\n");

		allMeassge.append("# Specify the queue where the job is submitted\n");
		allMeassge.append("## -q all.q\n");

		allMeassge
				.append("# The output path and file name if different from job name\n");
		allMeassge.append("#$ -o coutput_LS_");
		allMeassge.append(dataName);
		allMeassge.append("\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Send mail to: (Replace the edu.gmu-user-id with your user-id)\n");
		allMeassge.append("#$ -M xwang24@edu.gmu.edu\n");
		allMeassge.append("#\n");
		allMeassge
				.append("# Send mail at 'b' = submission , 'e' = completion, 's' = suspend, 'a' = aborted events, \n");
		allMeassge.append("# you can also combine this arguments like below\n");
		allMeassge.append("#$ -m ae\n");
		allMeassge.append("#\n");
		allMeassge.append("# Start the job\n");
		allMeassge.append("#  your application or script goes here");
		allMeassge.append("\n");
		allMeassge.append("\n");
		allMeassge.append("\n");

		allMeassge.append("java -jar ");
		allMeassge.append("\"");
		allMeassge.append(jarName);
		allMeassge.append("\" ");
		allMeassge.append(dataName);

		allMeassge.append(" ../data/");
		allMeassge.append(dataName);
		allMeassge.append("/");
		allMeassge.append(dataName);
		allMeassge.append("_TRAIN");

		allMeassge.append(" ../data/");
		allMeassge.append(dataName);
		allMeassge.append("/");
		allMeassge.append(dataName);
		allMeassge.append("_TEST EXACT");

		allMeassge.append(" >> ");
		allMeassge.append("logls");
		allMeassge.append(dataName);
		allMeassge.append(".txt");

		// System.out.println(allMeassge);
		return allMeassge.toString();
	}
}
