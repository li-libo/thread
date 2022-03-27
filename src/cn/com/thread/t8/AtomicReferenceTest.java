package cn.com.thread.t8;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceTest {
	
	public static final int numOfTest = 10;
	
	public static final int numOfRetry = 20;
	
	private static AtomicInteger newConnectionCount = new AtomicInteger();
	
	private final AtomicReference<Connection> connectionAtomicReference = new AtomicReference<>();
	
	public static void main(String[] args) {
		AtomicReferenceTest atomicReferenceTest = new AtomicReferenceTest();
		Set<Connection> safeConnectionSet = Collections.synchronizedSet(new HashSet<>());
		for(int i = 0; i < numOfTest; i++) {
			new Thread(()->{
				for(int j = 0; j < 100000; j++) {
					Connection newConnection = atomicReferenceTest.getNewConnection();
					if(!safeConnectionSet.add(newConnection)) {
						System.out.println("重复新连接!!!" + newConnection);
					}
				}
			}, "test-" + i).start();
		}
		while(Thread.activeCount() > 1) {
			
		}
		System.out.println("new Connection count = " + newConnectionCount.get());
	}
		
	public Connection getNewConnection() {
		// 失败重试机制
		for (int i = 0; i < numOfRetry; i++) {
			Connection oldConnection = connectionAtomicReference.get();
			if(oldConnection == null) {
				Connection newConnection = new Connection();
				newConnectionCount.incrementAndGet();
				connectionAtomicReference.compareAndSet(null, newConnection);
			}else if(connectionAtomicReference.compareAndSet(oldConnection, null)) {
				return oldConnection;
			}
		}
		return null;		
	}

}
