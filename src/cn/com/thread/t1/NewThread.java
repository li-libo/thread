package cn.com.thread.t1;

public class NewThread implements Runnable {

	@Override
	public synchronized void run() {
		while(true) {
			System.out.println("The current Thread Name is " + Thread.currentThread().getName());
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("自定义的线程执行了....");
		}		
	}

	public static void main(String[] args) throws InterruptedException {

		NewThread n = new NewThread();
		//n.run();
		// 初始化状态
		Thread thread = new Thread(n); // 创建线程,并指定线程任务
		thread.start(); // 启动线程
		while(true) {
			synchronized (n) {
				System.out.println("主线程执行了...");
				try {
					//Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
				n.notifyAll();
			}
		}
	}

}
