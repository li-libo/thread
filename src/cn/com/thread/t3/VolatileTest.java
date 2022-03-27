package cn.com.thread.t3;

import java.util.concurrent.TimeUnit;

/**
 * volatile只能保证可见性,不能保证原子性
 * @author lilibo
 *
 */
public class VolatileTest {
	
	private volatile static int value = 0;

	public static void main(String[] args) {
		for(int i = 0; i < 20; i++) {
			new Thread(()->{
				for(int j = 0; j < 1000; j++) {
					try {
						TimeUnit.MILLISECONDS.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					value++;
				}
			}).start();
		}
		while(Thread.activeCount() > 1) {
			Thread.yield();
		}
		System.out.println("value = " + value);
	}

}
