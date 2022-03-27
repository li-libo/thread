package cn.com.thread.tb9;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author lilibo
 * @create 2022-01-07 10:39 AM
 */
public class Cake {

    private final Random random = new Random();

    public Cake(String name, int price) throws InterruptedException {
        System.out.println("准备制作name = " + name + ", price = " + price + "蛋糕");
        TimeUnit.SECONDS.sleep(random.nextInt(10));
        this.name = name;
        this.price = price;
    }

    private String name;

    private int price;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Cake{" +
                "name='" + name + '\'' +
                ", price=" + price +
                '}';
    }
}
