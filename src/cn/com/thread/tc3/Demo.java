package cn.com.thread.tc3;

/**
Happens-before是用来指定两个操作之间的执行顺序。提供跨线程的内存可见性。
	在Java内存模型中，如果一个操作执行的结果需要对另一个操作可见，那么这两个操作之间必然存在happens-before关系。
	Happens-before规则如下:
		程序顺序规则: 单个线程中的每个操作，总是前一个操作happens-before于该线程中的任意后续操作
		监视器锁规则: 对一个锁的解锁，总是happens-before于随后对这个锁的加锁
		volatile变量规则: 对一个volatile域的写，happens-before于任意后续对这个volatile域的读
		传递性:
			A happens-before B
			B happens-before C
			则
			A happens-before C
		Start规则:
		Join规则:
 * @author lilibo
 *
 */
public class Demo {
	
	private int a;
	private int b;
	private int c;
	
	/**
	 * 1 happens-before 2
	 * 2 happens-before 3
	 * 3 happens-before 4
	 * ...
	 */
	public void a () {
		a = 2;  // 1
		b = 10; // 2
		
		c = a + b; // 3
		
		System.out.println(c);  // 4
	}
	
	public static void main(String[] args) {
		new Demo().a();
	}

}
