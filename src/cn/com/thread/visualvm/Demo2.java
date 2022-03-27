package cn.com.thread.visualvm;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author lilibo
 * @create 2022-01-15 9:23 PM
 */
public class Demo2 {

    private final Object lockObject = new Object();

    public static final String format1 = "the currentThread name = %s prepare to wait! localDateTime = %s";

    public static final String format2 = "the currentThread name = %s continue to doing! localDateTime =%s";

    public static final String format3 = "the currentThread name = %s prepare to notify!";

    /**
     * 等待唤醒示例
     */
    @Test
    public void test1() {
        Runnable runn1 = () -> {
          synchronized (lockObject) {
              // wait
              try {
                  System.out.println(String.format(format1, Thread.currentThread().getName(), LocalDateTime.now()));
                  lockObject.wait(); // 被唤醒后并不是直接执行后续代码, 而是需要尝试重新获取监视器对象
                  System.out.println(String.format(format2, Thread.currentThread().getName(), LocalDateTime.now()));
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
          }
        };
        Runnable runn2 = () -> {
            synchronized (lockObject) {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(String.format(format3, Thread.currentThread().getName()));
                    lockObject.notify();
                    TimeUnit.SECONDS.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        ThreadGroup g1 = new ThreadGroup("g1");
        new Thread(g1, runn1, "t1").start();
        new Thread(g1, runn2, "t2").start();

        while (g1.activeCount() > 0) {
            Thread.yield();
        }
    }

    public static final String format4 = "the currentThread name = %s prepare to suspend";

    public static final String format5 = "the currentThread name = %s has been resumed!";

    /**
     * suspend和resume示例
     */
    @Test
    public void test2() throws InterruptedException {
        Runnable runnable = () -> {
            System.out.println(String.format(format4, Thread.currentThread().getName()));
            Thread.currentThread().suspend();
            System.out.println(String.format(format5, Thread.currentThread().getName()));
        };
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        Thread t1 = new Thread(testGroup, runnable, "t1");
        t1.start();
        t1.resume();
        Thread t2 = new Thread(testGroup, runnable, "t2");
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t2.resume();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }


}
