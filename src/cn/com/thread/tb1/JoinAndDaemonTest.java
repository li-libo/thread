package cn.com.thread.tb1;

import java.util.concurrent.TimeUnit;

public class JoinAndDaemonTest {

	public static void main(String[] args) {
		Thread joinThread = new Thread(()->{
			try {
				System.out.println("the current thread name = " + Thread.currentThread().getName());
				TimeUnit.SECONDS.sleep(10);				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "joinThread");
		
		new Thread(()->{			
			try {
				System.out.println("the current thread name = " + Thread.currentThread().getName());
				joinThread.start();
				joinThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "testJoin").start();
		
		Thread daemonThread = new Thread(()->{
			while(true) {
				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}, "daemon");
		daemonThread.setDaemon(true);
		daemonThread.start();
	}

}
