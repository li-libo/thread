package cn.com.thread.tc9;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PingDuoDuo<E> implements Shop<E>{
	
	private final int capacity;
	
	private Object[] array;
	
	private int size;
	
	private int putIndex;
	
	private int takeIndex;
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private final Condition fullCondition = lock.newCondition();
	
	private final Condition emptyCondition = lock.newCondition();
	
	public PingDuoDuo(int capacity) {
		this.capacity = capacity;
		array = new Object[capacity];
	}

	@Override
	public void put(E e) throws Exception {
		try {
			lock.lock();
			while(size >= capacity) {
				fullCondition.await();
			}
			array[putIndex] = e;
			size++;
			putIndex++;
			if(putIndex == capacity) {
				putIndex = 0;
			}
			emptyCondition.signalAll();
		}finally {
			lock.unlock();
		}
	}

	@Override
	public E take() throws Exception {
		try {
			lock.lock();
			while(size <= 0) {
				emptyCondition.await();
			}
			@SuppressWarnings("unchecked")
			E oldValue = (E) array[takeIndex];
			size--;
			takeIndex++;
			if(takeIndex == capacity) {
				takeIndex = 0;
			}
			fullCondition.signalAll();
			return oldValue;
		}finally {
			lock.unlock();
		}
	}

	@Override
	public int size() throws Exception {
		return size;
	}

	@Override
	public String toString() {
		return "PingDuoDuo [capacity=" + capacity + ", size=" + size + "]";
	}

}
