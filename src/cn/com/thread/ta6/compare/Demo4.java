package cn.com.thread.ta6.compare;

public class Demo4 {
	
	private volatile int signal; 
	
	public synchronized void modify() {
		try {
			Thread.sleep(3 * 1000);
			signal = 1;
			System.out.println("The current Thread Name = " + Thread.currentThread().getName() + "修改singal = 1!!!");
			notifyAll();
		}catch(Exception e) {
			
		}
	}
	
	public synchronized void get() {
		try {
			while (signal != 1) {
				wait(10 * 1000);
			}
			System.out.println("The current Thread Name = " + Thread.currentThread().getName() + "开始执行!!!");
		}catch(Exception e) {
			
		}
	}

	public static void main(String[] args) {
		Demo4 demo4 = new Demo4();
		for(int i = 0; i < 5; i++) {
			final int j = i;
			new Thread(()->{
				demo4.get();
			}, "get-" + j).start();
		}
		
		new Thread(()->{
			demo4.modify();
		}, "modify").start();
	}

}
