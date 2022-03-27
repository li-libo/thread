package cn.com.thread.t7;

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

	public volatile boolean run = false; // 加上volatile保证可见性

	public static void main(String[] args) {

		Demo2 d = new Demo2();

		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 1; i <= 10; i++) {
					System.err.println("执行了第 " + i + " 次");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				d.run = true;
			}
		}).start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!d.run) {
					// 不执行
				}
				System.err.println("线程2执行了...");
			}
		}).start();

	}

}
