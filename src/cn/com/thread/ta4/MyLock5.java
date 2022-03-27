package cn.com.thread.ta4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyLock5 implements Lock {

	private final Sync sync;

	public MyLock5() {
		sync = new NotFairSync();
	}

	public MyLock5(boolean fair) {
		sync = fair ? new FairSync() : new NotFairSync();
	}

	static class Sync extends AbstractQueuedSynchronizer {

		
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 8195482460145575377L;

		@Override
		protected final boolean tryRelease(int arg) {
			Thread currentThread = Thread.currentThread();
			if (currentThread != getExclusiveOwnerThread()) {
				throw new IllegalMonitorStateException(
						"当前线程并不持有锁!!! the current Thread Name = " + Thread.currentThread().getName());
			}
			setState(getState() - arg);
			if (getState() == 0) {
				setExclusiveOwnerThread(null);
				return true;
			}
			return false;
		}

		@Override
		protected final boolean isHeldExclusively() {
			return Thread.currentThread() == getExclusiveOwnerThread();
		}

		final Condition newCondition() {
			return new ConditionObject();
		}

	}

	static final class FairSync extends Sync {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2584911137858172283L;

		@Override
		protected final boolean tryAcquire(int arg) {
			int state = getState();
			Thread currentThread = Thread.currentThread();
			if (state == 0) {
				if (!hasQueuedPredecessors() && compareAndSetState(0, arg)) {
					setExclusiveOwnerThread(currentThread);
					return true;
				}
			}
			if (currentThread == getExclusiveOwnerThread()) {
				setState(state + arg);
				return true;
			}
			return false;
		}

	}

	static final class NotFairSync extends Sync {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7408221979302692615L;

		@Override
		protected final boolean tryAcquire(int arg) {
			int state = getState();
			Thread currentThread = Thread.currentThread();
			if (state == 0) {
				if (compareAndSetState(0, arg)) {
					setExclusiveOwnerThread(currentThread);
					return true;
				}
			}
			if (currentThread == getExclusiveOwnerThread()) {
				setState(state + arg);
				return true;
			}
			return false;
		}

	}

	@Override
	public void lock() {
		sync.acquire(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock() {
		if(sync instanceof FairSync) {
			return ((FairSync) sync).tryAcquire(1);
		}
		return ((NotFairSync) sync).tryAcquire(1);
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
