package cn.com.thread.ta4;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Main {
	
	public static final int numOfTestThread = 5;

	private int value;
	private MyLock5 lock = new MyLock5(true);

	public int getNextId() {
		lock.lock();
		try {
			// Thread.sleep(100);
			return value++;
		} catch (Exception e) {
			throw new RuntimeException();
		} finally {
			lock.unlock();
		}
	}
	
	public void a() {
		lock.lock();
		System.out.println("the current Thread Name = " + Thread.currentThread().getName() + ": a");
		b();
		lock.unlock();
	}
	
	private void b() {
		lock.lock();
		System.out.println("the current Thread Name = " + Thread.currentThread().getName() + ": b");
		c();
		lock.unlock();
	}
	
	private void c() {
		lock.lock();
		System.out.println("the current Thread Name = " + Thread.currentThread().getName() + ": c");
		lock.unlock();
	}

	public static void main(String[] args) {
		Set<String> idSet = Collections.synchronizedSet(new HashSet<>());
		Main m = new Main();
		for(int i = 0; i < numOfTestThread; i++) {
			final int j = i;
			new Thread(()->{
				while(true) {
					String format = "the Current Thread Name = %s, nextId = %s";
					String nextId = m.getNextId() + "";					
					System.out.println(String.format(format, Thread.currentThread().getName(), nextId));
					if(!idSet.add(nextId)) {
						System.out.println("出现重复数据: " + String.format(format, Thread.currentThread().getName(), nextId));
					}
				}
			}, "getNextId-" + j).start();
		}
		
		for(int i = 0; i< numOfTestThread; i++) {
			final int j = i;
			new Thread(()->{
				while(true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					m.a();
				}
			}, "reentry-" + j).start();
		}
	}

}
