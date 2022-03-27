package cn.com.thread.ta7;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class DemoTest {

	public static void main(String[] args) {
		Shop1 shop = new Shop1();
		for(int numOfPush = 0; numOfPush < 1; numOfPush++) {
			final int j = numOfPush;
			new Thread(()->{
				try {
					while(true) {
						//Thread.sleep(100);
						shop.push();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}, "push-"+j).start();
		}
		
		for(int numOfTake = 0; numOfTake < 2; numOfTake++) {
			final int j = numOfTake;
			new Thread(()->{
				try {
					while(true)
						shop.take();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}, "take-"+j).start();
		}
	}

}

class Shop {
	
	public static final int MAX = 10;
	
	private volatile int count;
	
	public synchronized void push() throws InterruptedException {
		while(count >= MAX) {
			System.out.println("the current Thread name = " + Thread.currentThread().getName() + "商店已满!!!");
			wait();
		}
		count++;
		notifyAll();
	}
	
	public synchronized void take() throws InterruptedException {
		while(count <= 0) {
			System.out.println("the current Thread name = " + Thread.currentThread().getName() + "商店已空!!!");
			wait();
		}
		count--;
		notifyAll();
	}

}

class Shop1 {
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private final Condition pushCondition = lock.newCondition();
	
	private final Condition takeCondition = lock.newCondition();
	
	public static final int MAX = 10;
	
	private volatile int count;
	
	public void push() throws InterruptedException {
		try {
			lock.lock();
			while(count >= MAX) {
				System.out.println("the current Thread name = " + Thread.currentThread().getName() + "商店已满!!!");
				pushCondition.await();
			}
			count++;
			takeCondition.signalAll();
		}finally {
			lock.unlock();
		}
	}
	
	public void take() throws InterruptedException {
		try {
			lock.lock();
			while(count <= 0) {
				System.out.println("the current Thread name = " + Thread.currentThread().getName() + "商店已空!!!");
				takeCondition.await();
			}
			count--;
			pushCondition.signalAll();
		}finally {
			lock.unlock();
		}
	}
}