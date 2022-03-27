package cn.com.thread.ta5;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁降级示例
 * @author lilibo
 *
 */
public class DegradeLock {
	
	private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
	
	private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
	
	private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

	public static final int numOfTestThread = 5;
	
	private int count = 0;
	
	private final Set<String> idSet = Collections.synchronizedSet(new HashSet<>());
	
	public void addNextId() {
		writeLock.lock();
		count++;
		readLock.lock();
		writeLock.unlock();
		String id = "" + count;
		if(!idSet.add(id)) {
			System.out.println("出现重复数据, id = " + id);
		}
		readLock.unlock();
	}
	
	public static void main(String[] args) {
		DegradeLock degradeLock = new DegradeLock();
		for(int i = 0; i<numOfTestThread; i++) {
			new Thread(()->{
				while(true)
					degradeLock.addNextId();
			}).start();
		}
	}

}
