package cn.com.thread.t5;

/**
 * 单例示例-懒汉模式
 */
public class Singleton2 {
	
	private Singleton2() {}
	
	// 必须声明 volatile, 禁止指令重新排序, 否则可能会拿到没有实例化完毕的对象
	private static volatile Singleton2 instance;
	
	/**
	 * @return
	 */
	public static Singleton2 getInstance () {
		// 自旋 while(true)
		if(instance == null) {
			/*
			 * synchronized没有进行指令重排序原因:
			 * 		而synchronized的作用是加锁，可以保证串行执行，即可以让并发环境转为单线程环境。
			 * 因此加了synchronized就已经是单线程环境了。既然是单线程，那么无论是否进行了重排序，
			 * 最终的结果都不会有影响，即都可以保证线程安全。所以说，在使用synchronized时根本不用关心
			 * “重排序”这个问题，无论它支持或不支持，都已经不重要了。
			 */
			synchronized (Singleton2.class) { //  synchronized 块里的非原子操作依旧可能发生指令重排
				if(instance == null) {
					instance = new Singleton2();  // 指令重排序,也有可能是1-3-2
					// 申请一块内存空间   // 1
					// 在这块空间里实例化对象  // 2
					// instance引用指向这块空间地址  // 3
				}
			}
		}
		return instance;
	}

}
