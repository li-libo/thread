package cn.com.thread.interrept;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 中断线程的几种方式
 * @author lilibo
 * @create 2022-01-16 5:28 PM
 */
public class Demo1 {

    private volatile boolean interruptFlag = false;

    public static final String format1 = "the currentThread name = %s is running!";

    public static final String format2 = "the currentThread name = %s is dying!";

    /**
     * 通过1个变量控制线程中断, 切记该变量为volatile
     */
    @Test
    public void test1() throws InterruptedException {
        Runnable runnable = () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            while (!interruptFlag) {
            }
            System.out.println(String.format(format2, Thread.currentThread().getName()));
        };
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        new Thread(testGroup, runnable, "t1").start();
        TimeUnit.SECONDS.sleep(3);
        interruptFlag = true;
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * 通过线程自带的中断标志
     */
    @Test
    public void test2() throws InterruptedException {
        Runnable runnable = () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            while (!Thread.currentThread().isInterrupted()) {
            }
            System.out.println(String.format(format2, Thread.currentThread().getName()));
        };

        ThreadGroup testGroup = new ThreadGroup("testGroup");
        Thread t1 = new Thread(testGroup, runnable, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(3);
        t1.interrupt();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * 线程阻塞状态如何中断
     */
    @Test
    public void test3() throws InterruptedException {
        Runnable runnable = () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    TimeUnit.SECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    //当触发InterruptException异常后会清除线程中断标志,因此需要重新设置线程中断标志
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println(String.format(format2, Thread.currentThread().getName()));
        };

        ThreadGroup testGroup = new ThreadGroup("testGroup");
        Thread t1 = new Thread(testGroup, runnable, "t1");
        t1.start();
        TimeUnit.SECONDS.sleep(3);
        t1.interrupt();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }
}
