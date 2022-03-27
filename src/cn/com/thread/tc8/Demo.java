package cn.com.thread.tc8;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * 线程安全Set与线程不安全Set对比
 * @author lilibo
 *
 */
public class Demo {

	public static final String format1 = "the currentThreadName = %s, loop = %s";

	public static final String format2 = "the expected value = %s, actual value = %s";

	@Test
	public void test1() {
		Set<String> unsafeSet = new HashSet<>();
		int numOfTestThread = Runtime.getRuntime().availableProcessors();
		int numOfLoop = 10000;
		System.out.println("线程非安全Set集合...");
		ThreadGroup g1 = new ThreadGroup("unsafeSet");
		addData(unsafeSet, numOfTestThread, numOfLoop, g1);
		while (g1.activeCount() > 0) {
			Thread.yield();
		}
		System.out.println(String.format(format2, numOfLoop * numOfTestThread, unsafeSet.size()));

		System.out.println("线程安全Set集合, 利用ConcurrentHashMap...");
		Set<String> safeSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
		ThreadGroup g2 = new ThreadGroup("safeSet");
		addData(safeSet, numOfTestThread, numOfLoop, g2);
		while (g2.activeCount() > 0) {
			Thread.yield();
		}
		System.out.println(String.format(format2, numOfLoop * numOfTestThread, safeSet.size()));
	}

	private void addData(Set<String> set, int numOfTestThread, int numOfLoop, ThreadGroup threadGroup) {
		Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
			new Thread(threadGroup, ()->{
				Stream.iterate(0, count1 -> count1 + 1).limit(numOfLoop).forEach(count1 -> {
					String value = String.format(format1, Thread.currentThread().getName(), count1);
					if(!set.add(value)){
						throw new RuntimeException("出现重复数据: " + value);
					}
				});
			}, "unsafe-" + count).start();
		});
	}

}
