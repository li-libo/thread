package cn.com.thread.t2;

import java.util.concurrent.TimeUnit;

/**
 * 创建线程的多种方式:继承Thread类
 * 测试线程中断
 * @author lilibo
 *
 */
public class Demo1 extends Thread{
	
	public Demo1(String name) {
		super(name);
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				Thread.sleep(100);
				System.out.println("thread name = " + Thread.currentThread().getName());
			} catch (InterruptedException e) {
				//异常会清理中断标志
				e.printStackTrace();
				System.out.println("当前线程是否被中断? " + Thread.currentThread().isInterrupted());
				Thread.currentThread().interrupt();
			}			
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		Demo1 t1 = new Demo1("t1");
		Demo1 t2 = new Demo1("t2");
		t1.start();
		t2.start();
		TimeUnit.SECONDS.sleep(1);
		t1.interrupt();
	}

}
