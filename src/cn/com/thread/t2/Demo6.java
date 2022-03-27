package cn.com.thread.t2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 创建线程的多种方式: 线程池的实现
 * @author lilibo
 *
 */
public class Demo6 {

	public static void main(String[] args) throws InterruptedException {

		ExecutorService threadPool = Executors.newCachedThreadPool();

		for (int i = 0; i < 1000; i++) {
			threadPool.execute(()->{
				System.out.println("the current Thread Name = " + Thread.currentThread().getName());
			});
		}
		Thread.sleep(5 * 1000);
		threadPool.shutdown();
	}

}
