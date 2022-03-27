package cn.com.thread.tc4;

/**
 * 程序次序规则 
 * 监视器规则
 * 传递性
 * @author worker
 *
 */
public class Demo {
	
	private int value;
	
	/*
	 * 锁除了让临界区互斥执行外，还可以让释放锁的线程向获取同一个锁的线程发送消息。
	 */
	public synchronized void a() { // 1  获取锁
		value ++; // 2
	} // 3 释放锁
	
	public synchronized void b() { // 4 获取锁
		int a = value; // 5
		// 处理其他的操作 
		System.out.println("a = " + a);
	} // 6 释放锁
	
}
