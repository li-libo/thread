package cn.com.thread.ta3;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-02 9:29 PM
 */
public class ReentrantReadWriteLockDemo {

    private Map<String, String> dataMap = new HashMap<>();

    private final ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock.ReadLock readerLock = reentrantReadWriteLock.readLock();

    private final ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();

    public String get(String key) throws InterruptedException {
        if(readerLock.tryLock(10, TimeUnit.SECONDS)) {
            try{
                String value = dataMap.get(key);
                System.out.println("the currentThread Name = " + Thread.currentThread().getName() + "#get(), key = " + key);
                return value;
            }finally {
                readerLock.unlock();
            }
        }
        throw new RuntimeException("10s内无法获取读锁! the currentThread name = " + Thread.currentThread().getName());
    }

    public void put(String key, String value) throws InterruptedException {
        if(writeLock.tryLock(10, TimeUnit.SECONDS)) {
            try{
                dataMap.put(key, value);
                System.out.println("the currentThread name = " + Thread.currentThread().getName() + "#put(), key = " + key + ", value = " + value);
            }finally {
                writeLock.unlock();
            }
        }
        throw new RuntimeException("10s内无法获取写锁! the currentThrea name = " + Thread.currentThread().getName());
    }

    @Test
    public void test1() {
        ReentrantReadWriteLockDemo reentrantReadWriteLockDemo = new ReentrantReadWriteLockDemo();
        int numOfReadThread = 10;
        int numOfWriteThread = 3;
        Stream.iterate(0, count -> count + 1).limit(numOfReadThread).forEach(count -> {
            new Thread(()->{
                String key = "key-" + count;
                try {
                    System.out.println(reentrantReadWriteLockDemo.get(key));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "read-" + count).start();
        });

        Stream.iterate(0, count -> count + 1).limit(numOfWriteThread).forEach(count -> {
            new Thread(()->{
                String key = "key-" + count;
                String uuid = UUID.randomUUID().toString();
                try {
                    reentrantReadWriteLockDemo.put(key, uuid);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "write-" + count).start();
        });

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }

    }

}
