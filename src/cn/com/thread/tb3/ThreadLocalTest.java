package cn.com.thread.tb3;

import java.util.concurrent.TimeUnit;

public class ThreadLocalTest {

	private static final ThreadLocal<Integer> idThreadLocal = ThreadLocal.withInitial(() -> 1);

	public static String getNextId() {
		int oldValue = idThreadLocal.get();
		idThreadLocal.set(oldValue + 1);
		return oldValue + "";
	}

	public static void main(String[] args) {
		for (int numOfTest = 0; numOfTest < 3; numOfTest++) {
			final int j = numOfTest;
			new Thread(() -> {
				while (true) {
					try {
						TimeUnit.SECONDS.sleep(1);
						System.out.println("the current thread name = " + Thread.currentThread().getName() + ": " + getNextId());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}, "getNextId-" + j).start();
		}
	}

}
