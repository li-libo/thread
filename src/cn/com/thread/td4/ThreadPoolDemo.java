package cn.com.thread.td4;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolDemo {
	
	public static final int coreSize = 5;
	
	public static final int maxSize = 10;
	
	public static final int capacity = 5;

	public static void main(String[] args) throws InterruptedException {
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(coreSize, maxSize, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5));	
		for(int i = 0; i< maxSize + capacity; i++) {
			final int j = i;
			threadPoolExecutor.execute(()->{									
				try {
					System.out.println("the current thread name = " + Thread.currentThread().getName() + "正在执行task-" + j + "...");
					TimeUnit.SECONDS.sleep(3);
					System.out.println("the cuurent thread name = " + Thread.currentThread().getName() + "执行完毕task-" + j + "!!!");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			});
			System.out.println("线程池中线程数目:" + threadPoolExecutor.getPoolSize() + ", 队列中等待执行任务数目: " + threadPoolExecutor.getQueue().size() + ", 执行完的任务数: " + threadPoolExecutor.getCompletedTaskCount());
		}		
		
		while(threadPoolExecutor.getCompletedTaskCount() < maxSize + capacity) {
			TimeUnit.MILLISECONDS.sleep(100);			
		}
		threadPoolExecutor.shutdown();
	}

}
