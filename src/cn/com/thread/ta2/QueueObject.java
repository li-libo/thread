package cn.com.thread.ta2;

public class QueueObject {

	private boolean isNotified = false;

	public synchronized void doWait() throws InterruptedException {
		this.isNotified = false;
		while (!isNotified) {
			this.wait();
		}
	}

	public synchronized void doNotify() {
		this.isNotified = true;
		this.notify();
	}

	public boolean equals(Object o) {
		return this == o;
	}

}
