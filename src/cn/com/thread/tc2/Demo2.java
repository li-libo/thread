package cn.com.thread.tc2;

public class Demo2 {
	
	private int a;
	private boolean flag;
	
	public void writer () {
		// 这两个数据之间没有数据依赖性，因此处理器会对这两行代码进行指令重排序
		a = 1;
		flag = true;
	}
	
	public void reader () {
		if(flag) { // 此处也会有重排序: 也可能会先执行b = a + 1, 在判断flag
			int b = a + 1; // 此处由于指令重排序，a未必=1
			System.out.println(b);
		}
	}

	public static void main(String[] args) {
		Demo2 demo2 = new Demo2();		
		new Thread(() -> demo2.writer()).start();
		new Thread(() -> demo2.reader()).start();
	}
}
