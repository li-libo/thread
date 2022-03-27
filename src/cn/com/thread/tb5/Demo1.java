package cn.com.thread.tb5;

import org.junit.Test;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-06 4:35 PM
 */
public class Demo1 {

    public static final String nameOfIdeaThreadInRunMode = "Monitor Ctrl-Break";

    private final Random random = new Random();

    @Test
    public void test1() {
        int parties = 100;
        CyclicBarrier cyclicBarrier = new CyclicBarrier(parties, () -> System.out.println("****所有人员已就绪!****"));
        Stream.iterate(0, count -> count + 1).limit(parties).forEach(count -> {
            new Thread(()->{
                try {
                    int randomSecond = random.nextInt(5);
                    TimeUnit.SECONDS.sleep(randomSecond);
                    System.out.println(Thread.currentThread().getName() + "已就绪!");
                    cyclicBarrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, "员工-" + count).start();
        });

        Thread monitorThread = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                    System.out.println("当前还有" + cyclicBarrier.getNumberWaiting() + "线程在等待!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "monitorThread");
        monitorThread.setDaemon(true);
        monitorThread.start();

        int count = 2;
        while (Thread.activeCount() > count) {
            Set<String> threadNameSet = Thread.getAllStackTraces().keySet().stream().map(thread -> thread.getName()).collect(Collectors.toSet());
            if(threadNameSet.contains(nameOfIdeaThreadInRunMode)) {
                count = 3;
            }
            Thread.yield();
        }
    }

}
