package cn.com.thread.tb9;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CakeFuture<E> {

	private final ReentrantLock lock = new ReentrantLock();

	private final Condition addCondition = lock.newCondition();

	private final Condition takeCondtion = lock.newCondition();

	private E cake;

	public void setCake(E e) throws InterruptedException {
		try {
			lock.lock();
			while (cake != null) {
				addCondition.await();
			}
			cake = e;
			takeCondtion.signalAll();
		} finally {
			lock.unlock();
		}
	}
	
	public E get() throws InterruptedException {
		try {
			lock.lock();
			while(cake == null) {
				takeCondtion.await();
			}
			E e = cake;
			cake = null;
			addCondition.signalAll();
			return e;
		}finally {
			lock.unlock();
		}
	}

}
