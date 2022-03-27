package cn.com.thread.tc9;

import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-07 7:12 PM
 */
public class ShopDemo {

    private final int capacity = 10;

    @Test
    public void test1() {
        Shop shop = new PingDuoDuo(capacity);
        int numOfPutThread = 3;
        ThreadGroup putThreadGroup = new ThreadGroup("putThreadGroup");
        Stream.iterate(0, count -> count + 1).limit(numOfPutThread).forEach(count -> {
            new Thread(putThreadGroup, ()->{
                while (true) {
                    try {
                        shop.put(new Commodity());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "putThread-" + count).start();
        });

        int numOfTakeThread = 5;
        ThreadGroup takeThreadGroup = new ThreadGroup("takeThreadGroup");
        Stream.iterate(0, count -> count + 1).limit(numOfTakeThread).forEach(count -> {
            new Thread(takeThreadGroup, ()->{
                while (true) {
                    try {
                        shop.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "takeThread-" + count).start();
        });

        ThreadGroup sizeThreadGroup = new ThreadGroup("sizeThread");
        Thread sizeThread = new Thread(sizeThreadGroup, () -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                    System.out.println("size = " + shop.size());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "sizeThread");
        sizeThread.setDaemon(true);
        sizeThread.start();

        while(putThreadGroup.activeCount() > 0 || takeThreadGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

}
