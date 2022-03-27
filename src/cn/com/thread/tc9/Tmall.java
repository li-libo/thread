package cn.com.thread.tc9;

public class Tmall<E> implements Shop<E>{
	
	private final Object lock = new Object();
	
	private final int capacity;
	
	private Object[] array;
	
	private int size;
	
	private int putIndex;
	
	private int takeIndex;
	
	public Tmall(int capacity) {
		this.capacity = capacity;
		array = new Object[capacity];
	}

	@Override
	public void put(E e) throws Exception {
		synchronized(lock) {
			while(size >= capacity) {
				lock.wait();
			}
			array[putIndex] = e;
			putIndex++;
			size++;
			if(putIndex == capacity) {
				putIndex = 0;
			}
			lock.notifyAll();
		}
	}

	@Override
	public E take() throws Exception {
		synchronized (lock) {
			while(size <= 0) {
				lock.wait();
			}
			@SuppressWarnings("unchecked")
			E oldValue = (E) array[takeIndex];
			takeIndex++;
			size--;
			if(takeIndex == capacity) {
				takeIndex = 0;
			}
			lock.notifyAll();
			return oldValue;
		}
	}

	@Override
	public int size() throws Exception {
		return size;
	}

}
