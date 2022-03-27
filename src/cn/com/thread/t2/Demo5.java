package cn.com.thread.t2;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 使用定时器创建线程 Timer与ScheduledThreadPoolExecutor处理异常的对比
 * 
 * @author lilibo
 *
 */
public class Demo5 {

	public static void main(String[] args) {
		try {
			Timer timer = new Timer();
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					System.out.println("how about you? the current thread name is " + Thread.currentThread().getName());
				}
			}, 5000, 1000);
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					// 本任务的异常会影响其他任务的执行
					throw new RuntimeException("模拟Timer异常");
				}
			}, 10 * 1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 定时调度线程池
		ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(0);
		scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
			System.out.println("how about you? the current thread name is " + Thread.currentThread().getName());
		}, 3, 5, TimeUnit.SECONDS);
		scheduledThreadPoolExecutor.schedule(() -> {
			throw new RuntimeException("模拟ScheduledThreadPoolExecutor异常");
		}, 5, TimeUnit.SECONDS);
	}

}
