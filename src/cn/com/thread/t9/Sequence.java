package cn.com.thread.t9;

import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * Lock示例
 * @author lilibo
 * @create 2022-01-02 11:27 AM
 */
public class Sequence {

    private final Lock lock = new ReentrantLock();

    private int value;

    public static final int numOfTestThread = 5;

    public static final int numOfLoop = 10 * 1000;

    private Callable<Integer> callable = () -> {
        if(lock.tryLock(5, TimeUnit.SECONDS)) {
            try{
                return ++value;
            }finally {
                lock.unlock();
            }
        }
        throw new RuntimeException("5s内没有获取到锁!");
    };

    @Test
    public void test1() {
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            Stream.iterate(0, count1 -> count1 + 1).limit(numOfLoop).forEach(count1 -> {
                try {
                    FutureTask<Integer> futureTask = new FutureTask<>(callable);
                    new Thread(futureTask, "test-" + count).start();
                    Integer value = futureTask.get();
                    System.out.println("value = " + value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(value);
    }

}
