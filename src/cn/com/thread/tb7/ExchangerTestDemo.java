package cn.com.thread.tb7;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ExchangerTestDemo {

	public void exchangeData(Exchanger<String> exchanger, String initialData, int sleepSeconds) throws InterruptedException {
		System.out.println("the current thread name = " + Thread.currentThread().getName() + "开始交换数据, initialData = " + initialData);
		TimeUnit.SECONDS.sleep(sleepSeconds);
		String exchangedData = exchanger.exchange(initialData);
		System.out.println("the current thread name = " + Thread.currentThread().getName() + "交换数据完毕!!!, exchangedData = " + exchangedData);
	}
	
	public static void main(String[] args) {
		ExchangerTestDemo exchangerTestDemo = new ExchangerTestDemo();
		Exchanger<String> exchanger = new Exchanger<>();
		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
		for(int i = 0; i < 4; i++) {
			final int j = i;
			new Thread(()->{
				try {
					exchangerTestDemo.exchangeData(exchanger, j + "", threadLocalRandom.nextInt(10));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}, "exchange-" + j).start();
		}
	}

}
