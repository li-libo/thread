package cn.com.thread.threadgroup;

import org.junit.Test;


import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-11 5:33 PM
 */
public class ThreadGroupTest {

    public static final String format1 = "the currentThread name = %s, the threadGroup = %s";

    public static final String format2 = "*********the threadGroup = %s, activeCount = %s, activeGroup = %s";

    private Runnable runnable = () -> {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
                Thread currentThread = Thread.currentThread();
                ThreadGroup currentThreadGroup = currentThread.getThreadGroup();
                // System.out.println(String.format(format1, currentThread.getName(), currentThreadGroup));
                System.out.println("&&&&&&&&start print group...");
                while (currentThreadGroup != null) {
                    System.out.println(String.format(format2, currentThreadGroup, currentThreadGroup.activeCount(), currentThreadGroup.activeGroupCount()));
                    currentThreadGroup.list();
                    currentThreadGroup = currentThreadGroup.getParent();
                }
                System.out.println("&&&&&&&&end print group...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Test
    public void test1() {
        ThreadGroup threadGroup = new ThreadGroup("g1");
        int numOfTestThread = 1;
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(threadGroup, runnable, "test-" + count).start();
        });
        while (threadGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    @Test
    public void test2() {
        ThreadGroup parentThreadGroup = new ThreadGroup("parent");
        int numOfParentTestThread = 2;
        Stream.iterate(0, count -> count + 1).limit(numOfParentTestThread).forEach(count -> {
            new Thread(parentThreadGroup, runnable, "parent-" + count).start();
        });
        ThreadGroup sonThreadGroup = new ThreadGroup(parentThreadGroup, "son");
        int numOfSonTestThread = 3;
        Stream.iterate(0, count -> count + 1).limit(numOfSonTestThread).forEach(count -> {
            new Thread(sonThreadGroup, runnable, "son-" + count).start();
        });
        while (parentThreadGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    public static final String format3 = "group name = %s, parent group name = %s, activeCount = %s, activeGroupCount = %s";

    @Test
    public void test3() throws InterruptedException {
        ThreadGroup p1Group = new ThreadGroup("p1");
        ThreadGroup s1Group = new ThreadGroup(p1Group, "s1");
        new Thread(p1Group, () -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t1").start();
        new Thread(s1Group, ()-> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "t2").start();

        while (true) {
            TimeUnit.SECONDS.sleep(1);
            System.out.println(String.format(format3, p1Group.getName(), p1Group.getParent() == null ? null : p1Group.getParent().getName(), p1Group.activeCount(), p1Group.activeGroupCount()));
            System.out.println(String.format(format3, s1Group.getName(), s1Group.getParent() == null ? null : s1Group.getParent().getName(), s1Group.activeCount(), s1Group.activeGroupCount()));
            ThreadGroup rootThreadGroup = getRootThreadGroup(s1Group);
            System.out.println(String.format(format3, rootThreadGroup.getName(), rootThreadGroup.getParent() == null ? null : rootThreadGroup.getParent().getName(), rootThreadGroup.activeCount(), rootThreadGroup.activeGroupCount()));
            rootThreadGroup.list();
        }
    }

    /**
     * 递归获取根线程组
     * @param currentThreadGroup
     * @return
     */
    public static ThreadGroup getRootThreadGroup(ThreadGroup currentThreadGroup) {
        if(currentThreadGroup == null) {
            return null;
        }
        if(currentThreadGroup.getParent() == null) { // 递归正常退出条件
            return currentThreadGroup;
        }else {
            return getRootThreadGroup(currentThreadGroup.getParent());
        }
    }

    /**
     * 遍历获取根线程组
     * @param currentThreadGroup
     * @return
     */
    public static ThreadGroup getRootThreadGroup1(ThreadGroup currentThreadGroup) {
        if(currentThreadGroup == null) {
            return currentThreadGroup;
        }
        while (currentThreadGroup.getParent() != null) {
            currentThreadGroup = currentThreadGroup.getParent();
        }
        return currentThreadGroup;
    }

    public static final String format5 = "the currentThread name = %s is running!";

    public static final String format6 = "the currentThread name = %s is stopping!";

    /**
     * 利用线程组批量停止线程
     */
    @Test
    public void test4() throws InterruptedException {
        Runnable runn = () -> {
            System.out.println(String.format(format5, Thread.currentThread().getName()));
            while (!Thread.currentThread().isInterrupted()) {
            }
            System.out.println(String.format(format6, Thread.currentThread().getName()));
        };

        ThreadGroup testThreadGroup = new ThreadGroup("testGroup");
        int numOfTestThread = 4;
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(testThreadGroup, runn, "test-" + count).start();
        });
        TimeUnit.SECONDS.sleep(3);
        testThreadGroup.interrupt();
        while (testThreadGroup.activeCount() > 0) {
            Thread.yield();
        }
    }
}
