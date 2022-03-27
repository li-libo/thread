package cn.com.thread.tb4;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SumDemo {

	public static final String SUMFILE_PATH_NAME = "/Users/lilibo/eclipse-workspace/thread/src/cn/com/thread/tb4/nums.txt";

	private int[] array;

	public int mapReduceToSum(List<String> lineList) {
		int lineSize = lineList.size();
		array = new int[lineSize];
		for (int i = 0; i < lineSize; i++) {
			final int j = i;
			new Thread(() -> {
				String line = lineList.get(j).trim();
				List<String> numList = new ArrayList<String>(Arrays.asList(line.split(",")));
				int sum = numList.stream().mapToInt((x) -> Integer.parseInt(x)).sum();
				array[j] = sum;

			}).start();
		}
		int allSum = 0;
		while (Thread.activeCount() > 1) {

		}
		for (int o : array) {
			allSum += o;
		}
		return allSum;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		List<String> lineList = new ArrayList<>();
		try (BufferedReader read = new BufferedReader(new FileReader(SUMFILE_PATH_NAME))) {
			String line = null;
			while ((line = read.readLine()) != null) {
				lineList.add(line);
			}
		}
		SumDemo sumDemo = new SumDemo();
		String format = "求和结果为: %d";
		System.out.println(String.format(format, sumDemo.mapReduceToSum(lineList)));
	}

}
