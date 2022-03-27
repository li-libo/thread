package cn.com.thread.tc6;

/**
final的内存语义:
	写final域的重排序规则
	读final域的重排序规则
	final域为静态类型
	final域为抽象类型
 
写final域的重排序规则:
 	写final域的重排序的规则禁止把final域的写重排序到构造方法之外。
	Java的内存模型禁止编译器把final域的写重排序到构造方法之外
	编译器会在final域的写之后，在构造方法执行完毕之前，插入一个内存屏障StoreStore，保证处理器把final域的写操作在构造方法中执行。
		LoadLoad   load1 loadload load2
	*	StoreStore store1 storestore store2
		LoadStore
		StoreLoad

读final域的重排序规则:
	在一个线程中，初次读对象引用和初次读该对象所包含的final域，Java内存模型禁止处理器重排序这两个操作。

Final域为抽象类型:
	在构造方法内对一个final引用的对象的成员域的写入，与随后在构造方法外把这个被构造对象的引用赋值给一个引用变量，这两个操作之间不能重排序。

 * @author lilibo
 *
 */
public class FinalDemo {
	
	private int a;
	private final static int b ;
	
	static  {
		b = 10;
	}
	
	public FinalDemo() { // 1
	// b = 20; // 2
		a = 10; // 3
		// 4
	} 
	
	private FinalDemo demo;
	
	public void w() { // 5
		demo = new FinalDemo(); // 6
	} // 
	
	@SuppressWarnings({ "unused", "static-access" })
	public void r() {
		FinalDemo d = demo; // 7
		int temp1 = d.a; // 8
		int temp2 = d.b; // 9
	}
	
}
