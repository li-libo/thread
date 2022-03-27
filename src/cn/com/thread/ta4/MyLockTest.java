package cn.com.thread.ta4;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-03 6:07 PM
 */
public class MyLockTest {

    private final Lock myLock = new MyFairLock(true);

    private int id;

    public static final String format1 = "the currentThread name = %s进入%s";

    private void a() {
        try{
            myLock.lock();
            System.out.println(String.format(format1, Thread.currentThread().getName(), "a()"));
            b();
        }finally {
            myLock.unlock();
        }
    }

    private void b() {
        try{
            myLock.lock();
            System.out.println(String.format(format1, Thread.currentThread().getName(), "b()"));
            c();
        }finally {
            myLock.unlock();
        }
    }

    private void c() {
        try{
            myLock.lock();
            System.out.println(String.format(format1, Thread.currentThread().getName(), "c()"));
        }finally {
            myLock.unlock();
        }
    }

    private int getNextId() {
        try {
            myLock.lock();
            id++;
            return id;
        }finally {
            myLock.unlock();
        }
    }

    /**
     * 可重入性测试
     */
    @Test
    public void test1() {
        int numOfTestThread = 5;
        int numOfLoop = 5;
        MyLockTest myLockTest = new MyLockTest();
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(()->{
                Stream.iterate(0, count1 -> count1 + 1).limit(numOfLoop).forEach(count1 ->{
                    myLockTest.a();
                });
            }, "Test-" + count).start();
        });
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
    }

    @Test
    public void test2() {
        Set<String> idSet = Collections.synchronizedSet(new HashSet<>());
        int numOfTestThread = Runtime.getRuntime().availableProcessors() - 1;
        int numOfLoop = 10 * 10000;
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(()-> {
                Stream.iterate(0, count1 -> count1).limit(numOfLoop).forEach(count1 -> {
                    String nextId = getNextId() + "";
                    if(!idSet.add(nextId)) {
                        throw new RuntimeException("出现重复数据!");
                    }
                });
            }, "test-" + count).start();
        });

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println("id = " + id);
    }

}
