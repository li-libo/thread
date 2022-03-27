package cn.com.thread.t6;

/**
 * 死锁示例
 * @author lilibo
 *
 */
public class Demo3 {

	private final Object lock1 = new Object();
	
	private final Object lock2 = new Object();
	
	public void call1() throws InterruptedException {
		synchronized (lock1) {
			Thread.sleep(100);
			synchronized (lock2) {
				System.out.println("call1, the current thread name = " + Thread.currentThread().getName());
			}
		}
	}
	
	public void call2() throws InterruptedException {
		synchronized (lock2) {
			Thread.sleep(100);
			synchronized (lock1) {
				System.out.println("call2, the current thread name = " + Thread.currentThread().getName());
			}
		}
	}
	
	public static void main(String[] args) {
		Demo3 demo3 = new Demo3();
		new Thread(()-> {
			try {
				demo3.call1();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
		new Thread(()-> {
			try {
				demo3.call2();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}
	
}
