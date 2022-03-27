package cn.com.thread.tc9;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class JingDong<E> implements Shop<E>{

	private final int capacity;
	
	private final BlockingQueue<E> blockingQueue;
	
	public JingDong(int capacity){
		this.capacity = capacity;		
		this.blockingQueue = new LinkedBlockingQueue<>(capacity);
	}
	
	@Override
	public void put(E e) throws InterruptedException {
		blockingQueue.put(e);
	}

	@Override
	public E take() throws InterruptedException {
		return blockingQueue.take();
	}

	@Override
	public int size() {
		return blockingQueue.size();
	}

	@Override
	public String toString() {
		return "JingDong [capacity=" + capacity + "]";
	}

}
