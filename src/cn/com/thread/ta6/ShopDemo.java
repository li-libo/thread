package cn.com.thread.ta6;

import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-04 3:32 PM
 */
public class ShopDemo {

    public static final String nameOfIdeaThreadInRunMode = "Monitor Ctrl-Break";

    @Test
    public void test1() {
        IShop shop = new Shop1();
        int numOfPush = 2;
        Stream.iterate(0, count -> count + 1).limit(numOfPush).limit(numOfPush).forEach(count -> {
           new Thread(()->{
               while (true) {
                   try {
                       shop.push();
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
               }
           }, "push-" + count).start();
        });

        int numOfTake = 5;
        Stream.iterate(0, count -> count + 1).limit(numOfTake).forEach(count -> {
            new Thread(()->{
                while (true) {
                    try {
                        shop.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "take-" + count).start();
        });

        int finalThreadCount = 1;
        while (Thread.activeCount() > finalThreadCount) {
            Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
            Set<String> threadNameSet = threadMap.keySet().stream().map(t -> t.getName()).collect(Collectors.toSet());
            if(threadNameSet.contains(nameOfIdeaThreadInRunMode)) {
                finalThreadCount = 2;
            }
            Thread.yield();
        }
    }

}

interface IShop {
    void push() throws InterruptedException;
    void take() throws InterruptedException;
}

class Shop implements IShop{

    public static final int MAX = 10;

    private int count;

    @Override
    public synchronized void push() throws InterruptedException {
        while (count >= 10) {
            System.out.println("the shop is full!");
            this.wait();
        }
        count++;
        System.out.println("push good to the shop, the count = " + count);
        this.notifyAll();
    }

    @Override
    public synchronized void take() throws InterruptedException {
        while (count <= 0) {
            System.out.println("the shop is empty!");
            this.wait();
        }
        count--;
        System.out.println("take good from the shop, the count = " + count);
        this.notifyAll();
    }
}

class Shop1 implements IShop {

    private final Lock myLock = new ReentrantLock();

    private final Condition pushCondition = myLock.newCondition();

    private final Condition takeCondition = myLock.newCondition();

    public static final int MAX_CAPACITY = 10;

    private int count;

    @Override
    public void push() throws InterruptedException {
        try{
            myLock.lock();
            while (count >= MAX_CAPACITY) {
                System.out.println("the shop is full, count = " + count);
                pushCondition.await();
            }
            count++;
            System.out.println("push good to the shop, the count = " + count);
            takeCondition.signalAll();
        }finally {
            myLock.unlock();
        }
    }

    @Override
    public void take() throws InterruptedException {
        try{
            myLock.lock();
            while (count <= 0) {
                System.out.println("the shop is empty, count = " + count);
                takeCondition.await();
            }
            count--;
            System.out.println("take good from the shop, the count = " + count);
            pushCondition.signalAll();
        }finally {
            myLock.unlock();
        }
    }
}
