package cn.com.thread.ta1;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

/**
 * 使用synchronized实现Lock
 *
 * @author lilibo
 * @create 2022-01-02 4:33 PM
 */
public class SimpleLock implements Lock {

    private int id;

    private int state = 0;

    private Thread lockThread;

    private boolean isLocked;

    @Override
    public void lock() {
        Thread currentThread = Thread.currentThread();
        while (true) {
            synchronized (this) {
                if (state == 0) {
                    state++;
                    lockThread = currentThread;
                    isLocked = true;
                    break;
                } else if (lockThread == currentThread) {
                    state++;
                    isLocked = true;
                    break;
                }
            }
        }
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
        Thread currentThread = Thread.currentThread();
        if (currentThread != lockThread) {
            return;
        }
        synchronized (this) {
            if (currentThread == lockThread) {
                state = state > 0 ? state - 1 : state;
                if (state == 0) {
                    lockThread = null;
                    isLocked = false;
                }
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    @Test
    public void test1() {
        SimpleLock simpleLock = new SimpleLock();
        Set<String> idSet = new HashSet<>();
        int numOfTestThread = 5;
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(() -> {
                Stream.iterate(0, count1 -> count1 + 1).limit(10000).forEach(count1 -> {
                    try {
                        simpleLock.lock();
                        TimeUnit.MILLISECONDS.sleep(1);
                        id++;
                        if (!idSet.add("id-" + id)) {
                            throw new RuntimeException("出现重复数据!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        simpleLock.unlock();
                    }
                });
            }, "thread-" + count).start();
        });
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println("id = " + id);
    }
}
