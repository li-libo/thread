package cn.com.thread.ta1;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

/**
 * 使用synchronized和wait/notifyAll实现Lock
 * @author lilibo
 * @create 2022-01-02 8:52 PM
 */
public class MyLock implements Lock {

    private int id;

    private Thread lockedThread;

    private int state;

    private boolean isLocked;

    @Override
    public synchronized void lock() {
        Thread currentThread = Thread.currentThread();
        while(isLocked && currentThread!=lockedThread) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        state++;
        lockedThread = Thread.currentThread();
        isLocked = true;
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
    public synchronized void unlock() {
        Thread currentThread = Thread.currentThread();
        if(isLocked && currentThread == lockedThread) {
            state--;
            if(state == 0) {
                lockedThread = null;
                isLocked = false;
                this.notifyAll();
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Test
    public void test1() {
        int numOfTestThread = 5;
        Lock lock = new MyLock();
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(()->{
                Stream.iterate(0, count1 -> count1+1).limit(100000).forEach(count1->{
                    try{
                        lock.lock();
                        id++;
                    }finally {
                        lock.unlock();
                    }
                });
            }, "test-" + count).start();
        });

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println("最终id = " + id);
    }
}
