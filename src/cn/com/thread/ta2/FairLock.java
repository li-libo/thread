package cn.com.thread.ta2;

import java.util.ArrayList;
import java.util.List;

/**
 * 公平锁:
 * 		公平是针对锁的获取而言的，如果一个锁是公平的，那么锁的获取顺序就应该符合请求锁的绝对时间顺序
 * @author lilibo
 *
 */
public class FairLock {
	
	@SuppressWarnings("unused")
	private boolean isLocked = false;
	
	private Thread lockingThread = null;
	
	private List<QueueObject> waitingThreads = new ArrayList<QueueObject>();

	public void lock() throws InterruptedException {
		QueueObject queueObject = new QueueObject();
		synchronized (this) {
			waitingThreads.add(queueObject);
		}
		try {
			queueObject.doWait();
		} catch (InterruptedException e) {
			synchronized (this) {
				waitingThreads.remove(queueObject);
			}
			throw e;
		}
	}

	public synchronized void unlock() {
		if (this.lockingThread != Thread.currentThread()) {
			throw new IllegalMonitorStateException("Calling thread has not locked this lock");
		}
		isLocked = false;
		lockingThread = null;
		if (waitingThreads.size() > 0) {
			waitingThreads.get(0).doNotify();
		}
	}
}