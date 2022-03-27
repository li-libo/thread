package cn.com.thread.tc8;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程安全Set与线程不安全Set对比
 * @author lilibo
 *
 */
public class Demo {
	
	public static void main(String[] args) throws InterruptedException {	
		AtomicInteger atomicInteger = new AtomicInteger();
		Set<String> idSet1 = new HashSet<>();
		ThreadGroup group1 = new ThreadGroup("idSet1");
		for(int i = 0; i < 4; i++) {
			final int j = i;
			new Thread(group1, ()->{
				for(int c = 0; c < 10000; c++) {					
					String id = atomicInteger.incrementAndGet() + "";
					if(!idSet1.add(id)) {
						System.out.println("出现重复数据!!! id = " + id);
					}
				}
			}, "addId-" + j).start();
		}
		while(group1.activeCount() > 0) {
			TimeUnit.SECONDS.sleep(1);
		}
		System.out.println("idSet1 size = " + idSet1.size());
		
		ThreadGroup group2 = new ThreadGroup("idSet2");
		atomicInteger.set(0);
		Set<String> idSet2 = new CopyOnWriteArraySet<>();
		for(int i = 0; i < 4; i++) {
			final int j = i;
			new Thread(group2, ()->{
				for(int c = 0; c < 10000; c++) {					
					String id = atomicInteger.incrementAndGet() + "";
					if(!idSet2.add(id)) {
						System.out.println("出现重复数据!!! id = " + id);
					}
				}
			}, "addId-" + j).start();
		}
		while(group2.activeCount() > 0) {
			TimeUnit.SECONDS.sleep(1);
		}
		System.out.println("idSet2 size = " + idSet2.size());
		
		Runnable run1 = ()->{
			System.out.println("the current thread name = " + Thread.currentThread().getName());
		};
		run1.run();
	}

}
