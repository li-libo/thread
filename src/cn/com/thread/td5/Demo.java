package cn.com.thread.td5;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class Demo {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ThreadPoolExecutor pool = new ThreadPoolExecutor(10, 10, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<>(),
				new ThreadPoolExecutor.CallerRunsPolicy());
		ExecutorService pool1 = Executors.newFixedThreadPool(5);
		ExecutorService pool2 = Executors.newCachedThreadPool();
		ExecutorService pool3 = Executors.newSingleThreadExecutor();
		ExecutorService pool4 = Executors.newWorkStealingPool();		
		ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(10);
		AtomicInteger atomicInteger = new AtomicInteger();

//		List<FutureTask<Integer>> futureTaskList = new ArrayList<>();

//		for (int i = 0; i < 100; i++) {
//			FutureTask<Integer> futureTask = new FutureTask<>(() -> {
//				TimeUnit.SECONDS.sleep(1);
//				return atomicInteger.incrementAndGet();
//			});
//			futureTaskList.add(futureTask);
//			pool.submit(futureTask);
//		}
//		

//		for (FutureTask<Integer> future : futureTaskList) {
//			System.out.println("atomicInteger = " + future.get());
//		}
		
		for (int i = 0; i < 100; i++) {
			pool.submit(() -> {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}, atomicInteger.incrementAndGet());
		}

		System.out.println("最终atomicInteger = " + atomicInteger.get());
		pool.shutdown();
	}

}
