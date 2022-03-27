package cn.com.thread.tb5;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * 循环CyclicBarrier测试
 *
 * @author lilibo
 * @create 2022-01-06 4:55 PM
 */
public class Demo {

    private final Random random = new Random();

    public static void main(String[] args) {
        Demo demo = new Demo();
        int parties = 5;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(parties, () -> System.out.println("所有测试线程已就绪!"));
        for (int i = 0; ; i++) {
            if(i == 0) {
                demo.waitParties(parties, cyclicBarrier);
            }
            if(cyclicBarrier.getNumberWaiting() == 0) {
                cyclicBarrier.reset();
                demo.waitParties(parties, cyclicBarrier);
            }
        }
    }


    private void waitParties(int parties, CyclicBarrier cyclicBarrier) {
        Stream.iterate(0, count -> count + 1).limit(parties).forEach((count) -> {
            new Thread(() -> {
                try {
//                    int randomSecond = random.nextInt(5);
//                    TimeUnit.SECONDS.sleep(randomSecond);
                    System.out.println("the currentThreadName = " + Thread.currentThread().getName() + "已就绪!");
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "test-" + count).start();
        });
    }

}
