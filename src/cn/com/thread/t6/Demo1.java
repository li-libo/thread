package cn.com.thread.t6;

/**
 * 不同锁对象并不能起到同步作用
 * 重入锁
 * @author lilibo
 * @create 2022-01-01 6:13 PM
 */
public class Demo1 {

    public static final String format1 = "the currentThread name = %s 调用%s#%s方法";

    public synchronized void a() {
        System.out.println(String.format(format1, Thread.currentThread().getName(), this.getClass(), "a()"));
        b();
    }

    public synchronized void b() {
        System.out.println(String.format(format1, Thread.currentThread().getName(), this.getClass(), "b()"));
    }

    public static void main(String[] args) {
        Demo1 obj1 = new Demo1();
        Demo1 obj2 = new Demo1();
        new Thread(()->{
            obj1.a();
        }, "t1").start();
        new Thread(()->{
            obj2.b();
        }, "t2").start();
    }
}
