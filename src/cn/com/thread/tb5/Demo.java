package cn.com.thread.tb5;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class Demo {

	private static int numOfMeeting = 100;

	private Random random = new Random();

	public void meeting(CyclicBarrier cyclicBarrier) throws InterruptedException, BrokenBarrierException {
		Thread.sleep(random.nextInt(5000));
		System.out.println("threadName = " + Thread.currentThread().getName() + "到达, 等待开始开会");
		cyclicBarrier.await();
		System.out.println("threadName = " + Thread.currentThread().getName() + "开始发言: are you ok?");
	}

	public static void main(String[] args) {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(numOfMeeting, () -> {
			System.out.println("好了, 人都到齐了, 我们开始开会... threadName = " + Thread.currentThread().getName());		
		});

		Demo demo = new Demo();
		for (int i = 0; i < numOfMeeting; i++) {
			new Thread(() -> {
				try {
					demo.meeting(cyclicBarrier);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}, "t" + i).start();
		}

		new Thread(() -> {
			while (true) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
					System.out.println("当前等待的线程数 = " + cyclicBarrier.getNumberWaiting());
					System.out.println("is broken ?" + cyclicBarrier.isBroken());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "monitor").start();
	}

}
