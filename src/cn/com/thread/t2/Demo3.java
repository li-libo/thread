package cn.com.thread.t2;

/**
 * 创建线程的第3种方式:匿名内部类
 * @author lilibo
 *
 */
public class Demo3 {

	public static void main(String[] args) {
		Thread t1 = new Thread("匿名1") {
			@Override
			public void run() {
				System.out.println("The current Thread Name is " + Thread.currentThread().getName());
			}
		};
		t1.start();
		
		Thread t2 = new Thread(new Runnable() {
			@Override
			public void run() {
				System.out.println("The current Thread Name is " + Thread.currentThread().getName());
			}
		}, "匿名2");
		t2.start();
		
		Thread t3 = new Thread(()->{
			System.out.println("Runnable run..., the current Thread name is " + Thread.currentThread().getName());
		}, "匿名3") {
			public void run() {
				System.out.println("Thread run..., the current Thread name is " + Thread.currentThread().getName());
			}
		};
		t3.start();
		t3.run();
	}

}
