package cn.com.thread.tb2;

public class Demo {

	public void a(Thread joinThread) {
		System.out.println("方法a执行了...");
		joinThread.start();
		try {
			// 当线程终止时，会调用自身的notifyAll方法，会通知所有等待在该线程对象上的线程。
			/*
			 * public final synchronized void join(long millis)
			    throws InterruptedException {
			        long base = System.currentTimeMillis();
			        long now = 0;
			
			        if (millis < 0) {
			            throw new IllegalArgumentException("timeout value is negative");
			        }
			
			        if (millis == 0) {
			            while (isAlive()) { // judge this Thread is Alive
			                wait(0); // the current Thread wait
			            }
			        } else {
			            while (isAlive()) {
			                long delay = millis - now;
			                if (delay <= 0) {
			                    break;
			                }
			                wait(delay);
			                now = System.currentTimeMillis() - base;
			            }
			        }
			    }
			 */
			joinThread.join(); 
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("a方法执行完毕...");

	}

	public void b() {
		System.out.println("加塞线程开始执行....");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("加塞线程执行完毕...");		
	}

	public static void main(String[] args) {
		Demo demo = new Demo();
		Thread joinThread = new Thread(new Runnable() {
			@Override
			public void run() {
				demo.b();
			}
		}, "joinThread");

		new Thread(new Runnable() {
			@Override
			public void run() {
				demo.a(joinThread);
			}
		}, "test").start();
	}

}
