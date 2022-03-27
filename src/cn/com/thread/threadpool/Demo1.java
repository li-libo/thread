package cn.com.thread.threadpool;

import org.junit.Test;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-17 10:33 AM
 */
public class Demo1 {

    private static final AtomicInteger threadIdCount = new AtomicInteger();

    public static final String format1 = "the currentThread name = %s is running!";

    private static final AtomicInteger taskIdCount = new AtomicInteger();

    /**
     * java.util.concurrent.SynchronousQueue队列
     */
    @Test
    public void test1() {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(3, Integer.MAX_VALUE, 1, TimeUnit.SECONDS, new SynchronousQueue<>(), (r) -> {
            return new Thread(r, "test-" + threadIdCount.incrementAndGet());
        }, (r, executor) -> {
            System.out.println("任务r = " + r + "无法处理!");
        });
        poolExecutor.prestartAllCoreThreads();
        poolExecutor.allowCoreThreadTimeOut(true);
        int numOfTestThread = 16;
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            poolExecutor.execute(() -> {
                System.out.println(String.format(format1, Thread.currentThread().getName()));
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
        while (poolExecutor.getPoolSize() > 0) {
            Thread.yield();
        }
        System.out.println("关闭线程池...");
        poolExecutor.shutdown();
    }

    /**
     * java.util.concurrent.PriorityBlockingQueue 队列
     */
    @Test
    public void test2() {
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(0, 1, 1, TimeUnit.SECONDS, new PriorityBlockingQueue<>());
        Runnable runnable = () -> System.out.println(String.format(format1, Thread.currentThread().getName()));
        poolExecutor.allowCoreThreadTimeOut(true);
        int numOfTest = 10;
        Stream.iterate(0, count -> count + 1).limit(numOfTest).forEach(count -> {
            poolExecutor.execute(new Task(runnable, taskIdCount.incrementAndGet()));
        });
        while (poolExecutor.getPoolSize() > 0) {
            Thread.yield();
        }
        poolExecutor.shutdown();
    }

    /**
     * 自定义饱和策略
     */
    @Test
    public void test3() {
        ThreadGroup poolGroup = new ThreadGroup("poolGroup");
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(2, 5, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10), (r) -> {
            return new Thread(poolGroup, r, "test-pool-" + threadIdCount.incrementAndGet());
        }, (r, e) -> {
            System.out.println("无法处理runnable = " + r);
        });
        poolExecutor.allowCoreThreadTimeOut(true);
        int numOfTestThread = 20;
        Runnable runnable = () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            poolExecutor.execute(runnable);
        });
        System.out.println("pid = " + getPid());
        while (poolExecutor.getPoolSize() > 0) {
            Thread.yield();
        }
        poolExecutor.shutdown();
        System.out.println("poolExecutor.isShutdown() = " + poolExecutor.isShutdown());
        System.out.println("poolExecutor.isTerminated() = " + poolExecutor.isTerminated());
    }

    public static final String format2 = "beforeExecute: the currentThreadName = %s准备执行runnable = %s";

    public static final String format3 = "afterExecute: 执行runnable = %s完毕!";


    /**
     * 扩展线程池
     */
    @Test
    public void test4() {
        ThreadGroup poolGroup = new ThreadGroup("poolGroup");
        AtomicInteger threadIdCount = new AtomicInteger();
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 5, 1,TimeUnit.SECONDS, new LinkedBlockingDeque<>(), (r) -> {
            return new Thread(poolGroup, r, "pool-" + threadIdCount.incrementAndGet());
        }) {
            @Override
            protected void beforeExecute(Thread t, Runnable r) {
                System.out.println(String.format(format2, Thread.currentThread().getName(), r));
                super.beforeExecute(t, r);
            }

            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                System.out.println(String.format(format3, r));
                super.afterExecute(r, t);
            }

            @Override
            protected void terminated() {
                System.out.println("线程池终止!");
                super.terminated();
            }
        };
        poolExecutor.allowCoreThreadTimeOut(true);
        int numOfTestThread = 20;
        Runnable runnable = () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            poolExecutor.execute(runnable);
        });
        System.out.println("pid = " + getPid());
        while (poolExecutor.getPoolSize() > 0) {
            Thread.yield();
        }
        poolExecutor.shutdown();
        System.out.println("poolExecutor.isShutdown() = " + poolExecutor.isShutdown());
        System.out.println("poolExecutor.isTerminated() = " + poolExecutor.isTerminated());
    }

    public static String getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    /**
     * 使用ExecutorCompletionService取出最先完成的任务
     */
    @Test
    public void test5() throws ExecutionException, InterruptedException {
        ThreadGroup poolGroup = new ThreadGroup("poolGroup");
        AtomicInteger threadIdCount = new AtomicInteger();
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(5, 5, 1, TimeUnit.SECONDS, new LinkedBlockingDeque<>(), (r) -> {
            return new Thread(poolGroup, r, "pool-" + threadIdCount.incrementAndGet());
        });
        PurchaseGoods purchaseGoods = new PurchaseGoods();
        Instant start1 = Instant.now();
        List<Future<String>> future1List = new ArrayList<>();
        Stream.iterate(5, count -> count - 1).limit(2).forEach(count -> {
            String name = "商品-" + count;
            int waitSeconds = count;
            if(count < 5) {
                waitSeconds = 1;
            }
            final int w = waitSeconds;
            Future<String> f1 = poolExecutor.submit(() -> purchaseGoods.buy(name, w), name);
            future1List.add(f1);
        });
        for(Future<String> f1 : future1List) {
            String name = f1.get();
            System.out.println("******购买name = " + name + "完毕*****");
            purchaseGoods.transport(name);
        }
        Instant end1 = Instant.now();
        System.out.println("首次模拟案例耗时为: " + Duration.between(start1, end1).toMillis() + "ms");

        Instant start2 = Instant.now();
        CompletionService completionService = new ExecutorCompletionService(poolExecutor);
        future1List.clear();
        Stream.iterate(5, count -> count - 1).limit(2).forEach(count -> {
            String name = "商品-" + count;
            int waitSeconds = count;
            if(count < 5) {
                waitSeconds = 1;
            }
            final int w = waitSeconds;
            completionService.submit(() -> purchaseGoods.buy(name, w), name);
        });
        for(int i = 0; i < 2; i ++) {
            Future<String> f1 = completionService.take();
            String name = f1.get();
            System.out.println("******购买name = " + name + "完毕*****");
            purchaseGoods.transport(name);
        }
        Instant end2 = Instant.now();
        System.out.println("2次模拟案例优化后耗时为: " + Duration.between(start2, end2).toMillis() + "ms");
    }

    /**
     * 使用CompletionService执行一批任务,然后消费执行结果
     */
    @Test
    public void test6() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        int numOfTestThread = 5;
        List<Callable<Integer>> callableList = new ArrayList<>();
        Stream.iterate(5, count -> count - 1).limit(5).forEach(count -> {
            callableList.add(() -> {
               TimeUnit.SECONDS.sleep(count);
               return count;
            });
        });

        consumerTasks(executorService, callableList, (a)-> {
            System.out.println("所有任务执行完毕!" + a);
        });
    }

    private void consumerTasks(ExecutorService executorService, List<Callable<Integer>> callableList, Consumer<String> consumer) throws InterruptedException, ExecutionException {
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executorService);
        callableList.stream().forEach(callable -> {
            completionService.submit(callable);
        });
        int numOfTask = callableList.size();
        for(int i = 0; i < numOfTask; i++) {
            Integer integer = completionService.take().get();
            System.out.println("任务-" + integer + "执行完毕!");
        }
        consumer.accept(LocalDateTime.now().toString());
    }

    /**
     * 异步执行一批任务,有1个完成立即返回,其他取消
     * 方式1
     */
    @Test
    public void test7() throws ExecutionException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<String>> callableList = new ArrayList<>();
        Stream.iterate(5, count -> count - 1).limit(5).forEach(count -> {
                callableList.add(() -> {
                    TimeUnit.SECONDS.sleep(count);
                    return "耗时:" + count + "秒";
                });
            }
        );
        invokeAny(executorService, callableList);
        executorService.shutdown();
    }

    private void invokeAny(ExecutorService executorService, List<Callable<String>> callableList) throws InterruptedException, ExecutionException {
        CompletionService<String> completionService = new ExecutorCompletionService<>(executorService);
        List<Future<String>> futureList = new ArrayList<>();
        callableList.stream().forEach(task -> {
            Future<String> future = completionService.submit(task);
            futureList.add(future);
        });
        int size = callableList.size();
        String result = null;
        for(int i = 0; i < size; i++) {
            result = completionService.take().get();
            if(result!=null) {
                try{
                    break;
                }finally {
                    futureList.stream().forEach(f -> f.cancel(true));
                }
            }
        }
        System.out.println("最先执行完的结果为: " + result);
    }

    /**
     * 异步执行一批任务,有1个完成立即返回,其他取消
     * 方式2
     * 利用java.util.concurrent.ExecutorService#invokeAny(java.util.Collection)
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void test8() throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<String>> callableList = new ArrayList<>();
        Stream.iterate(5, count -> count - 1).limit(5).forEach(count -> {
            callableList.add(() -> {
               TimeUnit.SECONDS.sleep(count);
               return "耗时:" + count + "秒";
            });
        });
        String result = executorService.invokeAny(callableList);
        System.out.println("最先执行完 " + result);
    }

}

class PurchaseGoods {

    public static final String format1 = "the currentThread name = %s 购买%s, localDateTime = %s";

    public static final String format2 = "the currentThread name = %s 购买%s完毕!, localDateTime = %s";

    public static final String format3 = "the currentThread name = %s 开始运输%s, localDateTime = %s";

    public static final String format4 = "the currentThread name = %s 运输%s完毕!, localDateTime = %s";

    public void buy(String name, int waitSeconds){
        System.out.println(String.format(format1, Thread.currentThread().getName(), name, LocalDateTime.now()));
        try {
            TimeUnit.SECONDS.sleep(waitSeconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format(format2, Thread.currentThread().getName(), name, LocalDateTime.now()));
    }

    public void transport(String name){
        System.out.println(String.format(format3, Thread.currentThread().getName(), name, LocalDateTime.now()));
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(String.format(format4, Thread.currentThread().getName(), name, LocalDateTime.now()));
    }

}

class Task implements Runnable, Comparable<Task> {

    private Runnable runnable;

    private Integer id;

    public Task(Runnable runnable, Integer id) {
        this.runnable = runnable;
        this.id = id;
    }

    @Override
    public void run() {
        runnable.run();
        System.out.println("task.id = " + id);
    }

    @Override
    public int compareTo(Task o) {
        return Integer.compare(o.id, this.id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "runnable=" + runnable +
                ", id=" + id +
                '}';
    }
}