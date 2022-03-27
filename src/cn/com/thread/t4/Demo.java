package cn.com.thread.t4;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程优先级测试案例
 *
 * @author lilibo
 * @create 2022-01-01 4:22 PM
 */
public class Demo {

    /**
     * 线程运行计数器Map, key为threadName
     */
    private static final Map<String, AtomicInteger> countMap = new ConcurrentHashMap<>();

    public static final int totalNumOfTest = 100 * 10000;

    private static final AtomicInteger totalCount = new AtomicInteger();

    public static void main(String[] args) {
        Runnable runnable = () -> {
            while (totalCount.get() < totalNumOfTest){
                String threadName = Thread.currentThread().getName();
                countMap.compute(threadName, (k, v) -> {
                    if (v == null) {
                        return new AtomicInteger(1);
                    } else {
                        v.incrementAndGet();
                        return v;
                    }
                });
                totalCount.incrementAndGet();
            }
        };
        Thread t1 = new Thread(runnable, "高优先级线程");
        t1.setPriority(Thread.MAX_PRIORITY);
        t1.start();

        Thread t2 = new Thread(runnable, "低优先级线程");
        t2.setPriority(Thread.MIN_PRIORITY);
        t2.start();

        // intellij idea run运行程序时,会增加1个额外线程; debug运行程序时不会; 所以下述判断条件为>2
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }

        countMap.forEach((k, v) -> {
            System.out.println("key = " + k + ", value = " + v);
        });
    }
}
