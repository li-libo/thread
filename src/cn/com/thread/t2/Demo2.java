package cn.com.thread.t2;
/**
 * 创建线程的多种方式: 实现Runnable接口
 * @author lilibo
 */
public class Demo2 implements Runnable{
	
	public static void main(String[] args) {
		Demo2 runn = new Demo2();
		Thread t1 = new Thread(runn);
		Thread t2 = new Thread(runn);
		t1.start();
		t2.start();
	}

	@Override
	public void run() {
		synchronized (this) {
			try {
				while(true) {
					Thread.sleep(100);
					System.out.println("the current thread name = " + Thread.currentThread().getName());
					this.notify();
					this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
			
	}

}
