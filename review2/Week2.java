package net.chrisreading.review2;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

public class Week2 {
	
	public static void main(String[] args) {
		String[][] fileData = readCSV("SLCDecember2020Temperatures.csv");
		
		String report = buildReport(fileData);		
		String graph = buildGraph(fileData);
		System.out.println(report);
		System.out.println(graph);
		
		writeReportFile(report, graph);
	}
	
	private static String[][] readCSV(String path) {
		String[][] fileData = new String[31][3];
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(Week2.class.getResourceAsStream(path)))) {
			String line;
			int i = 0;
			while((line = reader.readLine()) != null) {
				String[] tokens = line.split(",");
				for(int j = 0; j < 3; j++) {
					fileData[i][j] = tokens[j];
				}
				i++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return fileData;
	}
	
	private static void writeReportFile(String... data) {
		try(FileWriter writer = new FileWriter("TemperaturesReport.txt")) {
			for(String s : data) {
				writer.write(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String buildReport(String[][] data) {
		String format = "%-4s %-5s %-4s %-12s\n";
		StringBuilder sb = new StringBuilder();
		sb.append("--------------------------------------------------------------\n");
		sb.append("December 2020: Temperatures in Utah\n");
		sb.append("--------------------------------------------------------------\n");
		sb.append(String.format(format, "Day", "High", "Low", "Variance"));
		sb.append("--------------------------------------------------------------\n");
		
		int avgHigh = 0;
		int avgLow = 0;
		int highestTempDay = 0;
		int highestTemp = 0;
		int lowestTempDay = 0;
		int lowestTemp = Integer.MAX_VALUE;
		
		for(int i = 0; i < data.length; i++) {
			int day = Integer.parseInt(data[i][0]);
			int high = Integer.parseInt(data[i][1]);
			int low = Integer.parseInt(data[i][2]);
			int variance = high - low;
			sb.append(String.format(format, day, high, low, variance));
			
			if(highestTemp < high) {
				highestTemp = high;
				highestTempDay = day;
			}
			
			if(lowestTemp > low) {
				lowestTemp = low;
				lowestTempDay = day;
			}
			
			avgHigh += high;
			avgLow += low;
		}
		
		sb.append("--------------------------------------------------------------\n");
		sb.append(String.format("December Highest Temperature: 12/%d: %d Average Hi: %.1f\n", highestTempDay, highestTemp, (float) avgHigh / data.length));
		sb.append(String.format("December Lowest Temperature: 12/%d: %d Average Lo: %.1f", lowestTempDay, lowestTemp, (float) avgLow / data.length));
		return sb.toString();
	}
	
	private static String buildGraph(String[][] data) {
		StringBuilder sb = new StringBuilder();
		sb.append("--------------------------------------------------------------\n");
		sb.append("Graph\n");
		sb.append("--------------------------------------------------------------\n");
		sb.append(generateTempHeader());
		sb.append(generateLineHeader());
		sb.append("--------------------------------------------------------------\n");
		
		for(int i = 0; i < data.length; i++) {
			int day = Integer.parseInt(data[i][0]);
			int high = Integer.parseInt(data[i][1]);
			int low = Integer.parseInt(data[i][2]);
			
			sb.append(generateHighLowLine(high, low, day));
		}
		
		sb.append("--------------------------------------------------------------\n");
		sb.append(generateLineHeader());
		sb.append(generateTempHeader());
		sb.append("--------------------------------------------------------------\n");
		
		return sb.toString();
	}
	
	private static String generateTempHeader() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i <= 50; i += 5) {
			if(i == 0) {
				sb.append(String.format("%7s", 1));
			} else {
				sb.append(String.format("%5s", i));
			}
		}
		sb.append("\n");
		return sb.toString();
	}
	
	private static String generateLineHeader() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i <= 50; i += 5) {
			if(i == 0) {
				sb.append(String.format("%7s", "|"));
			} else {
				sb.append(String.format("%5s", "|"));
			}
		}
		sb.append("\n");
		return sb.toString();
	}
	
	private static String generateHighLowLine(int high, int low, int day) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%02d %-3s", day, "Hi"));
		for(int j = 0; j < high; j++) {
			sb.append("+");
		}
		sb.append("\n");
		sb.append(String.format("%5s ", "Lo"));
		for(int j = 0; j < low; j++) {
			sb.append("-");
		}
		sb.append("\n");
		return sb.toString();
	}

}
