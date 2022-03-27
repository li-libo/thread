package cn.com.thread.ta8;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 使用交替输出a、b 、c
 * @author lilibo
 *
 */
public class Demo2 {

	private ReentrantLock reentrantLock = new ReentrantLock();
	
	private String str = "a";
	
	// 打印a的等待条件A
	private Condition conditionA = reentrantLock.newCondition(); 

	// 打印b的等待条件B
	private Condition conditionB = reentrantLock.newCondition();
	
	// 打印c的等待条件C
	private Condition conditionC = reentrantLock.newCondition();
	
	public void printA() throws InterruptedException {
		if(reentrantLock.tryLock(1, TimeUnit.MINUTES)) {
			try {
				reentrantLock.lock();
				while(!str.equals("a")) {
					conditionA.await();
				}
				System.out.println("threadName = " + Thread.currentThread().getName() + "打印a");
				str = "b";
				conditionB.signal();// 唤醒在conditionB上的等待线程
			}finally {
				reentrantLock.unlock();
			}
		}
	}
	
	public void printB() throws InterruptedException {
		if(reentrantLock.tryLock(1, TimeUnit.MINUTES)) {
			try {
				reentrantLock.lock();
				while(!str.equals("b")) {
					conditionB.await();
				}
				System.out.println("threadName = " + Thread.currentThread().getName() + "打印b");
				str = "c";
				conditionC.signal(); // 唤醒在conditionC上的等待线程
			}finally {
				reentrantLock.unlock();
			}
		}
	}
	
	public void printC() throws InterruptedException {
		if(reentrantLock.tryLock(1, TimeUnit.MINUTES)) {
			try {
				reentrantLock.lock();
				while(!str.equals("c")) {
					conditionC.await();
				}
				System.out.println("threadName = " + Thread.currentThread().getName()+ "打印c");
				str = "a";
				conditionA.signal(); // 唤醒在conditionA上的等待线程
			}finally {
				reentrantLock.unlock();
			}
		}
	}
	
	public static void main(String[] args) {
		Demo2 demo2 = new Demo2();
		
		new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(100);
					demo2.printA();
				} catch (InterruptedException e) {				
					e.printStackTrace();
				}				
			}
		}, "printA").start();
		
		new Thread(()->{
			while(true) {
				try {
					Thread.sleep(100);
					demo2.printB();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}				
			}
		}, "printB").start();
		
		new Thread(() ->{
			while(true) {
				try {
					Thread.sleep(100);
					demo2.printC();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}, "printC").start();
	}
	
}
