package cn.com.thread.t7;

import java.util.concurrent.TimeUnit;

/**
 * volatile 可见性测试
 * @author lilibo
 */
public class Demo3 {
	
	private static boolean blockFlag = true;

	public static void main(String[] args) {
		// Demo3 demo3 = new Demo3();
		new Thread(()->{
			try {
				TimeUnit.SECONDS.sleep(3);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			blockFlag = false;
		}).start();
		
		new Thread(()->{
			while(blockFlag) {
				//System.out.println("blockFlag = " + blockFlag);
			}
			System.out.println("线程2执行了");
		}).start();
		
		new Thread(()->{
			while(blockFlag) {
				Thread.yield();
			}
			System.out.println("线程3执行了");
		}).start();
	}

}
