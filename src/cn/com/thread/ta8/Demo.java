package cn.com.thread.ta8;

/**
 * 使用wait/notify交替重复输出a、b、c
 * @author lilibo
 *
 */
public class Demo {
	
	private String str = "a";
	
	public synchronized void printA() throws InterruptedException {
		while(!str.equals("a")) {
			wait();
		}
		System.out.println("threadName = " + Thread.currentThread().getName() + " 打印a");
		str = "b";
		notifyAll();
	}
	
	public synchronized void printB() throws InterruptedException {
		while(!str.equals("b")) {
			wait();
		}
		System.out.println("threadName = " + Thread.currentThread().getName() + " 打印b");
		str = "c";
		notifyAll();
	}
	
	public synchronized void printC() throws InterruptedException {
		while(!str.equals("c")) {
			wait();
		}
		System.out.println("threadName = " + Thread.currentThread().getName() + " 打印c");
		str = "a"; // 重新置为a,以便下次循环
		notifyAll();
	}
	
	public static void main(String[] args) {
		Demo demo = new Demo();
		new Thread(() ->{
			while(true) {
				try {
					Thread.sleep(100);
					demo.printA();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "printA").start();
		
		new Thread(()->{
			while(true) {
				try {
					Thread.sleep(100);
					demo.printB();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "printB").start();
		
		new Thread(() -> {
			while(true) {
				try {
					Thread.sleep(100);
					demo.printC();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "printC").start();
	}
	
}