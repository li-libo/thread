package cn.com.thread.t6;

import java.util.Random;

/**
 * 自旋锁示例
 * 多个线程执行完毕之后，打印一句话，结束
 * @author worker
 *
 */
public class Demo2 {
	
	public static void main(String[] args) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " 线程执行..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
				
				try {
					Thread.sleep(new Random().nextInt(2000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.out.println(Thread.currentThread().getName() + " 线程执行完毕了..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " 线程执行..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
				
				try {
					Thread.sleep(new Random().nextInt(2000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.out.println(Thread.currentThread().getName() + " 线程执行完毕了..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " 线程执行..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
				
				try {
					Thread.sleep(new Random().nextInt(2000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.out.println(Thread.currentThread().getName() + " 线程执行完毕了..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " 线程执行..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
				
				try {
					Thread.sleep(new Random().nextInt(2000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.out.println(Thread.currentThread().getName() + " 线程执行完毕了..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
			}
		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				System.out.println(Thread.currentThread().getName() + " 线程执行..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
				
				try {
					Thread.sleep(new Random().nextInt(2000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				System.out.println(Thread.currentThread().getName() + " 线程执行完毕了..." + ", ThreadGroup = " + Thread.currentThread().getThreadGroup());
			}
		}).start();
		
		while(Thread.activeCount() > 1) {
			// 自旋
			Thread.yield();
		}
		System.out.println("所有的线程执行完毕了...");
	}

}
