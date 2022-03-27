package cn.com.thread.ta6;

import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 线程同步通讯示例
 * @author lilibo
 * @create 2022-01-04 10:46 AM
 */
public class Demo3 {

    private int signal;

    public static final String format1 = "the currentThread name = %s恢复执行";

    public static final String format2 = "the currentThread name = %s准备wait";

    public static final String format3 = "the currentThread name = %s开始notifyAll";

    public synchronized int get() throws InterruptedException {
        while (signal == 0) {
            System.out.println(String.format(format2, Thread.currentThread().getName()));
            this.wait();
        }
        System.out.println(String.format(format1, Thread.currentThread().getName()));
        return signal;
    }

    public void set() throws InterruptedException {
        TimeUnit.SECONDS.sleep(5);
        synchronized (this) {
            System.out.println(String.format(format3, Thread.currentThread().getName()));
            signal = 1;
            this.notifyAll();
        }
    }

    public static final String nameOfIdeaThreadInRunMode = "Monitor Ctrl-Break";

    @Test
    public void test1() {
        Demo3 demo3 = new Demo3();
        int numOfSet = 1;
        int numOfGet = 5;
        Stream.iterate(0, count -> count + 1).limit(numOfGet).forEach(count -> {
            new GetThread("测试Get线程-" + count, demo3).start();
        });
        Stream.iterate(0, count -> count + 1).limit(numOfSet).forEach(count -> {
            new SetThread("测试Set线程-" + count, demo3).start();
        });
        int count = 1;
        while(Thread.activeCount() > count) {
            Thread.currentThread().getThreadGroup().list();
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            Set<String> threadNameSet = allStackTraces.keySet().stream().map(t -> t.getName()).collect(Collectors.toSet());
            if(threadNameSet.contains(nameOfIdeaThreadInRunMode)) {
                count = 2;
            }
            Thread.yield();
        }
    }

}

class SetThread extends Thread {

    private Demo3 demo3;

    public SetThread(String name, Demo3 demo3) {
        super(name);
        this.demo3 = demo3;
    }

    @Override
    public void run() {
        try {
            demo3.set();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}

class GetThread extends Thread {
    private Demo3 demo3;

    public GetThread(String name, Demo3 demo3) {
        super(name);
        this.demo3 = demo3;
    }

    @Override
    public void run() {
        try {
            int signal = demo3.get();
            System.out.println("the currentThread name = %s 获取signal = " + signal);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
