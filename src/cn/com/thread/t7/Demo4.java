package cn.com.thread.t7;

import java.util.concurrent.TimeUnit;

/**
 * @author lilibo
 * @create 2022-01-01 8:52 PM
 */
public class Demo4 {

    private static boolean blockFlag = true;

    public static void main(String[] args) {
        new Thread(()->{
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + " is running! set blockFlag = false");
            blockFlag = false;
        }, "t1").start();

        new Thread(()->{
            while(blockFlag) {
                //System.out.println(blockFlag);
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + " is running!");
        }, "t2").start();

        new Thread(()-> {
            while (blockFlag) {
                Thread.yield();
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + " is running!");
        }, "t3").start();
    }
}
