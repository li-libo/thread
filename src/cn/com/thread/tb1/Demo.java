package cn.com.thread.tb1;

/**
 * join示例
 * @author lilibo
 *
 */
public class Demo {
	
	public void target(Thread joinThread) throws InterruptedException {
		System.out.println("The currentThreadName = " + Thread.currentThread().getName());
		// 启动joinThread线程
		joinThread.start();
		// 等待joinThread结束
		joinThread.join();
		System.out.println("joinThreadName = " + joinThread.getName() + "结束!");
	}
	
	public static void main(String[] args) {
		Thread joinThread = new Thread(()->{
			System.out.println("joinThread开始执行...");
			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "joinThread");
		
		Demo demo = new Demo();
		new Thread(()->{
			try {
				demo.target(joinThread);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "targetThread").start();
	}

}
