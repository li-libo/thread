package cn.com.thread.t8;

import org.junit.Test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.stream.Stream;

/**
 * JDK提供的原子类原理及使用
 *
 * 原子更新基本类型
 * 原子更新数组
 * 原子更新抽象类型
 * 原子更新字段
 */
public class AtomicIntegerArrayTest {

	private final AtomicIntegerArray atomicIntegerArray = new AtomicIntegerArray(new int[]{1, 2, 3, 4, 5});

	@Test
	public void test1() {
		Stream.iterate(0, count -> count + 1).limit(atomicIntegerArray.length()).forEach(count -> {
			new Thread(()->{
				System.out.println("count = " + count);
				atomicIntegerArray.incrementAndGet(count); // 原子更新数组, 参数为数组下标
			}, "test-" + count).start();
		});
		while(Thread.activeCount() > 2) {
			Thread.yield();
		}
		System.out.println(atomicIntegerArray.toString());

		// 修改指定索引上值
		System.out.println("修改指定索引上的值...");
		atomicIntegerArray.accumulateAndGet(3, 5, (prev, value)-> prev + value);
		System.out.println(atomicIntegerArray.toString());
	}
}
