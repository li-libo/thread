package cn.com.thread.completablefuture;

import javafx.util.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author lilibo
 * @create 2022-01-13 3:23 PM
 */
public class CompletableFutureTest1 {

    private final Random random = new Random();

    public static final String format1 = "the currentThreadName = %s is running!";

    public static final String format2 = "the currentThreadName = %s is dying!";

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        Runnable runnable = () -> {
            int randomSeconds = 1 + random.nextInt(9);
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            try {
                TimeUnit.SECONDS.sleep(randomSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(String.format(format2, Thread.currentThread().getName()));
        };
        ThreadGroup threadGroup = new ThreadGroup("g1");
        Executor executor = new ThreadPoolExecutor(0, 1, 1, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new MyDefaultThreadFactory1(threadGroup, "无返回值-"));
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(runnable, executor);
        System.out.println("f1.get() = " + f1.get());
    }

    @Test
    public void test2() throws ExecutionException, InterruptedException {
        Supplier<String> supplier = () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            return UUID.randomUUID().toString();
        };
        ThreadGroup g2 = new ThreadGroup("g2");
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(supplier, new ThreadPoolExecutor(0, 1, 1, TimeUnit.SECONDS, new SynchronousQueue<>(), new MyDefaultThreadFactory1(g2, "有返回值-")));
        System.out.println(f1.get());
    }

    public static final String format3 = "the currentThread name = %s is running, receive Value = %s, Throwable t = %s";

    @Test
    public void test3() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Supplier<Integer> supplier = () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            int randomSeconds = random.nextInt(5) + 1;
            try {
                TimeUnit.SECONDS.sleep(randomSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(randomSeconds > 3)
                throw new RuntimeException("测试异常!");
            return randomSeconds;
        };
        ThreadGroup g1 = new ThreadGroup("g1");
        Executor f1Executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE - 1, 1, TimeUnit.SECONDS, new SynchronousQueue<>(), new MyDefaultThreadFactory1(g1, "数字返回值-"));
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(supplier, f1Executor);
        ThreadGroup g2 = new ThreadGroup("g2");
        Executor f2Executor = new ThreadPoolExecutor(0, Integer.MAX_VALUE - 1, 1, TimeUnit.SECONDS, new SynchronousQueue<>(), new MyDefaultThreadFactory1(g2, "接收返回处理-"));
//        CompletableFuture<String> f2 = f1.handleAsync((v, t) -> {
//            System.out.println(String.format(format3, Thread.currentThread().getName(), v , t));
//            return v + "#" + UUID.randomUUID().toString();
//        }, f2Executor);

        // thenApply...与handle...类似,但无法捕捉异常处理
        CompletableFuture<String> f2 = f1.thenApplyAsync(v -> {
            System.out.println(String.format(format3, Thread.currentThread().getName(), v, null));
            return v + "#" + UUID.randomUUID().toString();
        });
        System.out.println(f2.get());
    }

    @Test
    public void test4() {
        Supplier<Integer> supplier1 = () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            int randomSecond = random.nextInt(9) + 1;
            try {
                TimeUnit.SECONDS.sleep(randomSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(randomSecond > 1) {
                throw new RuntimeException("模拟抛出异常!");
            }
            return randomSecond;
        };
        ThreadGroup supplierThreadGroup = new ThreadGroup("supplier");
        CompletableFuture<Integer> f1 = CompletableFuture.supplyAsync(supplier1, new ThreadPoolExecutor(0, Integer.MAX_VALUE - 1, 1, TimeUnit.SECONDS, new SynchronousQueue<>(), new MyDefaultThreadFactory1(supplierThreadGroup, "supplier-")));
        f1.whenComplete((v, t)-> {
            System.out.println("whenComplete: " + String.format(format3, Thread.currentThread().getName(), v ,t));
        });
        f1.exceptionally((t) -> {
            System.out.println("exceptionally:" + String.format(format3, Thread.currentThread().getName(), null ,t));
            return null;
        });
        while (supplierThreadGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * 合并结果集
     */
    @Test
    public void test5() throws ExecutionException, InterruptedException {
        Supplier<ThreadGroup> supplier1 = () -> {
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ThreadGroup rootThreadGroup = getRootThreadGroup(Thread.currentThread().getThreadGroup());
            return rootThreadGroup;
        };
        Supplier<StackTraceElement[]> supplier2 = () -> {
            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return new Exception().getStackTrace();
        };
        CompletableFuture<ThreadGroup> f1 = CompletableFuture.supplyAsync(supplier1);
        CompletableFuture<StackTraceElement[]> f2 = CompletableFuture.supplyAsync(supplier2);
        CompletableFuture<Pair<ThreadGroup, StackTraceElement[]>> f3 = f1.thenCombineAsync(f2, (g, s) -> {
            Pair<ThreadGroup, StackTraceElement[]> pair = new Pair<>(g, s);
            return pair;
        });
        Pair<ThreadGroup, StackTraceElement[]> pair = f3.get();
        System.out.println("*********threadGroup***********");
        pair.getKey().list();
        System.out.println("*********StackTraceElement[]***********");
        System.out.println(Arrays.toString(pair.getValue()));
    }

    @Test
    public void test6() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Supplier<String> supplier = () -> {
            int randomSeconds = random.nextInt(10);
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + "获取sleepSeconds = " + randomSeconds);
            try {
                TimeUnit.SECONDS.sleep(randomSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "sleepSeconds = " + randomSeconds;
        };
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<String> f3 = f1.applyToEither(f2, v -> v);
        System.out.println(f3.get());
    }

    @Test
    public void test7() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Supplier<String> supplier = () -> {
            int randomSeconds = random.nextInt(9) + 1;
            System.out.println(String.format(format1, Thread.currentThread().getName()) + "准备开始等待sleepSeconds = " + randomSeconds);
            try {
                TimeUnit.SECONDS.sleep(randomSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return UUID.randomUUID().toString();
        };
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<Void> f2 = f1.thenRunAsync(() -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
        });
        f2.get();
    }

    @Test
    public void test8() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Supplier<String> supplier = () -> {
            int sleepSecond = random.nextInt(10);
            System.out.println("the currentThread name = " + Thread.currentThread().getName() + "获取sleepSeconds = " + sleepSecond);
            try {
                TimeUnit.SECONDS.sleep(sleepSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "sleepSeconds = " + sleepSecond;
        };
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<Void> f3 = f1.runAfterBothAsync(f2, () -> {
            System.out.println("the currentThreadName = " + Thread.currentThread().getName() + "等待f1和f2都执行完毕!");
        });
        f3.get();
    }

    private static ThreadGroup getRootThreadGroup(ThreadGroup threadGroup) {
        if(threadGroup == null) {
            return null;
        }
        if(threadGroup.getParent() == null) {
            // 递归退出条件
            return threadGroup;
        }
        return getRootThreadGroup(threadGroup.getParent());
    }


}

class MyDefaultThreadFactory1 implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    MyDefaultThreadFactory1(ThreadGroup group, String prefix) {
        SecurityManager s = System.getSecurityManager();
        this.group = group;
        namePrefix = prefix +
                poolNumber.getAndIncrement() +
                "-thread-";
    }

    public Thread newThread(Runnable r) {
        Thread t = new Thread(group, r,
                namePrefix + threadNumber.getAndIncrement(),
                0);
        if (t.isDaemon())
            t.setDaemon(false);
        if (t.getPriority() != Thread.NORM_PRIORITY)
            t.setPriority(Thread.NORM_PRIORITY);
        return t;
    }
}
