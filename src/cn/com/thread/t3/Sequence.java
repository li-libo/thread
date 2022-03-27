package cn.com.thread.t3;

public class Sequence {
	
	/**
	 线程安全性问题
	   多线程环境下
	   多个线程共享一个资源
	   对资源进行非原子性操作
	 */
	
	private int value;
	
	/**
	 * synchronized 放在普通方法上，内置锁就是当前类的实例, 即this
	 * @return
	 */
	public synchronized int getNext() {
		return value++;
	}
	
	public synchronized int getValue() {
		return value;
	}
	
	/**
	 * 修饰静态方法，内置锁是当前的Class字节码对象
	 * Sequence.class
	 * @return
	 */
	public static synchronized int getPrevious() {
//		return value --;
		return 0;
	}
	
	public int xx () {		
		// monitorenter
		synchronized (Sequence.class) {			
			if(value > 0) {
				return value;
			} else {
				return -1;
			}
			
		}
		// monitorexit
		
	}
	
	public static void main(String[] args) {
		
		Sequence s = new Sequence();
//		while(true) {
//			System.out.println(s.getNext());
//		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					System.out.println(Thread.currentThread().getName() + " " + s.getNext());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		new Thread(new Runnable() {			
			@Override
			public void run() {
				while(true) {
					System.out.println(Thread.currentThread().getName() + " " + s.getNext());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					System.out.println(Thread.currentThread().getName() + " " + s.getNext());
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
	}

}
