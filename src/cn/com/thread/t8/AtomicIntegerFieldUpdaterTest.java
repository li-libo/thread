package cn.com.thread.t8;

import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class AtomicIntegerFieldUpdaterTest {

	public static final int numOfThread = 10;

	private AtomicIntegerFieldUpdater<Pig> ageField = AtomicIntegerFieldUpdater.newUpdater(Pig.class, "age");

	public static void main(String[] args) {
		AtomicIntegerFieldUpdaterTest test = new AtomicIntegerFieldUpdaterTest();
		Pig pig = new Pig();
		for (int i = 0; i < numOfThread; i++) {
			new Thread(() -> {
				for (int j = 0; j < 10000; j++) {
					test.ageField.incrementAndGet(pig);
				}
			}).start();
		}

		while (Thread.activeCount() > 1) {

		}
		System.out.println("pig age = " + test.ageField.get(pig));
	}

}
