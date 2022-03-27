package cn.com.thread.longadder;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.LongAccumulator;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-13 9:15 PM
 */
public class LongAccumulatorTest {

    private final LongAccumulator longAccumulator = new LongAccumulator((x, y) -> x + y, 0);

    public static final int OUTTER_LOOP = 10;

    public static final int NUM_TEST_THREAD = Runtime.getRuntime().availableProcessors() - 1;

    public static final int INNER_LOOP = 5000;

    @Test
    public void test1() {
        Stream.iterate(0, outterLoop -> outterLoop + 1).limit(OUTTER_LOOP).forEach((outterLoop) -> {
            longAccumulator.reset();
            CountDownLatch countDownLatch = new CountDownLatch(NUM_TEST_THREAD);
            Stream.iterate(0, numOfTestThread -> numOfTestThread + 1).limit(NUM_TEST_THREAD).forEach(numOfTestThread -> {
                new Thread(()->{
                    Stream.iterate(0, innerLoop -> innerLoop + 1).limit(INNER_LOOP).forEach(innerLoop -> {
                        longAccumulator.accumulate(1);
                    });
                    countDownLatch.countDown();
                }, "test-" + numOfTestThread).start();
            });
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(String.format("count = %s, the currentThread name = %s, value = %s", outterLoop, Thread.currentThread().getName(), longAccumulator.longValue()));
        });
    }
}
