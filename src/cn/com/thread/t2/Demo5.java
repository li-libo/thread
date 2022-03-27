package cn.com.thread.t2;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 使用定时器创建线程和ScheduleThreadPoolExecutor对比, 异常处理对比
 * @author lilibo
 * @create 2021-12-31 6:37 PM
 */
public class Demo5 {

    public static final AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {
        // Timer测试
        try{
            Timer timer = new Timer("timer");
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Timer执行, the currentThread name = " + Thread.currentThread().getName());
                }
            }, 3000, 1000);
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    throw new RuntimeException("模拟Timer异常");
                }
            }, 5 * 1000);
        }catch (Exception e) {
            e.printStackTrace();
        }
        // ScheduleThreadPoolExecutor测试
        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(0, (r) -> {
            SecurityManager s = System.getSecurityManager();
            ThreadGroup group = (s != null) ? s.getThreadGroup() :
                    Thread.currentThread().getThreadGroup();
            Thread t = new Thread(group, r,
                    "定时调度ScheduledThreadPoolExecutor-" + atomicInteger.getAndIncrement(),
                    0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        });
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("定时调度ScheduledThreadPoolExecutor, the currentThreadName = " + Thread.currentThread().getName());
        }, 1, 1, TimeUnit.SECONDS);
        scheduledExecutorService.schedule(()->{
            throw new RuntimeException("模拟ScheduledThreadPoolExecutor异常");
        }, 5, TimeUnit.SECONDS);
    }

}
