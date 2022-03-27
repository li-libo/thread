package cn.com.thread.t5;

public class Singleton {
	
	// 私有化构造方法
	private Singleton () {}

	private static Singleton instance = new Singleton();
	
	public static Singleton getInstance() {
		return instance;
	}
	
	// 多线程环境下
	// 必须有共有资源
	// 对资源进行非原子性操作
	
	
}
