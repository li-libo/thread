package cn.com.thread.t2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 创建线程的多种方式, 使用ThreadPoolExecutor
 * @author lilibo
 * @create 2021-12-31 7:12 PM
 */
public class Demo6 {

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        int limit = 16;
        Stream.iterate(0, count -> count + 1).limit(limit).forEach(count -> {
            executorService.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println("the currentThreadName = " + Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        TimeUnit.SECONDS.sleep(10);
        executorService.shutdown();
    }
}
