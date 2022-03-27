package cn.com.thread.locksupport;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author lilibo
 * @create 2022-01-14 9:13 PM
 */
public class LockSupportTest {

    public static final String format1 = "localDateTime = %s: the currentThread name = %s is starting!";

    public static final String format2 = "localDateTime = %s, the currentThread name = %s is waking!";

    /**
     * 示例1
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {
        ThreadGroup g1Group = new ThreadGroup("g1");
        Thread t1 = new Thread(g1Group, () -> {
            System.out.println(String.format(format1, LocalDateTime.now(), Thread.currentThread().getName()));
            LockSupport.park();
            System.out.println(String.format(format2, LocalDateTime.now(), Thread.currentThread().getName()));
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(3);
        LockSupport.unpark(t1);
        System.out.println(LocalDateTime.now() + ", LockSupport.unpark()执行完毕!");
        while (g1Group.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * 示例2, 唤醒方法放在等待方法之前执行,看能否被唤醒?(应该是压根没有等待)
     */
    @Test
    public void test2() {
        ThreadGroup g1Group = new ThreadGroup("g1");
        Thread t1 = new Thread(g1Group, () -> {
            System.out.println(String.format(format1, LocalDateTime.now(), Thread.currentThread().getName()));
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            LockSupport.park();
            System.out.println(String.format(format2, LocalDateTime.now(), Thread.currentThread().getName()));
        }, "t1");
        t1.start();
        // 先唤醒
        LockSupport.unpark(t1);
        System.out.println(LocalDateTime.now() + ", LockSupport.unpark()执行完毕!");
        while (g1Group.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * 示例3, park让线程等待后, 是否能响应线程中断呢?
     *
     * LockSupport.park方法让线程等待之后，唤醒方式有2种：
     * 1. 调用LockSupport.unpark方法
     * 2. 调用等待线程的 interrupt() 方法，给等待的线程发送中断信号，可以唤醒线程
     */
    @Test
    public void test3() throws InterruptedException {
        ThreadGroup g1Group = new ThreadGroup("g1");
        Thread t1 = new Thread(g1Group, () -> {
            System.out.println(String.format(format1, LocalDateTime.now(), Thread.currentThread().getName()));
            System.out.println(Thread.currentThread().getName() + " park()之前中断标志: " + Thread.currentThread().isInterrupted());
            LockSupport.park();
            System.out.println(Thread.currentThread().getName() + " park()之后中断标志: " + Thread.currentThread().isInterrupted());
            System.out.println(String.format(format2, LocalDateTime.now(), Thread.currentThread().getName()));
        }, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(3);
        t1.interrupt();
        while (g1Group.activeCount() > 0) {
            Thread.yield();
        }
    }

    @Test
    public void test4() throws InterruptedException {
        ThreadGroup g1Group = new ThreadGroup("g1");
        Runnable runnable = () -> {
            System.out.println(String.format(format1, LocalDateTime.now(), Thread.currentThread().getName()));
            Thread.dumpStack();
            LockSupport.park();
            System.out.println(String.format(format2, LocalDateTime.now(), Thread.currentThread().getName()));
        };

        new Thread(g1Group, runnable, "t1").start();
        new Thread(g1Group, runnable, "t2").start();
        while (true) {
            TimeUnit.SECONDS.sleep(1);

        }
    }
}

class Blocker {
    private final String id;

    public Blocker(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Blocker{" +
                "id='" + id + '\'' +
                '}';
    }
}