package cn.com.thread.tc9;

import java.util.concurrent.atomic.AtomicInteger;

public class ShopDemo {
	
	public static final int numOfPutThread = 3;
	
	public static final int numOfTakeThread = 5;

	private static final AtomicInteger atomicInteger = new AtomicInteger();
	
	public static void main(String[] args) {
		Shop<Commodity> shop = new Tmall<>(10);
		for(int i = 0; i < numOfPutThread; i++) {
			final int j = i;
			new Thread(()->{
				while(true) {
					try {
						Commodity commodity = new Commodity(atomicInteger.incrementAndGet());
						shop.put(commodity);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, "putThread-" + j).start();
		}
		
		for(int i = 0; i < numOfTakeThread; i++) {
			final int j = i;
			new Thread(()->{
				while(true) {
					try {
						shop.take();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, "takeThread-" + j).start();
		}
		
		new Thread(()->{
			while(true) {
				try {
					System.out.println("shop size = " + shop.size());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, "sizeThread").start();
	}

}
