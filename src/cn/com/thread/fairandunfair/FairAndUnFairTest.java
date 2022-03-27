package cn.com.thread.fairandunfair;

import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * 演示公平锁和非公平锁
 * @author lilibo
 * @create 2022-01-14 10:51 AM
 */
public class FairAndUnFairTest {

    public static final String format1 = "the currentThread name = %s 竞争到锁!";

    @Test
    public void test1() throws InterruptedException {
        Lock lock = new ReentrantLock();
        ThreadGroup afterThreadGroup = new ThreadGroup("after");
        ThreadGroup beforeThreadGroup = new ThreadGroup("before");
        int numOfTryLock = Runtime.getRuntime().availableProcessors() - 1;
        new Thread(() -> {
            try {
                lock.lock();
                TimeUnit.SECONDS.sleep(3);
                new Thread(()-> {
                    startThreadGroup(lock, afterThreadGroup, numOfTryLock, "after-");
                }).start();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                lock.unlock();
            }
        }).start();
        TimeUnit.SECONDS.sleep(1);
        startThreadGroup(lock, beforeThreadGroup, numOfTryLock, "before-");
        while (beforeThreadGroup.activeCount() > 0 || afterThreadGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    @Test
    public void test2() {
        Lock lock = new ReentrantLock(true);
        Runnable runnable = () -> {
            while (true) {
                try{
                    lock.lock();
                    System.out.println("the currentThreadName = " + Thread.currentThread().getName() + "获取到锁!");
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        };
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        new Thread(testGroup, runnable, "t1").start();
        new Thread(testGroup, runnable, "t2").start();
        new Thread(testGroup, runnable, "t3").start();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    private static Unsafe unsafe;

    @Test
    public void test3() throws NoSuchFieldException, IllegalAccessException {
        // 当前类非根加载器加载, 因此抛出异常
        // Unsafe unsafe = Unsafe.getUnsafe();
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        unsafe = (Unsafe) unsafeField.get(null);
        System.out.println("unsafe = " + unsafe);
    }

    private void startThreadGroup(Lock lock, ThreadGroup threadGroup, int numOfTryLock, String prefix) {
        Stream.iterate(0, count -> count + 1).limit(numOfTryLock).forEach(count -> {
            new Thread(threadGroup, ()->{
                try{
                    lock.lock();
                    System.out.println(String.format(format1, Thread.currentThread().getName()));
                } finally {
                    lock.unlock();
                }
            }, prefix + count + ": startTime = " + System.currentTimeMillis()).start();
        });
    }

}
