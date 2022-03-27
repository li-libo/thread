package cn.com.thread.tb3;

import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-05 7:42 PM
 */
public class ThreadLocalTest {

    private static final ThreadLocal<Integer> idThreadLocal = ThreadLocal.withInitial(() -> 0);

    public static final String format1 = "the currentThread name = %s get() = %s";

    public static final String nameOfIdeaThreadInRunMode = "Monitor Ctrl-Break";

    public static int getNextId() {
        Integer newId = idThreadLocal.get() + 1;
        idThreadLocal.set(newId);
        return newId;
    }

    @Test
    public void test1() {
        int numOfTestThread = 3;
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(() -> {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(1);
                        System.out.println(String.format(format1, Thread.currentThread().getName(), getNextId()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }, "test-" + count).start();
        });

        int count = 1;
        while (Thread.activeCount() > count) {
            Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
            Set<String> threadNameSet = allStackTraces.keySet().stream().map(thread -> thread.getName()).collect(Collectors.toSet());
            if(threadNameSet.contains(nameOfIdeaThreadInRunMode)) {
                count = 2;
            }
            Thread.yield();
        }
    }

}
