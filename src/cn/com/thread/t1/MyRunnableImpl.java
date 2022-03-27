package cn.com.thread.t1;

/**
 * @author lilibo
 * @create 2021-12-30 9:00 PM
 */
public class MyRunnableImpl implements Runnable{
    @Override
    public synchronized void run() {
        while(true){
            System.out.println("the current ThreadName is " + Thread.currentThread().getName());
            try{
                wait();
            }catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("the current ThreadName = " + Thread.currentThread().getName() + " is running again!");
        }
    }

    public static void main(String[] args) {
        MyRunnableImpl myRunnable = new MyRunnableImpl();
//        myRunnable.run();
        new Thread(myRunnable, "newThread").start();
        while (true) {
            synchronized (myRunnable) {
                System.out.println("the main Thread is running!");
                myRunnable.notifyAll();
            }
        }
    }
}
