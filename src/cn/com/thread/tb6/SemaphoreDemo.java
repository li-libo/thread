package cn.com.thread.tb6;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphore控制并发度
 * @author lilibo
 *
 */
public class SemaphoreDemo {

	public static void main(String[] args) {
		SemaphoreDemo semaphoreDemo = new SemaphoreDemo();
		Semaphore semaphore = new Semaphore(10);
		ThreadGroup threadGroup = new ThreadGroup("getConnection");
		for(int i = 0; i < Runtime.getRuntime().availableProcessors() * 2; i++) {
			final int j = i;			
			new Thread(threadGroup, ()->{
				while(true) {
					try {
						semaphoreDemo.getConnection(semaphore);
					} catch (InterruptedException e) {
					}
				}
			}, "getConnection-"+j).start();
		}
		
		Thread checkNumOfThread = new Thread(()->{
			try {
				while(true) {
					TimeUnit.SECONDS.sleep(1);				
					System.out.println("threadGroup = getConnection 的activeCount = " + threadGroup.activeCount() + ", semaphore availablePermits = " + semaphore.availablePermits());
				}
			}catch(Exception e) {
				e.printStackTrace();
			}			
		}, "checkNumOfThread");
		checkNumOfThread.setDaemon(false);
		checkNumOfThread.start();
	}

	public void getConnection(Semaphore semaphore) throws InterruptedException {
		semaphore.acquire();
		TimeUnit.SECONDS.sleep(3);
		semaphore.release();
	}
}
