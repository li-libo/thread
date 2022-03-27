package cn.com.thread.tb9;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Future原理示例,Future类似于1个订单
 * @author lilibo
 * @create 2022-01-07 10:23 AM
 */
public class MyFutureDemo {

    private final CakeFactory cakeFactory = new CakeFactory();

    private final Random random = new Random();

    @Test
    public void test1() {
        int numOfTestThread = Runtime.getRuntime().availableProcessors();
        List<CakeFuture> cakeFutureList = Collections.synchronizedList(new ArrayList<>());
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            try {
                CakeFuture cakeFuture = cakeFactory.orderCake("蛋糕-" + count, 50 + random.nextInt(50));
                cakeFutureList.add(cakeFuture);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Set<Cake> cakeSet = Collections.synchronizedSet(new HashSet<>());
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(()-> {
                try {
                    Cake cake = cakeFutureList.get(count).get();
                    System.out.println("取出蛋糕: " + cake);
                    cakeSet.add(cake);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, "getCake-" + count).start();
        });

        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
        System.out.println(cakeSet);
    }
}
