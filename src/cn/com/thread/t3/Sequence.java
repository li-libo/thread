package cn.com.thread.t3;

import java.util.concurrent.TimeUnit;

/**
 * 线程安全性问题
 *      多线程环境下
 *      多个线程共享1个资源
 *      对资源进行非原子性操作
 * @author lilibo
 * @create 2021-12-31 8:52 PM
 */
public class Sequence {

    private static int value;

    /**
     * synchronized放在普通方法上, 内置锁就是当前类的实例, 即this
     * @return
     */
    public synchronized int getNext() {
        return value++;
    }

    /**
     * synchronized放在静态方法上,锁对象是类的class字节码对象
     * @return
     */
    public static synchronized int getPrevious() {
        return value--;
    }

    public int xx() {
        // monitorEnter
        synchronized (Sequence.class) {
            if(value > 0) {
                return value;
            }else {
                return -1;
            }
        }
        // monitorExit
    }

    public static void main(String[] args) {
        Sequence sequence = new Sequence();
        new Thread(() -> {
            while(true){
                try {
                    System.out.println("the currentThreadName = " + Thread.currentThread().getName() + ", getNext() = " + sequence.getNext());
                    TimeUnit.MILLISECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1").start();

        new Thread(() -> {
            while(true){
                try {
                    System.out.println("the currentThreadName = " + Thread.currentThread().getName() + ", getNext() = " + sequence.getNext());
                    TimeUnit.MILLISECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t2").start();

        new Thread(() -> {
            while(true){
                try {
                    System.out.println("the currentThreadName = " + Thread.currentThread().getName() + ", getNext() = " + sequence.getNext());
                    TimeUnit.MILLISECONDS.sleep(15);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t3").start();
    }
}
