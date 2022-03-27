package cn.com.thread.t6;

import java.util.concurrent.TimeUnit;

/**
 * 死锁示例
 *
 * @author lilibo
 * @create 2022-01-01 7:44 PM
 */
public class Demo3 {

    private final Object lock1 = new Object();

    private final Object lock2 = new Object();

    public static final String format1 = "the currentThread name = %s进入%s方法";

    public void call1() {
        synchronized (lock1) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock2) {
                System.out.println(String.format(format1, Thread.currentThread().getName(), "call1()"));
            }
        }
    }

    public void call2() {
        synchronized (lock2) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock1) {
                System.out.println(String.format(format1, Thread.currentThread().getName(), "call1()"));
            }
        }
    }

    public static void main(String[] args) {
        Demo3 demo3 = new Demo3();
        new Thread(() -> demo3.call1(), "t1").start();
        new Thread(() -> demo3.call2(), "t2").start();
    }

}
