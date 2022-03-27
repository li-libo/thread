package cn.com.thread.visualvm;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author lilibo
 * @create 2022-01-15 4:56 PM
 */
public class Demo1 {

    private final Object obj1 = new Object();

    private final Object obj2 = new Object();

    /**
     * 死锁示例
     */
    @Test
    public void test1() {
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        new Thread(testGroup, new SynAddRunnable(obj1, obj2, true), "t1").start();
        new Thread(testGroup, new SynAddRunnable(obj1, obj2, false), "t2").start();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * 饥饿死锁示例
     */
    @Test
    public void test2() throws ExecutionException, InterruptedException {
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        Callable<String> anotherCallable = () -> {
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + " in anotherCallable");
            return "anotherCallable access!";
        };

        Callable<String> myCallable = () -> {
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + " in myCallable");
            Future<String> future1 = singleThreadExecutor.submit(anotherCallable);
            return "success" + future1.get();
        };
        getPid();
        Future<String> future2 = singleThreadExecutor.submit(myCallable);
        System.out.println("the currentThread name = " + Thread.currentThread().getName() + " future2.get() = " + future2.get());
        System.out.println("process over!");
        singleThreadExecutor.shutdown();
    }

    public static String getPid() {
        // get name representing the running Java Virtual machine
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println("the name of running Java Virtual machine = " + name);
        String pid = name.split("@")[0];
        System.out.println("pid = " + pid);
        return pid;
    }

}

class SynAddRunnable implements Runnable{

    private Object lock1;

    private Object lock2;

    private boolean flag;

    public static final String format1 = "the currentThread name = %s is running;";

    public SynAddRunnable(Object lock1, Object lock2, boolean flag) {
        this.lock1 = lock1;
        this.lock2 = lock2;
        this.flag = flag;
    }

    @Override
    public void run() {
        if(flag) {
            synchronized (lock1) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock2) {
                    System.out.println(String.format(format1, Thread.currentThread().getName()));
                }
            }
        }else {
            synchronized (lock2) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (lock1) {
                    System.out.println(String.format(format1, Thread.currentThread().getName()));
                }
            }
        }
    }
}