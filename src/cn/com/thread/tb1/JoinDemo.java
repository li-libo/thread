package cn.com.thread.tb1;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author lilibo
 * @create 2022-01-04 5:08 PM
 */
public class JoinDemo {

    public static final String format1 = "the currentThread name = %s is running!";

    public static final String format2 = "the currentThread name = %s is dying!";

    @Test
    public void test1() throws InterruptedException {
        Thread joinThread = new Thread(() -> {
            try {
                System.out.println(String.format(format1, Thread.currentThread().getName()));
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "joinThread");
        Thread startThread = new Thread(() -> {
            try {
                System.out.println(String.format(format1, Thread.currentThread().getName()));
                joinThread.start();
                joinThread.join();
                System.out.println(String.format(format2, joinThread.getName()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "startThread");
        startThread.start();
        startThread.join();
        System.out.println(String.format(format2, startThread.getName()));
    }
}
