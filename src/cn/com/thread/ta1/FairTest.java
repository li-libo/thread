package cn.com.thread.ta1;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FairTest {
	
	final Lock lock = new ReentrantLock(true);
	
	public static final int numOfTest = 10;
	
	public void a () {
		lock.lock();
		System.out.println(Thread.currentThread().getName() + "   a");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		lock.unlock();
	}
	
	public static void main(String[] args) {		
		FairTest fairTest = new FairTest();
		for(int i = 0; i < numOfTest; i++) {
			new Thread(new Runnable() {			
				@Override
				public void run() {
					while(true)
					fairTest.a();
				}
			}, "test-" + i).start();	
		}		
	}

}
