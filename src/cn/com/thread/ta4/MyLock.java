package cn.com.thread.ta4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 利用AbstractQueuedSynchronizer实现Lock
 * @author lilibo
 * @create 2022-01-03 5:56 PM
 */
public class MyLock implements Lock {

    private final Helper helper;

    public MyLock() {
        this.helper = new Helper();
    }

    static class Helper extends AbstractQueuedSynchronizer {
        @Override
        protected boolean tryAcquire(int arg) {
            /*
             * 1. 如果第1个线程进来,可以拿到锁,因此可以返回true
             * 2. 如果第2个线程进来,如果当前进来的线程和当前保存的线程不一致拿不到锁,返回false; 如果当前进来的线程和当前保存的线程一致可以拿到锁,返回true,但要更新状态值
             * 3. 如何判断是第1个线程进来还是其他线程进来?
             */
            int state = getState();
            Thread currentThread = Thread.currentThread();
            if(state == 0) {
                if(compareAndSetState(0, arg)) {
                    setExclusiveOwnerThread(currentThread);
                    return true;
                }
            } else {
                if(currentThread == getExclusiveOwnerThread()) {
                    setState(state + arg);
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            // 锁的获取和释放肯定是一一对应的,那么调用此方法的线程一定是当前线程
            Thread currentThread = Thread.currentThread();
            if(currentThread != getExclusiveOwnerThread()) {
                throw new RuntimeException("当前线程并不持有锁!");
            }
            int state = getState();
            int newState = state - arg;
            if(newState == 0) {
                setExclusiveOwnerThread(null);
                setState(0);
                return true;
            }
            setState(newState);
            return false;
        }

        @Override
        protected int tryAcquireShared(int arg) {
            return super.tryAcquireShared(arg);
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            return super.tryReleaseShared(arg);
        }

        @Override
        protected boolean isHeldExclusively() {
            return super.isHeldExclusively();
        }

        @Override
        public String toString() {
            return super.toString();
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

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return false;
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
