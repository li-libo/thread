package cn.com.thread.tc3;

public class Demo3 {
	// start规则
	public void a() {
		System.out.println("a"); // 1 启动另外一个线程的线程
		new Thread(new Runnable() {			
			@Override
			public void run() {
				System.out.println("b"); // 2 
			}
		}).start();
	}
	
	public static void main(String[] args) {
		Demo3 demo3 = new Demo3();
		demo3.a();
	}

}
