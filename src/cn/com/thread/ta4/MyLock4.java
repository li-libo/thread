package cn.com.thread.ta4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 自定义锁,支持可重入
 * @author lilibo
 *
 */
public class MyLock4 implements Lock{
	
	final MyLock4Sync sync;
	
	public MyLock4() {
		sync = new MyLock4Sync();
	}
	
	static class MyLock4Sync extends AbstractQueuedSynchronizer{

		/**
		 * 
		 */
		private static final long serialVersionUID = -4803426454757477803L;
		
		@Override
	    protected boolean tryAcquire(int arg) {
			Thread currentThread = Thread.currentThread();
			int state = getState();
			if(state == 0) {
				if(compareAndSetState(0, arg)) {
					setExclusiveOwnerThread(currentThread);
					return true;
				}
			}
			if(currentThread == getExclusiveOwnerThread()) {
				setState(state + arg);
				return true;
			}
			return false;
		}
	    
		@Override
	    protected boolean tryRelease(int arg) {
	        Thread currentThread = Thread.currentThread();
			if (currentThread != getExclusiveOwnerThread()) {
				throw new IllegalMonitorStateException("当前线程并不持有锁!!! @the current Thread name = " + currentThread.getName());
			}
			setState(getState() - arg);
			if(getState() == 0) {
				setExclusiveOwnerThread(null);
				return true;
			}
			return false;
		}
		
		@Override
	    protected boolean isHeldExclusively() {
	        return Thread.currentThread() == getExclusiveOwnerThread();
	    }
		
		final Condition newCondition() {
			return new ConditionObject();
		}
		
	}

	@Override
	public void lock() {
		sync.acquire(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		
	}

	@Override
	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		sync.release(1);
	}

	@Override
	public Condition newCondition() {
		return sync.newCondition();
	}

}
