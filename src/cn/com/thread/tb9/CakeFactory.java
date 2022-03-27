package cn.com.thread.tb9;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author lilibo
 * @create 2022-01-07 10:47 AM
 */
public class CakeFactory {

    public CakeFuture orderCake(String name, int price) throws InterruptedException {
        CakeFuture cakeFuture = new CakeFuture();
        new Thread(()->{
            try {
                Cake cake = new Cake(name, price);
                cakeFuture.set(cake);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "makeCake-Thread").start();
        return cakeFuture;
    }

}
