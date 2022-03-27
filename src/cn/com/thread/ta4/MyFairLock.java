package cn.com.thread.ta4;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author lilibo
 * @create 2022-01-03 6:22 PM
 */
public class MyFairLock implements Lock {

    private final Sync sync;

    public MyFairLock(boolean fair) {
        sync = fair ? new FairSync() : new NoFairSync();
    }

    abstract static class Sync extends AbstractQueuedSynchronizer{
        final ConditionObject newCondition() {
            return new ConditionObject();
        }
    }

    static class FairSync extends Sync {

        @Override
        protected boolean tryAcquire(int arg) {
            int state = getState();
            Thread currentThread = Thread.currentThread();
            if (state == 0) {
                // 公平性关键java.util.concurrent.locks.AbstractQueuedSynchronizer.hasQueuedPredecessors
                if (!hasQueuedPredecessors() && compareAndSetState(0, arg)) {
                    setExclusiveOwnerThread(currentThread);
                    return true;
                }
            } else {
                if (currentThread == getExclusiveOwnerThread()) {
                    setState(state + arg);
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            Thread currentThread = Thread.currentThread();
            if (currentThread != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException("当前线程并不持有锁!");
            }
            int state = getState();
            int newState = state - 1;
            if (newState == 0) {
                setExclusiveOwnerThread(null);
                setState(newState);
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

    }

    static class NoFairSync extends Sync {

        @Override
        protected boolean tryAcquire(int arg) {
            int state = getState();
            Thread currentThread = Thread.currentThread();
            if (state == 0) {
                if (compareAndSetState(0, arg)) {
                    setExclusiveOwnerThread(currentThread);
                    return true;
                }
            } else {
                if (currentThread == getExclusiveOwnerThread()) {
                    setState(state + arg);
                    return true;
                }
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int arg) {
            Thread currentThread = Thread.currentThread();
            if (currentThread != getExclusiveOwnerThread()) {
                throw new IllegalMonitorStateException("当前线程并不持有锁!");
            }
            int state = getState();
            int newState = state - 1;
            if (newState == 0) {
                setExclusiveOwnerThread(null);
                setState(newState);
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
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
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
