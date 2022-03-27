package cn.com.thread.ta6;

public class Demo2 {
	
	private volatile int singal;
	
	public int getSingal() {
		return singal;
	}

	public void setSingal(int singal) {
		this.singal = singal;
	}

	public static void main(String[] args) {		
		Demo2 demo2 = new Demo2();
		new Thread(()->{
			synchronized (demo2) {
				try {
					Thread.sleep(1000);
					System.out.println("the current Thread Name = " + Thread.currentThread().getName() + ", 开始修改singal!!!");
					demo2.setSingal(1);
					demo2.notify(); // 调用当前锁对象的wait/notify
				}catch(Exception e) {
					
				}
			}			
		}, "a").start();
		
		new Thread(()->{
			synchronized (demo2) {
				try {
					while (demo2.getSingal() != 1) {
						demo2.wait(); // 调用当前锁对象的wait/notify
					}
					System.out.println("the current Thread Name = " + Thread.currentThread().getName() + ", 开始执行!!!");
				}catch(Exception e) {
					
				}
			}
		}, "b").start();
	}

}
