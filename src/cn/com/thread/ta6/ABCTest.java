package cn.com.thread.ta6;

import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author lilibo
 * @create 2022-01-04 2:39 PM
 */
public class ABCTest {

    public static final String nameOfIdeaThreadInRunMode = "Monitor Ctrl-Break";

    @Test
    public void test1() {
        IABC abc = new ABC1();
        new Thread(() -> {
            while (true) {
                try {
                    abc.printA();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "printA").start();

        new Thread(() -> {
            while(true) {
                try {
                    abc.printB();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "printB").start();

        new Thread(() -> {
            while (true) {
                try {
                    abc.printC();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "printC").start();

        int count = 1;
        while (Thread.activeCount() > count) {
            Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
            Set<String> threadNameSet = threadMap.keySet().stream().map(t -> t.getName()).collect(Collectors.toSet());
            if(threadNameSet.contains(nameOfIdeaThreadInRunMode)) {
                count = 2;
            }
            Thread.yield();
        }
    }
}

class ABC implements IABC {

    public static final String format1 = "the currentThread name = %s 打印%s";

    private String flag = "A";

    @Override
    public synchronized void printA() throws InterruptedException {
        while (!"A".equals(flag)) {
            this.wait();
        }
        System.out.println(String.format(format1, Thread.currentThread().getName(), "A"));
        flag = "B";
        this.notifyAll();
    }

    @Override
    public synchronized void printB() throws InterruptedException {
        while (!"B".equals(flag)) {
            this.wait();
        }
        System.out.println(String.format(format1, Thread.currentThread().getName(), "B"));
        flag = "C";
        this.notifyAll();
    }

    @Override
    public synchronized void printC() throws InterruptedException {
        while (!"C".equals(flag)) {
            this.wait();
        }
        System.out.println(String.format(format1, Thread.currentThread().getName(), "C"));
        flag = "A";
        this.notifyAll();
    }
}

/**
 * 使用Condition的await/signal可以更精准的实现等待/唤醒
 */
class ABC1 implements IABC {

    public static final String format1 = "the currentThread name = %s 打印%s";

    private String flag = "A";

    private final ReentrantLock myLock = new ReentrantLock();

    private final Condition conditionA = myLock.newCondition();

    private final Condition conditionB = myLock.newCondition();

    private final Condition conditionC = myLock.newCondition();

    @Override
    public void printA() throws InterruptedException {
        try{
            myLock.lock();
            while (!"A".equals(flag)) {
                conditionA.await();
            }
            System.out.println(String.format(format1, Thread.currentThread().getName(), "A"));
            flag = "B";
            conditionB.signalAll();
        }finally {
            myLock.unlock();
        }
    }

    @Override
    public void printB() throws InterruptedException {
        try{
            myLock.lock();
            while (!"B".equals(flag)) {
                conditionB.await();
            }
            System.out.println(String.format(format1, Thread.currentThread().getName(), "B"));
            flag = "C";
            conditionC.signalAll();
        }finally {
            myLock.unlock();
        }
    }

    @Override
    public void printC() throws InterruptedException {
        try{
            myLock.lock();
            while (!"C".equals(flag)) {
                conditionC.await();
            }
            System.out.println(String.format(format1, Thread.currentThread().getName(), "C"));
            flag = "A";
            conditionA.signalAll();
        }finally {
            myLock.unlock();
        }
    }
}

interface IABC {

    void printA() throws InterruptedException;

    void printB() throws InterruptedException;

    void printC() throws InterruptedException;
}