package cn.com.thread.ta1;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * 公平锁测试
 * @see ReentrantLock.FairSync#tryAcquire(int)
 * @see AbstractQueuedSynchronizer#hasQueuedPredecessors()
 *
 * @author lilibo
 * @create 2022-01-02 4:11 PM
 */
public class FairLockTest {

    private final List<String> threadNameList = new ArrayList<>();

    private final Lock lock = new ReentrantLock(true);

    public static final int numOfTest = Runtime.getRuntime().availableProcessors() - 1;

    Runnable runnable = ()->{
        try {
            if(lock.tryLock(10, TimeUnit.SECONDS)){
                try{
                    threadNameList.add(Thread.currentThread().getName());
                }finally {
                    lock.unlock();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    };

    @Test
    public void test1() {
        Stream.iterate(0, count-> count + 1).limit(numOfTest).forEach(count -> {
            new Thread(runnable, "test-" + count).start();
        });
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(threadNameList);
    }

}
