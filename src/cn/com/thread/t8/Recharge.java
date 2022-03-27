package cn.com.thread.t8;

import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

/**
在介绍 AtomicReference 的同时，我希望同时提出一个有关原子操作的逻辑上的不足:
	线程判断被修改对象是否可以正确写入的条件是对象的当前值和期望是否一致。这个逻辑从一般意义上来说是正确的。
但有可能出现一个小小的例外，就是当你获得对象当前数据后，在准备修改为新值前，对象的值被其他线程连续修改了2次，
而经过这2次修改后，对象的值又恢复为旧值。这样，当前线程就无法正确判断这个对象究竟是否被修改过。
 * @author lilibo
 *
 */
public class Recharge {

	/*
	 * 打一个比方，如果有一家蛋糕店，为了挽留客户，决定为贵宾卡里余额小于20元的客户一次性赠送20元，
	 * 刺激消费者充值和消费。但条件是，每一位客户只能被赠送一次。
	 */
	public static void main(String[] args) {
		AtomicReference<Integer> moneyRef = new AtomicReference<>(11);
		new Thread(()->{
			while(true) {
				Integer oldMoney = moneyRef.get();
				if(oldMoney >= 20) {
					System.out.println("无需充值!!!");
				}
				if(oldMoney < 20 && moneyRef.compareAndSet(oldMoney, oldMoney + 20)) {
					System.out.println("充值成功!!!");
				}
			}
		}, "recharge-thread").start();
		
		Random random = new Random();
		new Thread(()->{
			while(true) {
				Integer oldMoney = moneyRef.get();
				int price = random.nextInt(10);
				if(oldMoney >= price && moneyRef.compareAndSet(oldMoney, oldMoney - price)) {
					System.out.println("消费成功!!! price = " + price);
				}
			}
		}, "consume-thread").start();
	}

}
