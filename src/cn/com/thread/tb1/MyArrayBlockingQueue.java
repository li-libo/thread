package cn.com.thread.tb1;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MyArrayBlockingQueue<E> {
	
	private final int capacity;
	
	private final Object[] array;
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private final Condition offerCondition = lock.newCondition();
	
	private final Condition pollCondition = lock.newCondition();
	
	private int size;
	
	private int offerIndex;
	
	private int pollIndex;
	
	public void offer(E e) throws InterruptedException {
		try {
			lock.lock();
			while(size >= capacity) {
				offerCondition.await();
			}
			array[offerIndex] = e;
			offerIndex++;
			size++;
			if(offerIndex == capacity) {
				offerIndex = 0;
			}
			pollCondition.signalAll();
		}finally {
			lock.unlock();
		}
	}
	
	public E poll() throws InterruptedException {
		try {
			lock.lock();
			while(size <= 0) {
				pollCondition.await();
			}
			@SuppressWarnings("unchecked")
			E value = (E) array[pollIndex];
			pollIndex++;
			size--;
			if(pollIndex == capacity) {
				pollIndex = 0;
			}
			offerCondition.signalAll();
			return value;
		}finally {
			lock.unlock();
		}
	}
	
	public int size() {
		return size;
	}
	
	public MyArrayBlockingQueue(int capacity) {
		this.capacity = capacity;
		array = new Object[capacity];
	}

	public static void main(String[] args) {
		MyArrayBlockingQueue<String> myArrayBlockingQueue = new MyArrayBlockingQueue<>(10);
		AtomicInteger atomicInteger = new AtomicInteger();
		for(int numOfOffer = 0; numOfOffer < 3; numOfOffer++) {
			final int j = numOfOffer;
			new Thread(()->{	
				while(true) {
					try {
						String id = atomicInteger.incrementAndGet() + "";
						myArrayBlockingQueue.offer(id);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}				
			}, "offer-" + j).start();
		}
		
		Set<String> idSet = Collections.synchronizedSet(new HashSet<>());
		for(int numOfPoll = 0; numOfPoll < 5; numOfPoll++) {
			final int j = numOfPoll;
			new Thread(()->{
				while(true) {
					try {
						String id = myArrayBlockingQueue.poll();
						if(!idSet.add(id)) {
							System.out.println("出现重复数据: id = " + id);
						}
					}catch(Exception e) {
						e.printStackTrace();
					}
				}
			}, "poll-" +j).start();
		}
		
		new Thread(()->{
			while(true) {
				System.out.println("queue size = " + myArrayBlockingQueue.size());
			}
		}, "size").start();
	}

}
