package cn.com.thread.ta6;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 利用volatile+自旋循环实现线程通讯简单示例
 * @author lilibo
 * @create 2022-01-03 9:01 PM
 */
public class Demo1 {

    private volatile int signal;

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    @Test
    public void test1() {
        new Thread(() -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + "设置signal为1!");
            setSignal(1);
        }, "t1").start();

        new Thread(()->{
            while (getSignal() == 0) {
                System.out.println("the currentThread name = " + Thread.currentThread().getName() + "等待...");
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + "恢复执行!");
        }, "t2").start();

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
    }
}
