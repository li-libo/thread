package cn.com.thread.t5;

/**
 * 单例模式-懒汉模式(双重检查锁)
 * @author lilibo
 * @create 2022-01-01 5:44 PM
 */
public class Singleton3 {

    // 使用volatile防止指令重排序
    private static volatile Singleton3 singleton3;

    private Singleton3() {
        System.out.println("实例化" + this.getClass());
    }

    public static Singleton3 getInstance() {
        if(singleton3 == null) {
            synchronized (Singleton3.class) {
                if(singleton3 == null) {
                    // 指令重排序问题, 也有可能是1->3->2
                    /*
                        实例化对象
                        1. 申请1块内存空间
                        2. 在这块空间上实例化对象
                        3. 实例引用指向这块内存空间地址
                     */
                    singleton3 = new Singleton3();
                }
            }
        }
        return singleton3;
    }
}
