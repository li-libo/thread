package cn.com.thread.tb2;

import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 当线程结束时,会唤醒notify所有阻塞在该线程上的线程
 * @author lilibo
 * @create 2022-01-05 7:10 PM
 */
public class Demo1 {

    public static final String nameOfIdeaThreadInRunMode = "Monitor Ctrl-Break";

    public static final String format1 = "the currentThread name = %s准备阻塞!";

    public static final String format2 = "the currentThread name = %s恢复执行";

    @Test
    public void test1() {
        Thread blockThread = new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "blockThread");

        int numOfTestThread = 5;
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(() -> {
                synchronized (blockThread) {
                    try {
                        System.out.println(String.format(format1, Thread.currentThread().getName()));
                        blockThread.wait();
                        System.out.println(String.format(format2, Thread.currentThread().getName()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "waitThread-" + count).start();
        });

        blockThread.start();
        int count = 1;
        while (Thread.activeCount() > count) {
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            Set<String> threadNameSet = allStackTraces.keySet().stream().map(thread -> thread.getName()).collect(Collectors.toSet());
            if(threadNameSet.contains(nameOfIdeaThreadInRunMode)) {
                count = 2;
            }
            Thread.yield();
        }
    }
}
