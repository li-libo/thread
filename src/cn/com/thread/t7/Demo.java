package cn.com.thread.t7;

/**
 * 保证可见性的前提
 * 
 * 多个线程拿到的是同一把锁，否则是保证不了的。
 * 
 * @author worker
 *
 */
public class Demo {

	public volatile int a = 1; // volatile不能保证原子性操作,譬如a++; 也不能保证操作的互斥性 

	public int getA() {
		return a++;
	}

	public void setA(int a) {
		try {
			Thread.sleep(2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.a = a;
	}

	public static void main(String[] args) throws InterruptedException {
		Demo demo = new Demo();
		new Thread(() -> demo.setA(10)).start();
		//demo.a = 10;
		new Thread(()->{
			//System.out.println(demo.getA());
			System.out.println(demo.a);
		}).start();
		Thread.sleep(100);
		System.out.println("主线程" + demo.getA());
	}

}
