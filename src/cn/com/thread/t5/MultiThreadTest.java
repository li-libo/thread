package cn.com.thread.t5;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-01 4:54 PM
 */
public class MultiThreadTest {

    public static final int numOfTest = 1000;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(numOfTest);
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);
        Stream.iterate(1, count -> count + 1).limit(numOfTest).forEach(count->{
            executorService.submit(()->{
                Singleton instance = Singleton.getInstance();
                System.out.println("the current Thread name = " + Thread.currentThread().getName() + " getInstance() = " + instance);
                countDownLatch.countDown();
            });
        });
        countDownLatch.await();
        executorService.shutdown();
    }
}
