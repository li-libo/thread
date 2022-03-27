package cn.com.thread.ta5;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Demo {

	private Map<String, Object> map = new HashMap<>();

	private ReadWriteLock rwl = new ReentrantReadWriteLock();

	private Lock r = rwl.readLock();
	private Lock w = rwl.writeLock();

	private volatile boolean isUpdate;

	/**
	 * 锁降级
	 * 锁降级是指写锁降级为读锁;在写锁没释放的时候,获取到读锁，再释放写锁
	 * 
	 * 锁升级(ReentrantReadWriteLock不支持)
	 * 把读锁升级为写锁,在读锁没有释放的时候,获取到写锁(ReentrantReadWriteLock拿不到写锁,读写互斥),在释放读锁
	 * 
	 * 一个获取了写锁的线程当然可以再进行读操作（即获得读锁），因为写锁是排他锁，自己写内容的时候别人还不能读，但是自己肯定能读自己写的内容
	在ReentrantReadWriteLock中的 tryAcquireShared()方法（即获取读锁的方法）中，会先进行判断是否其他线程已经获取写锁
	1) 如果已经有线程获取了写锁，则判断获取了写锁的线程是否是当前线程（自己），如果是就可以尝试获取读锁，如果不是就不能获取写锁
	2) 如果还没有线程获取写锁，则也可以尝试获取读锁
	 */
	public void readWrite() {
		r.lock(); // 为了保证isUpdate能够拿到最新的值
		if (isUpdate) {
			r.unlock();
			w.lock();
			map.put("xxx", "xxx");
			r.lock(); //为了防止其他写线程(读锁/写锁互斥)进入直到最终释放读锁; 锁降级是指写锁变成读锁
			w.unlock();
		}
		Object obj = map.get("xxx");
		System.out.println(obj);
		r.unlock(); // 最终释放读锁

	}

	public Object get(String key) {
		r.lock();
		System.out.println(Thread.currentThread().getName() + " 读操作在执行..");
		try {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return map.get(key);
		} finally {
			r.unlock();
			System.out.println(Thread.currentThread().getName() + " 读操执行完毕..");
		}
	}

	public void put(String key, Object value) {
		w.lock();
		System.out.println(Thread.currentThread().getName() + " 写操作在执行..");
		try {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			map.put(key, value);
		} finally {
			w.unlock();
			System.out.println(Thread.currentThread().getName() + " 写操作执行完毕..");
		}
	}

}
