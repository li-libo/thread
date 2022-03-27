package cn.com.thread.tb9;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Future原理示例, Future类似于一个"订单"
 * @author lilibo
 *
 */
public class MyFutureDemo {

	public static void main(String[] args) throws InterruptedException {
		CakeFactory cakeFactory = new CakeFactory();
		Set<CakeFuture<Cake>> cakeFutureSet = Collections.synchronizedSet(new HashSet<>());
		for(int i = 0; i< 5; i++) {
			CakeFuture<Cake> cakeFuture = cakeFactory.orderCake("巧克力黑森林-" + i);
			cakeFutureSet.add(cakeFuture);
		}		
		System.out.println("干些别的...");
		TimeUnit.SECONDS.sleep(1);
		for(CakeFuture<Cake> future: cakeFutureSet) {
			new Thread(()->{
				try {
					System.out.println("取蛋糕..." + future.get());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}).start();;			
		}
	}

}
