package cn.com.thread.tb5;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

public class CyclicBarrierTest {
	
	public static final int parties = 10;
	
	public static void main(String[] args) throws InterruptedException {
		CyclicBarrier cyclicBarrier = new CyclicBarrier(parties, ()->{
			System.out.println("10人已到齐!!! the current thread name = " + Thread.currentThread().getName());
		});
		CyclicBarrierTest cyclicBarrierTest = new CyclicBarrierTest();
		for(int i =0; ; i++) {
			if(i == 0) {
				waitParties(cyclicBarrier, cyclicBarrierTest);
			}
			if(cyclicBarrier.getNumberWaiting() == 0) {
				System.out.println("准备重置cyclicBarrier!!!");
				TimeUnit.SECONDS.sleep(1);
				cyclicBarrier.reset();
				waitParties(cyclicBarrier, cyclicBarrierTest);
			}
		}	
	}

	private static void waitParties(CyclicBarrier cyclicBarrier, CyclicBarrierTest cyclicBarrierTest) {
		for(int num = 0; num < parties; num++) {
			final int j = num;
			new Thread(()->{
				try {
					System.out.println("the current thread name = " + Thread.currentThread().getName() + " waitting!!!");
					cyclicBarrier.await();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}, "person-" + j).start();
		}
	}

}
