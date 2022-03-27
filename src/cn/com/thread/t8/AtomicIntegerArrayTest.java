package cn.com.thread.t8;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicIntegerArrayTest {

	public static final int numOfTestThread = 5;

	private static int[] array = { 1, 3, 4, 5, 6 };

	private static AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(array);

	public static void main(String[] args) {
		
		for (int i = 0; i < numOfTestThread; i++) {
			final int index = i;
			new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					atomicIntegerArray.getAndAdd(index, 1);
				}
			}, "test-" + i).start();
		}
		while (Thread.activeCount() > 1) {

		}
		System.out.println("原始array数组:" + Arrays.toString(array) + ", atomicIntegerArray中数组为:" + atomicIntegerArray.toString());
	}

}
