package cn.com.thread.t7;

import java.util.concurrent.TimeUnit;

/**
volatile示例: 
	volatile称之为轻量级锁，被volatile修饰的变量，在线程之间是可见的。
	可见：一个线程修改了这个变量的值，在另外一个线程中能够读到这个修改后的值。
	synchronized除了线程之间互斥意外，还有一个非常大的作用，就是保证可见性

Lock指令:
	在多处理器的系统上
	将当前处理器缓存行的内容写回到系统内存
	这个写回到内存的操作会使在其他CPU里缓存了该内存地址的数据失效
	硬盘  -- 内存  -- CPU的缓存

 * @author lilibo
 *
 */
public class Demo2 {

	// private static boolean blockFlag = true;

	// 加入volatile使多个线程能及时看到最新值, 保证可见性
	private static volatile boolean blockFlag = true;

	public static void main(String[] args) {
		new Thread(()-> {
			try {
				System.out.println("the currentThread name = " + Thread.currentThread().getName() + " is running!");
				TimeUnit.SECONDS.sleep(1);
				System.out.println("the currentThread name = " + Thread.currentThread().getName() + " set blockFlag = false!");
				blockFlag = false;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "t1").start();

		new Thread(() -> {
			while(blockFlag) {
				//System.out.println("blockFlag = " + blockFlag);
			}
			System.out.println("the currentThread name = " + Thread.currentThread().getName() + "恢复执行了!");
		}, "t2").start();
	}
}
