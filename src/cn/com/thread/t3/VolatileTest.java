package cn.com.thread.t3;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * volatile只能保证可见性.不能保证原子性
 * @author lilibo
 * @create 2021-12-31 8:06 PM
 */
public class VolatileTest {

    private static volatile int value;

    public static final int numOfThread = 5;

    public static final int loop = 10000;

    public static void main(String[] args) throws InterruptedException {
        Stream.iterate(0, count -> count + 1).limit(numOfThread).forEach(count -> {
            new Thread(()-> {
                Stream.iterate(0, count1 -> count1 + 1).limit(loop).forEach(count1-> {
                    value++;
                });
            }, "thread-" + count).start();
        });
        while (Thread.activeCount() > 1) {
            Thread.currentThread().getThreadGroup().list();
            Thread.yield();
        }
        System.out.println("value = " + value);
    }
}
