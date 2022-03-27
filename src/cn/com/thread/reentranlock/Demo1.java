package cn.com.thread.reentranlock;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-16 6:20 PM
 */
public class Demo1 {

    public static final String format1 = "the currentThread name = %s is running!";

    public static final String format2 = "the currentThread name = %s is dying!";

    /**
     * 可重入锁示例
     */
    @Test
    public void test1() throws InterruptedException {
        Lock lock = new ReentrantLock();
        Runnable runnable = () -> {
          try{
              lock.lock();
              lock.lock();
              System.out.println(String.format(format1, Thread.currentThread().getName()));
          }finally {
              lock.unlock();
              lock.unlock();
              System.out.println(String.format(format2, Thread.currentThread().getName()));
          }
        };
        Thread t1 = new Thread(runnable, "test1");
        t1.start();
        t1.join();
    }

    /**
     * 公平锁示例
     */
    @Test
    public void test2() {
        int numOfLoop = 10;
        Lock lock = new ReentrantLock(true);
        Runnable runnable = () -> {
            Stream.iterate(0, count -> count + 1).limit(numOfLoop).forEach(count -> {
                try{
                    lock.lock();
                    System.out.println(String.format(format1, Thread.currentThread().getName()));
                }finally {
                    lock.unlock();
                }
            });
        };
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        Thread t1 = new Thread(testGroup, runnable, "t1");
        Thread t2 = new Thread(testGroup, runnable, "t2");
        Thread t3 = new Thread(testGroup, runnable, "t3");
        t1.start();
        t2.start();
        t3.start();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * ReentrantLock获取锁的过程是可以中断的
     *
     * 关于获取锁的过程中被中断，注意几点:
     * 1. ReentrantLock中必须使用实例方法 lockInterruptibly() 获取锁时，在线程调用interrupt() 方法之后，才会引发 InterruptedException 异常
     * 2. 线程调用interrupt()之后，线程的中断标志会被置为true
     * 3. 触发InterruptedException异常之后，线程的中断标志会被清空，即置为false
     * 4. 所以当线程调用interrupt()引发InterruptedException异常，中断标志的变化是:false->true->false
     */
    @Test
    public void test3() throws InterruptedException {
        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        Thread t1 = new Thread(testGroup, makeDeadLockRunnable(true, lock1, lock2), "t1");
        t1.start();
        Thread t2 = new Thread(testGroup, makeDeadLockRunnable(false, lock1, lock2), "t2");
        t2.start();
        System.out.println("pid = " + getPid());
        // 尝试中断t2线程
        TimeUnit.SECONDS.sleep(3);
        t2.interrupt();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * 锁申请等待限时
     */
    @Test
    public void test4() {
        ReentrantLock lock = new ReentrantLock();
        Runnable runnable = () -> {
            try {
                if(lock.tryLock(3, TimeUnit.SECONDS)) {
                    System.out.println("the currentThread name = " + Thread.currentThread().getName() + "获取到锁! localDateTime = " + LocalDateTime.now());
                    TimeUnit.SECONDS.sleep(2);
                }else {
                    System.out.println("the currentThread name = " + Thread.currentThread().getName() + "3秒内没有获取到锁! localDateTime = " + LocalDateTime.now());
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if(lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        };
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        new Thread(testGroup, runnable, "t1").start();
        new Thread(testGroup, runnable, "t2").start();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    public static final String format3 = "the currentThread name = %s 获取%s!";

    public Runnable makeDeadLockRunnable(boolean flag, ReentrantLock lock1, ReentrantLock lock2) {
        return () -> {
            if(flag) {
                try{
                    lock1.lockInterruptibly();
                    System.out.println(String.format(format3, Thread.currentThread().getName(), "lock1"));
                    TimeUnit.SECONDS.sleep(1);
                    lock2.lockInterruptibly();
                    System.out.println(String.format(format3, Thread.currentThread().getName(), "lock2"));
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } finally {
                    if(lock2.isHeldByCurrentThread())
                        lock2.unlock();
                    if(lock1.isHeldByCurrentThread())
                        lock1.unlock();
                }
            }else {
                try{
                    lock2.lockInterruptibly();
                    System.out.println(String.format(format3, Thread.currentThread().getName(), "lock2"));
                    TimeUnit.SECONDS.sleep(1);
                    lock1.lockInterruptibly();
                    System.out.println(String.format(format3, Thread.currentThread().getName(), "lock1"));
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                } finally {
                    if(lock1.isHeldByCurrentThread())
                        lock1.unlock();
                    if(lock2.isHeldByCurrentThread())
                        lock2.unlock();
                }
            }
        };
    };

    public String getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

}
