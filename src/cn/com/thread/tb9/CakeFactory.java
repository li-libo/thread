package cn.com.thread.tb9;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CakeFactory {

	private final Random random = new Random();
	
	public CakeFuture<Cake> orderCake(String name) {
		double price = 60 + random.nextInt(40);
		CakeFuture<Cake> cakeFuture = new CakeFuture<>();
		new Thread(()->{			
			try {
				TimeUnit.SECONDS.sleep(random.nextInt(10));
				Cake cake = new Cake(name, price);
				cakeFuture.setCake(cake);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}, "makeCake").start();;
		return cakeFuture;		
	}

}
