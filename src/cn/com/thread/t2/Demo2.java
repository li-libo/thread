package cn.com.thread.t2;

/**
 * 线程的多种创建方式, 继承Runnable接口
 * 利用wait/notify两个线程交替输出
 * @author lilibo
 * @create 2021-12-31 3:22 PM
 */
public class Demo2 implements Runnable, ConsoleColors{

    @Override
    public synchronized void run() {
        while (true) {
            try {
                System.out.println(GREEN + "the currentThread name " + Thread.currentThread().getName() + " is running!" + RESET);
                this.notify();
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(BLUE + "the currentThread name = " + Thread.currentThread().getName() + " is restoring!" + RESET);
        }
    }

    public static void main(String[] args) {
        Demo2 run = new Demo2();
        Thread t1 = new Thread(run, "t1");
        Thread t2 = new Thread(run, "t2");
        t1.start();
        t2.start();
    }
}
