package cn.com.thread.ta6;

public class Demo {
	
	private volatile int singal;

	public int getSingal() {
		return singal;
	}

	public void setSingal(int singal) {
		this.singal = singal;
	}

	public static void main(String[] args) {
		Demo demo = new Demo();
		new Thread(()->{
			try {
				Thread.sleep(1000);
				System.out.println("the current thread name = " + Thread.currentThread().getName() + "修改singal为1!!!");
				demo.setSingal(1);				
			}catch(Exception e) {
				
			}			
		}, "a").start();
		
		new Thread(()->{
			try {
				while(demo.getSingal() != 1) {
					System.out.println("the current thread name = " + Thread.currentThread().getName() + "等待...");
					Thread.sleep(100);
				}
				System.out.println("the current thread name = " + Thread.currentThread().getName() + "开始执行!!!");
			}catch(Exception e) {
				
			}			
		}, "b").start();
	}

}
