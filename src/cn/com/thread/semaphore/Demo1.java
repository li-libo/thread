package cn.com.thread.semaphore;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-16 9:17 PM
 */
public class Demo1 {

    public static final String format1 = "the currentThread name = %s 获取许可, permit = %s";

    public static final String format2 = "the currentThread name = %s 释放许可, permit = %s";

    public static final String format3 = "the currentThread name = %s 3秒内获取许可失败!, permit = %s";

    /**
     * 释放许可正确姿势
     * @throws InterruptedException
     */
    @Test
    public void test1() throws InterruptedException {
        Semaphore semaphore = new Semaphore(1);
        Runnable runnable = () -> {
            boolean acquirePermitFlag = false; // 加入获取许可标志
            try{
                semaphore.acquire();
                acquirePermitFlag = true;
                TimeUnit.SECONDS.sleep(2);
                System.out.println(String.format(format1, Thread.currentThread().getName(), semaphore.availablePermits()));
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            } finally {
                if(acquirePermitFlag) {
                    semaphore.release();
                }
                System.out.println(String.format(format2, Thread.currentThread().getName(), semaphore.availablePermits()));
            };
        };

        ThreadGroup testGroup = new ThreadGroup("testGroup");
        new Thread(testGroup, runnable, "t1").start();
        TimeUnit.SECONDS.sleep(1);
        Thread t2 = new Thread(testGroup, runnable, "t2");
        t2.start();
        Thread t3 = new Thread(testGroup, runnable, "t2");
        t3.start();
        TimeUnit.SECONDS.sleep(1);
        t2.interrupt();
        t3.interrupt();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    @Test
    public void test2() {
        Semaphore semaphore = new Semaphore(3);
        Runnable runnable = () -> {
          boolean acquirePermitFlag = false;
          try{
              acquirePermitFlag = semaphore.tryAcquire(3, TimeUnit.SECONDS);
              if(acquirePermitFlag) {
                  System.out.println(String.format(format1, Thread.currentThread().getName(), semaphore.availablePermits()));
                  TimeUnit.SECONDS.sleep(5);
              }else {
                  System.out.println(String.format(format3, Thread.currentThread().getName(), semaphore.availablePermits()));
              }
          } catch (InterruptedException interruptedException) {
              interruptedException.printStackTrace();
          } finally {
              if(acquirePermitFlag) {
                  semaphore.release();
                  System.out.println(String.format(format2, Thread.currentThread().getName(), semaphore.availablePermits()));
              }
          }
        };
        ThreadGroup testGroup = new ThreadGroup("g1");
        int numOfTest = 9;
        Stream.iterate(0, count -> count + 1).limit(numOfTest).forEach(count -> {
            new Thread(testGroup, runnable, "test-" + count).start();
        });
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }
}
