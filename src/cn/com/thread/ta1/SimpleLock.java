package cn.com.thread.ta1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class SimpleLock implements Lock{

	private int count = 0;
	
	private Thread lockThread;
	
	private boolean isLocked;
	
	@Override
	public synchronized void lock() {
		Thread currentThread = Thread.currentThread();
		if(isLocked && currentThread != lockThread) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		count++;
		lockThread = currentThread;
		isLocked = true;
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized void unlock() {
		if(!isLocked) {
			return;
		}
		Thread currentThread = Thread.currentThread();
		if(lockThread == currentThread) {
			count--;
		}
		if(count == 0) {			
			lockThread = null;
			isLocked = false;
			notifyAll();
		}
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}

}
