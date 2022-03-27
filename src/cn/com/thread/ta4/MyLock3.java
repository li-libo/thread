package cn.com.thread.ta4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyLock3 implements Lock{
	
	
	private final Sync sync;
	
	public MyLock3(){
		sync = new Sync();
	}
	
	static class Sync extends AbstractQueuedSynchronizer {

		/**
		 * 
		 */
		private static final long serialVersionUID = -3637648017063118440L;
		
		@Override
		protected boolean tryAcquire(int arg) {
			int state = getState();
			Thread currentThread = Thread.currentThread();
			if (state == 0) {
				if (compareAndSetState(0, arg)) {
					setExclusiveOwnerThread(Thread.currentThread());
					return true;
				}
			} else {
				if (getExclusiveOwnerThread() == currentThread) {
					setState(state + arg);
					return true;
				}
			}
			return false;
		}
		
		
		@Override
	    protected boolean tryRelease(int arg) {
	        int state = getState();
	        Thread currentThread = Thread.currentThread();
	        if(currentThread == getExclusiveOwnerThread()) {
	        	int newState = state - arg;
	        	setState(newState);
	        	if(newState == 0) {
	        		setExclusiveOwnerThread(null);
	        		return true;
	        	}
	        }
	        return false;
	    }


	    protected int tryAcquireShared(int arg) {
	        throw new UnsupportedOperationException();
	    }

	    
	    protected boolean tryReleaseShared(int arg) {
	        throw new UnsupportedOperationException();
	    }


	    protected boolean isHeldExclusively() {
	        throw new UnsupportedOperationException();
	    }
			
	    ConditionObject newCondition() {
            return new ConditionObject();
        }
	    
	}

	@Override
	public void lock() {
		sync.acquire(1);		
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		sync.acquireInterruptibly(1);		
	}

	@Override
	public boolean tryLock() {
		return sync.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return sync.tryAcquireNanos(1, unit.toNanos(time));
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
