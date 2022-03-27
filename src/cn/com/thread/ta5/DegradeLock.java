package cn.com.thread.ta5;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Stream;

/**
 * 锁降级示例
 * @author lilibo
 * @create 2022-01-03 8:17 PM
 */
public class DegradeLock {

    private final Set<String> idSet = Collections.synchronizedSet(new HashSet<>());

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();

    private final ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();

    private int count;

    public void addId() {
        try{
            String id = null;
            try{
                writeLock.lock();
                count++;
                id = count + "";
                readLock.lock();
            }finally {
                writeLock.unlock();
            }
            if(!idSet.add(id)) {
                throw new RuntimeException("出现重复数据!, id = " + id);
            }
        }finally {
            readLock.unlock();
        }
    }

    @Test
    public void test1() {
        int numOfTestThread = 5;
        int numOfLoop = 10 * 10000;
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(()->{
                Stream.iterate(0, count1 -> count1 + 1).limit(numOfLoop).forEach(count1 -> {
                    addId();
                });
            }, "test-" + count).start();
        });

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println("count = " + count);
    }

}
