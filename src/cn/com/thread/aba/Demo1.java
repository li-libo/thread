package cn.com.thread.aba;

import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-17 8:22 PM
 */
public class Demo1 {

    private int initMoney = 19;

    private final AtomicReference<Integer> atomicReference = new AtomicReference<>(initMoney);

    private final AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(initMoney, 0);

    public static final String format1 = "the currentThread name = %s 充值成功!, 当前金额 = %s";

    public static final String format2 = "the currentThread name = %s 消费成功!, 当前金额 = %s";

    /**
     * 模拟ABA问题
     */
    @Test
    public void test1() {
        int loop = 10;
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<>());
        executorService.allowCoreThreadTimeOut(true);
        Runnable rechargeRunn = () -> {
            Stream.iterate(0, count -> count + 1).limit(loop).forEach(count -> {
                if(atomicReference.get() == initMoney) {
                    if(atomicReference.compareAndSet(atomicReference.get(), atomicReference.get() + 20)) {
                        System.out.println(String.format(format1, Thread.currentThread().getName(), atomicReference.get()));
                    }
                }
            });
        };
        Runnable consumerRunn = () -> {
            Stream.iterate(0, count -> count + 1).limit(loop).forEach(count -> {
                if(atomicReference.get() >= 20) {
                    if(atomicReference.compareAndSet(atomicReference.get(), atomicReference.get() - 20)) {
                        System.out.println(String.format(format2, Thread.currentThread().getName(), atomicReference.get()));
                    }
                }
            });
        };

        Stream.iterate(0, count -> count + 1).limit(10).forEach(count -> {
            if(count % 2 == 0) {
                executorService.submit(rechargeRunn);
            }else {
                executorService.submit(consumerRunn);
            }
        });

        while (executorService.getPoolSize() > 0) {
            Thread.yield();
        }
        executorService.shutdown();
    }

    /**
     * 使用java.util.concurrent.atomic.AtomicStampedReference解决ABA问题
     */
    @Test
    public void test2() {
        int loop = 10;
        ThreadPoolExecutor executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<>());
        executorService.allowCoreThreadTimeOut(true);
        Runnable rechargeRunn = () -> {
          Stream.iterate(0, count -> count + 1).limit(loop).forEach(count -> {
              int money = atomicStampedReference.getReference();
              if(money == initMoney) {
                  if(atomicStampedReference.compareAndSet(money, money+ 20, 0, 1)) {
                      System.out.println(String.format(format1, Thread.currentThread().getName(), atomicStampedReference.getReference()));
                  }
              }
          });
        };

        Runnable consumerRunn = () -> {
            Stream.iterate(0, count -> count + 1).limit(loop).forEach(count -> {
                int stamp = atomicStampedReference.getStamp();
                int money = atomicStampedReference.getReference();
                if(money >= 20) {
                    if(atomicStampedReference.compareAndSet(money, money - 20, stamp, stamp + 1)) {
                        System.out.println(String.format(format2, Thread.currentThread().getName(), atomicStampedReference.getReference()));
                    }
                }
            });
        };

        Stream.iterate(0, count -> count + 1).limit(10).forEach(count -> {
            if(count % 2 == 0) {
                executorService.submit(rechargeRunn);
            }else {
                executorService.submit(consumerRunn);
            }
        });

        while (executorService.getPoolSize() > 0) {
            Thread.yield();
        }
        executorService.shutdown();
    }

}
