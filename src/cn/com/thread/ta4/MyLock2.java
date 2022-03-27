package cn.com.thread.ta4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 基于AQS自己实现Lock
 * @author lilibo
 *
 */
public class MyLock2 implements Lock {

	private Helper helper = new Helper();
	
	private class Helper extends AbstractQueuedSynchronizer{
	   
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean tryAcquire(int arg) {
			/*
			 * 1. 如果第1个线程进来,可以拿到锁,因此可以返回true
			 * 2. 如果第2个线程进来,如果当前进来的线程和当前保存的线程不一致拿不到锁,返回false; 如果当前进来的线程和当前保存的线程一致可以拿到锁,返回true,但要更新状态值
			 * 3. 如何判断是第1个线程进来还是其他线程进来?
			 */
			int state = getState();
			Thread ownerThread = getExclusiveOwnerThread();
			if(state == 0) {
				if(compareAndSetState(0, arg)) {
					setExclusiveOwnerThread(Thread.currentThread());
					return true;
				}
			}else {
				if(Thread.currentThread() == ownerThread) {
					setState(state + arg);
					return true;
				}
			}
			return false;
		}
	    
		@Override
		protected int tryAcquireShared(int arg) {
			// TODO Auto-generated method stub
			return super.tryAcquireShared(arg);
		}
		
		@Override
		protected boolean tryRelease(int arg) {
			// 锁的获取和释放肯定是一一对应的,那么调用此方法的线程一定是当前线程
			if(getExclusiveOwnerThread() != Thread.currentThread()) {
				throw new RuntimeException();
			}
			int state = getState();
			int newState = state - arg;
			if(newState == 0) {
				setState(newState);
				setExclusiveOwnerThread(null);
				return true;
			}
			setState(newState);
			return false;
		}
		
		@Override
		protected boolean tryReleaseShared(int arg) {
			// TODO Auto-generated method stub
			return super.tryReleaseShared(arg);
		}
		
		final ConditionObject newCondition() {
            return new ConditionObject();
        }
		
	}
	
	@Override
	public void lock() {
		helper.acquire(1);		
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		helper.acquireInterruptibly(1);
	}

	@Override
	public boolean tryLock() {
		return helper.tryAcquire(1);
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return helper.tryAcquireNanos(1, unit.toNanos(time));
	}

	@Override
	public void unlock() {
		helper.release(1);
	}

	@Override
	public Condition newCondition() {
		return helper.newCondition();
	}
	
}
