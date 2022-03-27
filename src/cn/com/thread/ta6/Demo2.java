package cn.com.thread.ta6;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * 使用wait/notify通讯示例
 * @author lilibo
 * @create 2022-01-03 9:22 PM
 */
public class Demo2 {

    private int signal;

    public int getSignal() {
        return signal;
    }

    public void setSignal(int signal) {
        this.signal = signal;
    }

    @Test
    public void test1() {
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (this) {
                setSignal(1);
                System.out.println("the currentThread name = " + Thread.currentThread().getName() + " notifyAll!");
                this.notifyAll();
            }
        }, "t1").start();

        new Thread(()->{
            synchronized (this) {
                try {
                    while (getSignal() == 0) {
                        System.out.println("the currentThread name = " + Thread.currentThread().getName() + " wait!");
                        this.wait();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("the currentThread name = " + Thread.currentThread().getName() + "恢复执行!");
            }
        }, "t2").start();

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
    }
}
