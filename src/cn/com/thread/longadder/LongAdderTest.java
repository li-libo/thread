package cn.com.thread.longadder;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-13 8:43 PM
 */
public class LongAdderTest {

    private final LongAdder longAdder = new LongAdder();

    public static final int OUTER_LOOP = 10;

    public static final int INNER_LOOP = 5000;

    public static final int NUM_TEST_THREAD = Runtime.getRuntime().availableProcessors() - 1;

    public static final String format1 = "第%s次循环计算结果:%s";

    @Test
    public void test1() {
        Stream.iterate(0, outerLoop -> outerLoop + 1).limit(OUTER_LOOP).forEach(outerLoop -> {
            longAdder.reset();
            CountDownLatch countDownLatch = new CountDownLatch(NUM_TEST_THREAD);
            Stream.iterate(0, numOfThread -> numOfThread + 1).limit(NUM_TEST_THREAD).forEach(numOfThread -> {
                new Thread(()-> {
                    Stream.iterate(0, innerLoop -> innerLoop + 1).limit(INNER_LOOP).forEach(innerLoop -> {
                        longAdder.increment();
                    });
                    countDownLatch.countDown();
                }, "testThread-" + numOfThread).start();
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(String.format(format1, outerLoop, longAdder.sum()));
        });
    }

}
