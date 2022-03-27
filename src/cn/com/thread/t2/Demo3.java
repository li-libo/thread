package cn.com.thread.t2;

/**
 * 创建线程方式-匿名内部类
 *
 * @author lilibo
 * @create 2021-12-31 3:52 PM
 */
public class Demo3 implements ConsoleColors {

    public static final String format1 = "the currentThread name = %s is running...";

    public static final String format2 = "Runnable run...,  the currentThread name = %s is running...";

    public static final String format3 = "Thread run...,  the currentThread name = %s is running...";

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println(String.format(format1, Thread.currentThread().getName()));
            }
        }, "匿名1").start();

        new Thread(() -> {
            System.out.println(GREEN + String.format(format1, Thread.currentThread().getName() + RESET));
        }, "匿名2").start();

        // 猜猜会输出什么?
        Runnable runn1 = () -> {
            System.out.println(RED + String.format(format2, Thread.currentThread().getName()) + RESET);
        };
        Thread t3 = new Thread(runn1, "匿名3") {
            @Override
            public void run() {
                System.out.println(BLUE + String.format(format3, Thread.currentThread().getName() + RESET));
            }
        };
        t3.start();
//
//        System.out.println("--------------");
//        runn1.run();
//        t3.run();
    }
}
