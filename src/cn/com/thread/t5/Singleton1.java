package cn.com.thread.t5;

/**
 * 单例示例, 饿汉模式
 * @author lilibo
 * @create 2022-01-01 4:51 PM
 */
public class Singleton1 {

    private static final Singleton1 singleton1 = new Singleton1();

    private Singleton1() {
        System.out.println("实例化" + this.getClass());
    }

    public static Singleton1 getInstance() {
        return singleton1;
    }
}
