package cn.com.thread.completablefuture;

import org.junit.Test;

import java.sql.Time;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * @author lilibo
 * @create 2022-01-12 6:55 PM
 */
public class CompletableFutureTest {

    @Test
    public void test1() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(1, new MyDefaultThreadFactory("无返回值", null));
        CompletableFuture<Void> f1 = CompletableFuture.runAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println("the currentThreadName = " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, executorService);
        System.out.println(f1.get());
        ExecutorService executorService1 = Executors.newFixedThreadPool(1, new MyDefaultThreadFactory("有返回值", null));
        CompletableFuture<Long> f2 = CompletableFuture.supplyAsync(()-> {
            try {
                TimeUnit.SECONDS.sleep(3);
                System.out.println("the currentThreadName = " + Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return System.currentTimeMillis();
        }, executorService1);
        System.out.println(f2.get());
    }

    /**
     * 完成时回调和异常回调
     */
    @Test
    public void test2() {
        ThreadGroup threadGroup = new ThreadGroup("测试2");
        CompletableFuture<Void> future = CompletableFuture.runAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
        }, new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                1L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new MyDefaultThreadFactory("测试2", threadGroup)));
        future.whenComplete((v, t)->{
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            System.out.println("执行完成, 返回值 = " + v + ", Throwable t = " + t);
        });
        future.exceptionally(v ->{
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            System.out.println("执行失败, e.getMessage = " + v.getMessage());
            return null;
        });
        while (threadGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * java.util.concurrent.CompletableFuture#thenApply(java.util.function.Function)
     * 当1个线程依赖另1个线程时,可以使用thenApply来把这两个线程串行化
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test3() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> f1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return System.currentTimeMillis();
        });
        CompletableFuture<String> f2 = f1.thenApply(t -> {
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            return "测试返回值" + t;
        });
        System.out.println("f2 = " + f2.get());
    }

    /**
     * java.util.concurrent.CompletableFuture#handle(java.util.function.BiFunction)
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test4() throws ExecutionException, InterruptedException {
        CompletableFuture<Long> f1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return System.currentTimeMillis();
        });
        CompletableFuture<String> f2 = f1.handle((v, t) -> {
            if(t == null) {
                t = new Exception("你好啊");
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            return "测试返回值" + v + ", t = " + t;
        });
        System.out.println("f2 = " + f2.get());
    }

    /**
     * java.util.concurrent.CompletableFuture#thenAccept(java.util.function.Consumer)
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test5() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(()-> {
            try {
                TimeUnit.SECONDS.sleep(3);
                return UUID.randomUUID().toString();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });
        CompletableFuture<Void> f2 = f1.thenAccept(v -> {
            System.out.println("id = " + v);
        });
        System.out.println("f2.get() = " + f2.get());
    }

    /**
     * 根thenAccept方法不同的是, thenRun不关心任务的处理结果,只要上面的任务执行完成,就开始执行thenRun
     */
    @Test
    public void test6() throws ExecutionException, InterruptedException {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(()->{
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return UUID.randomUUID().toString();
        });
        f1.thenRunAsync(() -> {
            ThreadGroup rootThreadGroup = getRootThreadGroup(Thread.currentThread().getThreadGroup());
            rootThreadGroup.list();
        });
        f1.get();
    }

    /**
     * java.util.concurrent.CompletableFuture#thenCombineAsync(java.util.concurrent.CompletionStage, java.util.function.BiFunction)合并结果集
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test7() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Supplier<String> supplier = () -> {
            try {
                TimeUnit.SECONDS.sleep(random.nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            return UUID.randomUUID().toString();
        };

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<String> f3 = f1.thenCombineAsync(f2, (v1, v2) -> {
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            return v1 + "#" + v2;
        });
        System.out.println("f3.get() = " + f3.get());
    }

    @Test
    public void test8() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Supplier<String> supplier = () -> {
            try {
                TimeUnit.SECONDS.sleep(random.nextInt(5));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("the currentThread name = " + Thread.currentThread().getName());
            return UUID.randomUUID().toString();
        };

        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<Void> f3 = f1.thenAcceptBoth(f2, (v1, v2) -> {
            System.out.println(v1 + "#" + v2);
        });
        f3.get();
    }

    /**
     * applyToEither执行返回较快的那个CompletableStage
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void test9() throws ExecutionException, InterruptedException {
        Random random = new Random();
        Supplier<String> supplier = () -> {
            int randomSeconds = 1 + random.nextInt(4);
            System.out.println("randomSeconds = " + randomSeconds);
            try {
                TimeUnit.SECONDS.sleep(randomSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "sleepSecond = " + randomSeconds;
        };
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(supplier);
        CompletableFuture<Void> f3 = f1.applyToEither(f2, fasterV -> {
            return fasterV;
        }).thenAccept((fasterV) -> {
            System.out.println(fasterV);
        });
        System.out.println(f3.get());
    }

    private ThreadGroup getRootThreadGroup(ThreadGroup threadGroup) {
        if(threadGroup == null) {
            return null;
        }
        if(threadGroup.getParent() == null) {
            return threadGroup;
        }else {
            return getRootThreadGroup(threadGroup.getParent());
        }
    }

}

class MyDefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    MyDefaultThreadFactory(String prefix, ThreadGroup group) {
        SecurityManager s = System.getSecurityManager();
        this.group = group == null ? Thread.currentThread().getThreadGroup() : group;
        namePrefix = prefix + "-" +
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