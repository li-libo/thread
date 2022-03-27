package cn.com.thread.ta1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 自己实现Lock
 * @author lilibo
 *
 */
public class MyLock implements Lock {
	
	private boolean isLocked = false;
	
	private Thread lockBy = null;
	
	private int lockCount = 0;

	@Override
	public void lockInterruptibly() throws InterruptedException {
		
	}

	@Override
	public boolean tryLock() {
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void lock() {
		Thread currentThread = Thread.currentThread();
		if (isLocked && currentThread != lockBy) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		isLocked = true;		
		lockBy = currentThread;
		lockCount++;			
	}

	@Override
	public synchronized void unlock() {
		if(isLocked && Thread.currentThread() == lockBy) {
			lockCount--;
			if(lockCount == 0) {
				lockBy = null;
				isLocked = false;
				notifyAll();
			}
		}		
	}

}
