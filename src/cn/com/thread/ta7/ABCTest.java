package cn.com.thread.ta7;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 交替输出ABC
 * @author lilibo
 *
 */
public class ABCTest {

	public static void main(String[] args) {
		ABC1 abc = new ABC1();
		new Thread(()->{
			while(true)
				try {
					abc.printA();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}, "printA").start();

		new Thread(()->{
			while(true)
				try {
					abc.printB();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}, "printB").start();
		
		new Thread(()->{
			while(true)
				try {
					abc.printC();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}, "printC").start();
	}

}

class ABC {
	
	String flag = "A";
	
	public synchronized void printA() throws InterruptedException {
		while(!flag.equals("A")) {
			wait();
		}
		System.out.println(Thread.currentThread().getName() + ": A");
		flag = "B";
		notifyAll();
	}
	
	public synchronized void printB() throws InterruptedException {
		while(!flag.equals("B")) {
			wait();
		}
		System.out.println(Thread.currentThread().getName() + ": B");
		flag = "C";
		notifyAll();
	}
	
	public synchronized void printC() throws InterruptedException {
		while(!flag.equals("C")) {
			wait();
		}
		System.out.println(Thread.currentThread().getName() + ": C");
		flag = "A";
		notifyAll();
	}

}

class ABC1 {
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private final Condition aCondition = lock.newCondition();
	
	private final Condition bCondition = lock.newCondition();
	
	private final Condition cCondition = lock.newCondition();
	
	private String flag = "A";
	
	public void printA() throws InterruptedException {
		try {
			lock.lock();
			while(!flag.equals("A")) {
				aCondition.await();
			}
			System.out.println(Thread.currentThread().getName() + ": A");
			flag = "B";
			bCondition.signalAll();
		}finally {
			lock.unlock();
		}
	}
	
	public void printB() throws InterruptedException {
		try {
			lock.lock();
			while(!flag.equals("B")) {
				bCondition.await();
			}
			System.out.println(Thread.currentThread().getName() + ": B");
			flag = "C";
			cCondition.signalAll();
		}finally {
			lock.unlock();
		}
	}
	
	public void printC() throws InterruptedException {
		try {
			lock.lock();
			while(!flag.equals("C")) {
				cCondition.await();
			}
			System.out.println(Thread.currentThread().getName() + ": C");
			flag = "A";
			aCondition.signalAll();
		}finally {
			lock.unlock();
		}
	}
}