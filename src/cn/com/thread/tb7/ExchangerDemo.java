package cn.com.thread.tb7;

import java.util.Random;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

public class ExchangerDemo {
	
	private Random random = new Random();
	
	private String value1 = "12345";
	
	private String value2 = "#####";
	
	private String value3 = "*****";
	
	private String value4 = "@@@@@";
	
	public void getValue1(Exchanger<String> exchanger) throws InterruptedException {
		System.out.println("current threadName = " + Thread.currentThread().getName() + "开始获取数据...");
		TimeUnit.MILLISECONDS.sleep(random.nextInt(5) * 1000);
		String valueOfAnotherThread = exchanger.exchange(value1);
		System.out.println("current threadName = " + Thread.currentThread().getName() + ", 其他线程提供的值为: " + valueOfAnotherThread);
		if(!value1.equals(valueOfAnotherThread)) {
			System.out.println("current threadName = " + Thread.currentThread().getName() + "与其他线程对比的值不同!");
		}
	}
	
	public void getValue2(Exchanger<String> exchanger) throws InterruptedException {
		System.out.println("current threadName = " + Thread.currentThread().getName() + "开始获取数据...");
		TimeUnit.MILLISECONDS.sleep(random.nextInt(5) * 1000);
		String valueOfAnotherThread = exchanger.exchange(value2);
		System.out.println("current threadName = " + Thread.currentThread().getName() + ", 其他线程提供的值为: " + valueOfAnotherThread);
		if(!value2.equals(valueOfAnotherThread)) {
			System.out.println("current threadName = " + Thread.currentThread().getName() + "与其他线程对比的值不同!");
		}
	}
	
	public void getValue3(Exchanger<String> exchanger) throws InterruptedException {
		System.out.println("current threadName = " + Thread.currentThread().getName() + "开始获取数据...");
		TimeUnit.MILLISECONDS.sleep(random.nextInt(5) * 1000);
		String valueOfAnotherThread = exchanger.exchange(value3);
		System.out.println("current threadName = " + Thread.currentThread().getName() + ", 其他线程提供的值为: " + valueOfAnotherThread);
		if(!value3.equals(valueOfAnotherThread)) {
			System.out.println("current threadName = " + Thread.currentThread().getName() + "与其他线程对比的值不同!");
		}
	}
	
	public void getValue4(Exchanger<String> exchanger) throws InterruptedException {
		System.out.println("current threadName = " + Thread.currentThread().getName() + "开始获取数据...");
		TimeUnit.MILLISECONDS.sleep(random.nextInt(5) * 1000);
		String valueOfAnotherThread = exchanger.exchange(value4);
		System.out.println("current threadName = " + Thread.currentThread().getName() + ", 其他线程提供的值为: " + valueOfAnotherThread);
		if(!value4.equals(valueOfAnotherThread)) {
			System.out.println("current threadName = " + Thread.currentThread().getName() + "与其他线程对比的值不同!");
		}
	}
	
	public static void main(String[] args) {
		ExchangerDemo exchangerDemo = new ExchangerDemo();
		Exchanger<String> exchanger = new Exchanger<>();
		
		new Thread(() -> {
			try {
				exchangerDemo.getValue1(exchanger);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "t1").start();
		
		new Thread(() -> {
			try {
				exchangerDemo.getValue2(exchanger);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "t2").start();
		
//		new Thread(() -> {
//			try {
//				exchangerDemo.getValue3(exchanger);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}, "t3").start();
//		
//		new Thread(() -> {
//			try {
//				exchangerDemo.getValue3(exchanger);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}, "t4").start();
	}

}
