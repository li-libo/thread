package cn.com.thread.t6;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 自旋示例
 * @author lilibo
 * @create 2022-01-01 7:02 PM
 */
public class Demo2 {

    public static final String format1 = "the currentThread name = %s线程执行..., ThreadGroup = %s";

    public static final String format2 = "the currentThread name = %s线程执行完毕!, ThreadGroup = %s";

    public static void main(String[] args) {
        Runnable runnable = () -> {
            try {
                System.out.println(String.format(format1, Thread.currentThread().getName(), Thread.currentThread().getThreadGroup()));
                TimeUnit.SECONDS.sleep(1);
                System.out.println(String.format(format2, Thread.currentThread().getName(), Thread.currentThread().getThreadGroup()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        int numOfTest = 5;
        Stream.iterate(0, count -> count + 1).limit(numOfTest).forEach(count -> {
            new Thread(runnable, "test-" + count).start();
        });
        // 自旋
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println("所有线程执行完毕了!");
    }

}
