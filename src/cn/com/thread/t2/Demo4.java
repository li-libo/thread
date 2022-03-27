package cn.com.thread.t2;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 创建线程的方式:带返回值的线程
 * @author lilibo
 *
 */
public class Demo4 implements Callable<Integer>{

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		FutureTask<Integer> f1 = new FutureTask<>(new Demo4());
		new Thread(f1).start();
		System.out.println("f1 = " + f1.get());
		
		FutureTask<String> f2 = new FutureTask<>(()->{
			try {
				TimeUnit.SECONDS.sleep(3);
				System.out.println("测试...");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "完毕!");
		new Thread(f2).start();
		System.out.println("f2 = " + f2.get());
		
		// map的compute和merge
		Map<String, Integer> map = new HashMap<>();
		for (int i = 0; i < 10; i++) {
			String key = "key" + i;
			map.put(key, i);
		}
		for (int i = 0; i < 10; i++) {
			String key = "key" + i;
			map.compute(key, (k, v) -> {
				if (v != null) {
					return v + 3;
				}
				return 0;
			});
		}
		for (int i = 0; i < 10; i++) {
			String key = "key" + i;
			map.merge(key, 5, (oldValue, value)->{
				return (oldValue + value);
			});
		}
		map.forEach((k, v) -> System.out.println("key = " + k + ", value = " + v));
	}

	@Override
	public Integer call() throws Exception {
		TimeUnit.SECONDS.sleep(3);
		return 1;
	}

}
