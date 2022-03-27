package cn.com.thread.ta1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock支持锁可重入
 * @author lilibo
 * @create 2022-01-02 3:54 PM
 */
public class ReentrantLockDemo {

    public static final String format1 = "the currentThread name = %s 进入%s方法";

    private final Lock lock = new ReentrantLock();

    public void a() throws InterruptedException {
        if(lock.tryLock(5, TimeUnit.SECONDS)) {
            try{
                System.out.println(String.format(format1, Thread.currentThread().getName(), "a()"));
                b();
            }finally {
                TimeUnit.SECONDS.sleep(3);
                lock.unlock();
            }
        }
    }

    public void b() throws InterruptedException {
        if(lock.tryLock(5, TimeUnit.SECONDS)) {
            try{
                System.out.println(String.format(format1, Thread.currentThread().getName(), "b()"));
                c();
            }finally {
                lock.unlock();
            }
        }
    }

    public void c() throws InterruptedException {
        if(lock.tryLock(5, TimeUnit.SECONDS)) {
            try {
                System.out.println(String.format(format1, Thread.currentThread().getName(), "c()"));
            }finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        ReentrantLockDemo reentrantLockDemo = new ReentrantLockDemo();
        // 锁可重入示例
        new Thread(()->{
            try {
                reentrantLockDemo.a();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t1").start();
        new Thread(()->{
            try {
                reentrantLockDemo.a();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "t2").start();
    }
}
