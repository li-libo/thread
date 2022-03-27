package cn.com.thread.tb7;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;

/**
	Exchanger 是 JDK 1.5 开始提供的一个用于两个工作线程之间交换数据的封装工具类，简单说就是一个线程在完成一定的事务后想与另一个线程交换数据，
则第一个先拿出数据的线程会一直等待第二个线程，直到第二个线程拿着数据到来时才能彼此交换对应数据。其定义为 Exchanger<V> 泛型类型，其中 V 
表示可交换的数据类型，对外提供的接口很简单，具体如下：

    Exchanger()：无参构造方法。
    V exchange(V v)：等待另一个线程到达此交换点（除非当前线程被中断），然后将给定的对象传送给该线程，并接收该线程的对象。
    V exchange(V v, long timeout, TimeUnit unit)：等待另一个线程到达此交换点（除非当前线程被中断或超出了指定的等待时间），然后将给定的对象传送给该线程，
并接收该线程的对象。

	可以看出，当一个线程到达 exchange 调用点时，如果其他线程此前已经调用了此方法，则其他线程会被调度唤醒并与之进行对象交换，然后各自返回；
如果其他线程还没到达交换点，则当前线程会被挂起，直至其他线程到达才会完成交换并正常返回，或者当前线程被中断或超时返回。

 * @author lilibo
 *
 */
public class Test {
	
	static class Producer extends Thread {
		private Exchanger<Integer> exchanger;
		private static int data = 0;

		Producer(String name, Exchanger<Integer> exchanger) {
			super("Producer-" + name);
			this.exchanger = exchanger;
		}

		@Override
		public void run() {
			for (int i = 1; i < 100; i++) {
				try {
					TimeUnit.SECONDS.sleep(1);
					data = i;
					System.out.println(getName() + " 交换前:" + data);
					data = exchanger.exchange(data);
					System.out.println(getName() + " 交换后:" + data);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	static class Consumer extends Thread {
		private Exchanger<Integer> exchanger;
		private static int data = 0;

		Consumer(String name, Exchanger<Integer> exchanger) {
			super("Consumer-" + name);
			this.exchanger = exchanger;
		}

		@Override
		public void run() {
			while (true) {
				data = 0;
				System.out.println(getName() + " 交换前:" + data);
				try {
					TimeUnit.SECONDS.sleep(1);
					data = exchanger.exchange(data);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(getName() + " 交换后:" + data);
			}
		}
	}

	public static void main(String[] args) throws InterruptedException {
		Exchanger<Integer> exchanger = new Exchanger<Integer>();
		new Producer("", exchanger).start();
		new Consumer("", exchanger).start();
		TimeUnit.SECONDS.sleep(10);
		//System.exit(-1);
	}
}
