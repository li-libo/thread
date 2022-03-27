package cn.com.thread.td4;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorDemo {

	public static void main(String[] args) {
		ThreadPoolExecutor pool = new ThreadPoolExecutor(0, 5, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5),
				new ThreadPoolExecutor.CallerRunsPolicy());
		AtomicInteger atomicInteger = new AtomicInteger(0);
		for(int i = 0; i<100; i++) {
			pool.submit(()->{
				atomicInteger.incrementAndGet();
			});
		}
		// 等待
		while(Thread.activeCount() > 1) {
			
		}
		pool.shutdown();
		System.out.println("最终值 = " + atomicInteger.get());
	}
}
