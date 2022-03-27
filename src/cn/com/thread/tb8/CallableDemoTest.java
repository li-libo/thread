package cn.com.thread.tb8;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class CallableDemoTest {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		Callable<Integer> callable = () -> {
			System.out.println(Thread.currentThread().getName() + " 开始执行callable");
			TimeUnit.SECONDS.sleep(10);
			System.out.println(Thread.currentThread().getName() + " 执行callable完毕!!!");
			return 1;
		};
		FutureTask<Integer> futureTask = new FutureTask<>(callable);
		new Thread(futureTask, "futureTask-test").start();
		System.out.println("干些别的...");
		System.out.println("futureTask.get() = " + futureTask.get());
	}

}
