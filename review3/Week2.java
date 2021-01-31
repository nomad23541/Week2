package net.chrisreading.review3;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Month;
import java.util.Scanner;

public class Week2 {

	public static void main(String[] args) {
		String[][] data = null;
		try(Scanner input = new Scanner(System.in)) {
			int selection = 0;
			do {
				System.out.println("Select which month to run report for:");
				System.out.println("1. December 2020");
				System.out.println("2. November 2020");
				System.out.print("Selection: ");
				selection = input.nextInt();
			} while (selection < 1 || selection > 2);
			
			data = loadData(selection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		String report = buildReport(data);
		String graph = buildGraph(data);
		System.out.println(report);
		System.out.println(graph);

		writeReportFile(report, graph);
	}

	private static String[][] loadData(int selection) throws SQLException {
		String connUrl = "jdbc:mysql://127.0.0.1:3306/practice";
		String login = "root";
		String password = "password";

		int month = 0;
		if (selection == 1) {
			month = 12;
		} else if (selection == 2) {
			month = 11;
		}

		String query = "SELECT month, day, year, hi, lo FROM temperatures WHERE month = " + month
				+ " AND year = 2020 ORDER BY month, day, year;";

		Connection conn = DriverManager.getConnection(connUrl, login, password);
		if (conn != null) {
			try (Statement statement = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_UPDATABLE); 
				 ResultSet rs = statement.executeQuery(query)) {
				
				int numRows;
				int numCols = 5;
				rs.last();
				numRows = rs.getRow();
				rs.first();
				
				String[][] data = new String[numRows][numCols];
				for(int i = 0; i < numRows; i++) {
					data[i][0] = rs.getString("month");
					data[i][1] = rs.getString("day");
					data[i][2] = rs.getString("year");
					data[i][3] = rs.getString("hi");
					data[i][4] = rs.getString("lo");
					rs.next();
				}
				
				return data;
			}
		}
		
		return null;
	}

	private static void writeReportFile(String... data) {
		try (FileWriter writer = new FileWriter("TemperaturesReport.txt")) {
			for (String s : data) {
				writer.write(s);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String buildReport(String[][] data) {
		String monthString = Month.of(Integer.parseInt(data[0][0])).name();
		String format = "%-12s %-5s %-4s %-12s\n";
		StringBuilder sb = new StringBuilder();
		sb.append(generateLine(60));
		sb.append(monthString + " 2020: Temperatures in Utah\n");
		sb.append(generateLine(60));
		sb.append(String.format(format, "Date", "High", "Low", "Variance"));
		sb.append(generateLine(60));
		
		int month = 0;
		int avgHigh = 0;
		int avgLow = 0;
		int highestTempDay = 0;
		int highestTemp = 0;
		int lowestTempDay = 0;
		int lowestTemp = Integer.MAX_VALUE;

		for (int i = 0; i < data.length; i++) {
			month = Integer.parseInt(data[i][0]);
			int day = Integer.parseInt(data[i][1]);
			int year = Integer.parseInt(data[i][2]);
			int high = Integer.parseInt(data[i][3]);
			int low = Integer.parseInt(data[i][4]);
			int variance = high - low;
			
			sb.append(String.format(format, month + "/" + day + "/" + year, high, low, variance));

			if (highestTemp < high) {
				highestTemp = high;
				highestTempDay = day;
			}

			if (lowestTemp > low) {
				lowestTemp = low;
				lowestTempDay = day;
			}

			avgHigh += high;
			avgLow += low;
		}

		sb.append(generateLine(60));
		sb.append(String.format("%s Highest Temperature: %d/%d: %d Average Hi: %.1f\n", monthString, month, highestTempDay,
				highestTemp, (float) avgHigh / data.length));
		sb.append(String.format("%s Lowest Temperature: %d/%d: %d Average Lo: %.1f", monthString, month, lowestTempDay, lowestTemp,
				(float) avgLow / data.length));
		return sb.toString();
	}

	private static String buildGraph(String[][] data) {
		StringBuilder sb = new StringBuilder();
		sb.append(generateLine(60));
		sb.append("Graph\n");
		sb.append(generateLine(60));
		sb.append(generateTempHeader());
		sb.append(generateLineHeader());
		sb.append(generateLine(60));

		for (int i = 0; i < data.length; i++) {
			int day = Integer.parseInt(data[i][1]);
			int high = Integer.parseInt(data[i][3]);
			int low = Integer.parseInt(data[i][4]);

			sb.append(generateHighLowLine(high, low, day));
		}

		sb.append(generateLine(60));
		sb.append(generateLineHeader());
		sb.append(generateTempHeader());
		sb.append(generateLine(60));

		return sb.toString();
	}

	private static String generateTempHeader() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i <= 50; i += 5) {
			if (i == 0) {
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
		for (int i = 0; i <= 50; i += 5) {
			if (i == 0) {
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
		for (int j = 0; j < high; j++) {
			sb.append("+");
		}
		sb.append("\n");
		sb.append(String.format("%5s ", "Lo"));
		for (int j = 0; j < low; j++) {
			sb.append("-");
		}
		sb.append("\n");
		return sb.toString();
	}
	
	private static String generateLine(int dashes) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < dashes; i++) {
			sb.append("-");
		}
		
		sb.append("\n");
		return sb.toString();
	}

}
