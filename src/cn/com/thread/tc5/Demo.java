package cn.com.thread.tc5;

import cn.com.thread.tc2.Demo2;

/**
 *  对于volatile变量的写 happens-before 对于volatile变量的读
 * @author lilibo
 */
public class Demo {
	
	private int a;
	private volatile boolean flag;
	
	public void writer () {
		a = 1; // 1
		flag = true; // 2 当写一个volatile变量时，Java内存模型会把该线程对应的本地缓存变量值刷新到主内存中
	}
	
	public void reader () {
		if(flag) { // 3 当读一个volatile变量时，Java内存模型会把当前线程对应的本地缓存变量值设置为无效，然后从主内存中读取共享变量。
			int b = a + 1; // 4
			System.out.println(b); // 5
		}
	}
	
	public static void main(String[] args) {
		Demo2 demo2 = new Demo2();
		new Thread(() -> demo2.writer()).start();
		new Thread(() -> demo2.reader()).start();
	}
	
}
