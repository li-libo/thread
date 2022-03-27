package cn.com.thread.td4;

import org.junit.Test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-07 8:47 PM
 */
public class ThreadPoolDemo1 {

    public static final String format1 = "线程池中线程数目: %s, 队列中等待执行任务数: %s, 执行完任务数: %s";

    @Test
    public void test1() {
        int coreSize = 5;
        int maxSize = 10;
        int queueCapacity = 5;
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5, 10, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<>(queueCapacity));
        Stream.iterate(0, count -> count + 1).limit(maxSize + queueCapacity).forEach(count -> {
            threadPoolExecutor.submit(() -> {
                System.out.println("the currentThread name = " + Thread.currentThread().getName() + "is running!");
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("the currentThread name = " + Thread.currentThread().getName() + "is dying!");
            });
        });
        Thread daemonThread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                    int poolSize = threadPoolExecutor.getPoolSize();
                    long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
                    int queueSize = threadPoolExecutor.getQueue().size();
                    System.out.println(String.format(format1, poolSize, queueSize, completedTaskCount));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "daemonThread");
        daemonThread.setDaemon(true);
        daemonThread.start();

        while (threadPoolExecutor.getCompletedTaskCount() <  (maxSize + queueCapacity)) {
            Thread.yield();
        }
        threadPoolExecutor.shutdown();
    }

}

class MyThreadFactory implements ThreadFactory {

    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    MyThreadFactory(ThreadGroup group) {
        SecurityManager s = System.getSecurityManager();
        this.group = group;
        namePrefix = "pool-" +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }

}