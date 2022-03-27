package cn.com.thread.unsafe;

import org.junit.BeforeClass;
import org.junit.Test;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * @author lilibo
 * @create 2022-01-14 5:21 PM
 */
public class UnsafeTest {

    private static Unsafe unsafe;

    private int count;

    private static long count1;

    private final Object lockObject = new Object();

    @BeforeClass
    public static void beforeClass() throws NoSuchFieldException, IllegalAccessException {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        unsafe = (Unsafe) unsafeField.get(null);
    }

    /**
     * sun.misc.Unsafe#getAndAddInt(java.lang.Object, long, int)测试 - 非静态实例变量
     *
     * @throws NoSuchFieldException
     */
    @Test
    public void test1() throws NoSuchFieldException {
        long offsetOfCount = unsafe.objectFieldOffset(this.getClass().getDeclaredField("count"));
        int numOfTestThread = 5;
        int loop = 1000;
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        Runnable runnable = () -> {
            Stream.iterate(0, count -> count + 1).limit(loop).forEach(count -> {
                unsafe.getAndAddInt(this, offsetOfCount, 1);
            });
        };
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(testGroup, runnable, "test-" + count).start();
        });
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
        System.out.println("count = " + count);
    }

    @Test
    public void test2() throws NoSuchFieldException {
        long offsetOfCount1 = unsafe.staticFieldOffset(UnsafeTest.class.getDeclaredField("count1"));
        int numOfTestThread = Runtime.getRuntime().availableProcessors() - 1;
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        int loop = 1000;
        Runnable runnable = () -> {
            Stream.iterate(0, count -> count + 1).limit(loop).forEach(count -> {
                unsafe.getAndAddLong(UnsafeTest.class, offsetOfCount1, 2);
            });
        };
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(testGroup, runnable, "test-" + count).start();
        });
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
        System.out.println("count1 = " + count1);
    }

    public static final String format1 = "the currentThread name = %s is starting!";

    public static final String format2 = "the currentThread name = %s is ending!";

    /**
     * park并不释放锁
     */
    @Test
    public void test3() {
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        Lock lock = new ReentrantLock();
        Thread t1 = new Thread(testGroup, () -> {
            try {
                lock.lock();
                System.out.println(String.format(format1, Thread.currentThread().getName()));
                unsafe.park(false, 0);
                System.out.println(String.format(format2, Thread.currentThread().getName()));
            } finally {
                lock.unlock();
            }
        }, "t1");
        t1.start();
        new Thread(testGroup, () -> {
            try {
                TimeUnit.SECONDS.sleep(3);
                unsafe.unpark(t1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, "unpark").start();

        new Thread(testGroup, () -> {
            System.out.println(String.format(format1, Thread.currentThread().getName()));
            unsafe.park(false, TimeUnit.SECONDS.toNanos(3));
            System.out.println(String.format(format2, Thread.currentThread().getName()));
        }, "t3").start();
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
    }

    /**
     * UnSafe重入锁
     */
    @Test
    public void test4() {
        int numOfTestThread = 5;
        int numOfLoop = 1000;
        Set<String> countSet = Collections.synchronizedSet(new HashSet<>());
        Runnable runnable = () -> {
            Stream.iterate(0, count -> count + 1).limit(numOfLoop).forEach(count -> {
                try {
                    unsafe.monitorEnter(lockObject);
                    this.count++;
                    String value = this.count + "";
                    if(!countSet.add(value)) {
                        throw new RuntimeException("出现重复数据! value = " + value);
                    }
                }finally {
                    unsafe.monitorExit(lockObject);
                }
            });
        };
        ThreadGroup testGroup = new ThreadGroup("testGroup");
        Stream.iterate(0, count -> count + 1).limit(numOfTestThread).forEach(count -> {
            new Thread(testGroup, runnable, "test-" + count).start();
        });
        while (testGroup.activeCount() > 0) {
            Thread.yield();
        }
        System.out.println("count = " + count);
    }

    /**
     * 偏移量
     */
    @Test
    public void test5() throws NoSuchFieldException {
        Field countField = UnsafeTest.class.getDeclaredField("count");
        Field count1Field = UnsafeTest.class.getDeclaredField("count1");
        System.out.println("offset of count = " + unsafe.objectFieldOffset(countField));
        System.out.println("offset of count1 = " + unsafe.staticFieldOffset(count1Field));
        System.out.println("base class of count1 field = " + unsafe.staticFieldBase(count1Field));
    }

    /**
     * 判断是否需要初始化和初始化
     */
    @Test
    public void test6() {
        if(unsafe.shouldBeInitialized(C1.class)) {
            System.out.println("C1 class需要初始化!");
            unsafe.ensureClassInitialized(C1.class);
        }
        // 调用C2.count无形中将C2初始化了
        System.out.println(C2.count);
        System.out.println(unsafe.shouldBeInitialized(C2.class));
    }

    /**
     * 绕过构造方法创建对象
     * @throws InstantiationException
     */
    @Test
    public void test7() throws InstantiationException {
        C3 o = (C3) unsafe.allocateInstance(C3.class);
        o.setName("小野胖夫🐷君");
        System.out.println(o);
    }

    /**
     * 数组相关的一些方法
     * @see sun.misc.Unsafe#arrayBaseOffset(java.lang.Class)
     * @see Unsafe#arrayIndexScale(java.lang.Class)
     */
    @Test
    public void test8() {
        int[] array = new int[5];
        int arrayBaseOffset = unsafe.arrayBaseOffset(int[].class);
        int arrayIndexScale = unsafe.arrayIndexScale(int[].class);
        int shift = 31 - Integer.numberOfLeadingZeros(arrayIndexScale);
        long offset = ((long) 3 << shift) + arrayBaseOffset;
        System.out.println("arrayBaseOffSet = " + arrayBaseOffset + ", shift = " + shift + ", offset = " + offset);
        unsafe.getAndAddInt(array, offset, 33);
        System.out.println("array = " + Arrays.toString(array));
    }

    static class C1 {
        private static int count;

        static {
            count = 10;
            System.out.println("C1 static field count init!");
        }
    }

    static class C2 {
        private static int count;

        static {
            count = 11;
            System.out.println("C2 static field count init!");
        }
    }

}

class C3 {

    private String name;

    private C3() {
        System.out.println("调用" + this.getClass() + "无参构造器!");
    }

    private C3(String name) {
        System.out.println("调用" + this.getClass() + "有参构造器!");
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "C3{" +
                "name='" + name + '\'' +
                '}';
    }
}