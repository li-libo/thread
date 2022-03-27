package cn.com.thread.t2;

import java.util.concurrent.TimeUnit;

/**
 * 创建线程的多种方式-继承Thread
 * 测试线程中断
 * @author lilibo
 * @create 2021-12-31 3:09 PM
 */
public class Demo1 extends Thread{

    public Demo1(String name) {
        super(name);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()){
            try {
                System.out.println("the currentThread name = " + Thread.currentThread().getName() + " is running!");
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                // 异常会清理中断状态
                System.out.println("InterruptedException: the currentThread name = " + Thread.currentThread().getName() + ", is the current interrupted? " + Thread.currentThread().isInterrupted());
                // 重新标记中断状态
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("the currentThread name = " + Thread.currentThread().getName() + " is dying!");
    }

    public static void main(String[] args) throws InterruptedException {
        Demo1 t1 = new Demo1("t1");
        Demo1 t2 = new Demo1("t2");
        t1.start();
        t2.start();
        TimeUnit.SECONDS.sleep(1);
        t1.interrupt();
    }
}
