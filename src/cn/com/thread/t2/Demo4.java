package cn.com.thread.t2;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 创建线程的多种方式, 带返回值的线程
 * Map的compute和merge
 *
 * @author lilibo
 * @create 2021-12-31 4:46 PM
 */
public class Demo4 implements Callable<String> {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        FutureTask<String> futureTask1 = new FutureTask<>(new Demo4());
        // 直接使用主线程执行
//        futureTask1.run();
//        TimeUnit.SECONDS.sleep(2);
//        String result = futureTask1.get();
//        System.out.println(result);

        new Thread(futureTask1, "test-1").start();
        System.out.println(futureTask1.get());

        // 测试Callable适配器
        Callable<String> call2 = Executors.callable(() -> {
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + " is running!");
        }, "callable适配器");
        FutureTask futureTask2 = new FutureTask<>(call2);
        Thread thread = new Thread(futureTask2);
        thread.start();
        System.out.println(futureTask2.get());

        // Map的compute和merge
        Map<String, Integer> testMap = new ConcurrentHashMap<>();
        int limit = 5;
        Stream.iterate(0, count -> count + 1).limit(limit).forEach(count -> {
            String key = "key-" + count;
            testMap.put(key, count);
        });
        System.out.println("初次填充完数据testMap为: " + testMap);

        Stream.iterate(0, count -> count + 1).limit(limit).forEach(count -> {
            String key = "key-" + count;
            testMap.compute(key, (k, v) -> {
                System.out.println("参数k为" + k);
                return v == null ? 0 : v + 5;
            });
        });
        System.out.println("执行compute操作后testMap为: " + testMap);

        Stream.iterate(0, count -> count + 1).limit(limit).forEach(count->{
            String key = "key-" + count;
            testMap.merge(key, 33, (oldValue, value) -> {
                System.out.println("参数value为" + value);
                return oldValue + value;
            });
        });
        System.out.println("执行merge操作后testMap为: " + testMap);
    }

    @Override
    public String call() throws Exception {
        TimeUnit.SECONDS.sleep(1);
        System.out.println("the current Thread - " + Thread.currentThread());
        return UUID.randomUUID().toString();
    }
}
