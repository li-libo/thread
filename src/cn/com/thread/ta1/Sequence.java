package cn.com.thread.ta1;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class Sequence {
	
	private int id;
	
	private final SimpleLock lock = new SimpleLock();
	
	private static Set<String> idSet = Collections.synchronizedSet(new HashSet<>());
	
	public int getNextId() {
		try {
			lock.lock();
			id++;
			return id;
		}finally {
			lock.unlock();
		}
	}

	public static void main(String[] args) {
		Sequence sequence = new Sequence();
		for(int i = 0; i < 5; i++) {
			final int j = i;
			new Thread(()->{
				while(true) {
					String nextId = sequence.getNextId() + "";
					// System.out.println("nextId = " + nextId);
					if(!idSet.add(nextId)) {
						System.out.println("出现重复数据, the current Thread name = " + Thread.currentThread().getName() + ", nextId = " + nextId);
					}
				}
			}, "test-" + j).start();
		}
	}

}
